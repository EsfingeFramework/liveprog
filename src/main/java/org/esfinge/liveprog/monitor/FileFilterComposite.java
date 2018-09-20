package org.esfinge.liveprog.monitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.esfinge.liveprog.util.Utils;

/**
 * <p>
 * Permite encadear um conjunto de filtros para os arquivos monitorados.
 * <p><i>
 * Allows to chain a set of file filters.
 * </i> 
 * 
 * @see org.esfinge.liveprog.monitor.ILiveClassFileFilter
 */
public class FileFilterComposite implements ILiveClassFileFilter
{
	// lista de filtros encadeados
	private List<ILiveClassFileFilter> filters;
	
	
	/**
	 * <p>
	 * Constrói um novo conjunto de filtro de arquivos.
	 * <p><i>
	 * Constructs a new composite of file filters.
	 * </i>
	 * 
	 * @param filters conjunto de filtros para validar os arquivos monitorados 
	 * <br><i>set of file filters to validate the monitored files</i>
	 */
	public FileFilterComposite(ILiveClassFileFilter... filters)
	{
		this.filters = new ArrayList<ILiveClassFileFilter>();
		this.addFilter(filters);
	}
	
	/**
	 * <p>
	 * Adiciona os filtros especificados para validar os arquivos monitorados.
	 * <p><i>
	 * Adds the specified file filters to validate the monitored files.
	 * </i>
	 * 
	 * @param filters conjunto de filtros para validar os arquivos monitorados 
	 * <br><i>set of file filters to validate the monitored files</i>
	 */
	public void addFilter(ILiveClassFileFilter... filters)
	{
		Utils.addToCollection(this.filters, filters);
	}

	@Override
	public boolean acceptFile(File file)
	{
		boolean accept = !this.filters.isEmpty();
		
		for (int i = 0; i < this.filters.size() && accept; i++)
			accept &= filters.get(i).acceptFile(file);
		
		return ( accept );
	}
}
