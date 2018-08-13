package org.esfinge.liveprog.monitor;

import java.io.File;

/**
 * Interface para ser notificado quando uma nova versao de uma classe dinamica for encontrada.
 */
public interface IMonitorObserver
{
	/**
	 * Notifica que o arquivo atualizado de uma classe dinamica foi encontrado, 
	 * para que a classe possa ser recarregada.
	 * 
	 * @param classFile o arquivo da classe atualizada, para que possa ser recarregada
	 */
	public void classFileUpdated(File classFile);
}
