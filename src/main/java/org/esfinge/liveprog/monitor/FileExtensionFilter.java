package org.esfinge.liveprog.monitor;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * Filtra os arquivos pelo tipo de extens�o.
 * <p><i>
 * Filters files by extension type.
 * </i> 
 */
public class FileExtensionFilter implements ILiveClassFileFilter
{
	// os tipos de arquivos aceitos
	private Set<String> validExtensions;
	
	
	/**
	 * <p>
	 * Constr�i um novo filtro de arquivos especificando as extens�es aceitas.
	 * <p><i>
	 * Constructs a new file filter by specifying the accepted extension types.
	 * </i>
	 * 
	 * @param fileExtensions extens�es dos tipos de arquivos aceitos
	 * <br><i>the accepted extension types</i>
	 */
	public FileExtensionFilter(String... fileExtensions)
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
