package org.esfinge.liveprog.monitor;

import java.io.File;

/**
 * <p>
 * Interface a ser implementada pelas classes interessadas em ser notificadas 
 * quando arquivos de classes din�micas atualizadas forem encontrados.
 * </p>
 * <p><i>
 * Interface to be implemented by classes interested on being notified when updated LiveClass files are found.
 * </i></p>
 * 
 * @see org.esfinge.liveprog.monitor.ILiveClassFileMonitor
 */
public interface ILiveClassFileMonitorObserver
{
	/**
	 * <p>
	 * Recebe a notifica��o de que o arquivo de uma classe din�mica atualizada foi encontrado.
	 * </p>
	 * <p><i>
	 * Gets notified that a updated LiveClass file was found.
	 * 
	 * @param liveClassFile - arquivo da classe din�mica atualizada
	 * <br><i>the updated LiveClass file</i>
	 */
	public void liveClassFileUpdated(File liveClassFile);
}
