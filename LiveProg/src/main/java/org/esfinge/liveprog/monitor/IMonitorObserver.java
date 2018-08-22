package org.esfinge.liveprog.monitor;

import java.io.File;

/**
 * Interface para ser notificado quando arquivos de novas versoes de classes dinamicas forem encontrados.
 */
public interface IMonitorObserver
{
	/**
	 * Notifica que um arquivo de uma nova versao de classe dinamica foi encontrado.
	 * 
	 * @param classFile o arquivo da nova versao da classe dinamica, para que possa ser recarregada
	 */
	public void classFileUpdated(File classFile);
}
