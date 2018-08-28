package org.esfinge.liveprog.monitor;

import java.io.File;

/**
 * Interface para validar os arquivos monitorados.
 */
public interface ILiveClassFileValidator
{
	/**
	 * Verifica se o formato do arquivo monitorado eh valido.
	 * 
	 * @param file o novo arquivo encontrado pelo monitor
	 * @return <b>true</b> se for um arquivo num formato valido, <b>false</b> caso contrario
	 */
	public boolean isValid(File file);
}
