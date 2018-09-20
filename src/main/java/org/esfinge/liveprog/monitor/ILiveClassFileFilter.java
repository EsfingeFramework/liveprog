package org.esfinge.liveprog.monitor;

import java.io.File;

/**
 * <p>
 * Interface para filtrar os arquivos monitorados.
 * <p><i>
 * Interface to filter the monitored files.
 * </i>
 * 
 * @see org.esfinge.liveprog.monitor.ILiveClassFileMonitor
 */
public interface ILiveClassFileFilter
{
	/**
	 * <p>
	 * Verifica se o arquivo encontrado � v�lido.
	 * <p><i>
	 * Checks if the specified file is valid.
	 * </i>
	 * 
	 * @param file arquivo encontrado pelo monitor
	 * <br><i>the file found by monitor</i>
	 * @return <i>true</i> se o arquivo encontrado for v�lido, <i>false</i> caso contr�rio
	 * <br><i>true if the file found is valid, false otherwise</i>
	 */
	public boolean acceptFile(File file);
}
