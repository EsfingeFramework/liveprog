package org.esfinge.liveprog.monitor;

import java.io.File;

/**
 * Interface para filtrar os tipos de arquivos a serem monitorados.
 */
public interface IMonitorFileFilter
{
	/**
	 * Verifica se o arquivo eh apto a ser monitorado.
	 * 
	 * @param file o novo arquivo encontrado pelo monitor
	 * @return <b>true</b> se for um arquivo apto a ser monitorado, <b>false</b> caso contrario
	 */
	public boolean acceptFile(File file);
}
