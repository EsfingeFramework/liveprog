package org.esfinge.liveprog.monitor;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Filtra os arquivos monitorados pelo tipo de extensao. 
 */
public class FileExtensionMonitorFilter implements IMonitorFileFilter
{
	// os tipos de arquivos aceitos
	private Set<String> validExtensions;
	
	
	/**
	 * Cria um filtro de arquivos pelos tipos de extensao.
	 * 
	 * @param fileExtensions as extensoes dos tipos de arquivos aceitos;
	 * nao colocar o ponto (.), somente a extensao!
	 */
	public FileExtensionMonitorFilter(String... fileExtensions)
	{
		this.validExtensions = new HashSet<String>(Arrays.asList(fileExtensions));
	}

	@Override
	public boolean acceptFile(File file)
	{
		if ( file != null && file.isFile() )
		{
			String fileName = file.getName();
			int pos = fileName.lastIndexOf('.');
			if ( pos != -1)
				return ( this.validExtensions.contains(fileName.substring(pos+1)) );
		}
		
		return ( false );
	}
}
