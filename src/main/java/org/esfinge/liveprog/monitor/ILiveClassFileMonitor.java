package org.esfinge.liveprog.monitor;

/**
 * <p>
 * Interface para o monitoramento de arquivos de classes dinâmicas atualizadas.
 * </p>
 * <p><i>
 * Interface for monitoring updated LiveClass files.
 * </i></p>
 */
public interface ILiveClassFileMonitor
{
	/**
	 * <p>
	 * Inicia o monitoramento de arquivos de classes dinâmicas atualizadas.
	 * </p>
	 * <i><p>
	 * Starts monitoring for updated LiveClass files.
	 * </i></p>
	 */
	public void start();
	
	/**
	 * <p>
	 * Interrompe o monitoramento de arquivos de classes dinâmicas atualizadas.
	 * </p>
	 * <i><p>
	 * Stops monitoring for updated LiveClass files.
	 * </i></p>
	 */
	public void stop();
	
	/**
	 * <p>
	 * Termina o monitoramento de arquivos de classes dinâmicas atualizadas.
	 * </p>
	 * <i><p>
	 * Shutdowns the monitoring for updated LiveClass files.
	 * </i></p>
	 */
	public void shutdown();
	
	/**
	 * <p>
	 * Atribui o filtro dos arquivos monitorados.
	 * </p>
	 * <i><p>
	 * Sets the monitored files filter.
	 * </i></p>
	 * 
	 * @param filter - filtro para os arquivos monitorados
	 * <br><i>the filter of monitored files</i>
	 * @see org.esfinge.liveprog.monitor.ILiveClassFileFilter
	 */
	public void setFileFilter(ILiveClassFileFilter filter);

	/**
	 * <p>
	 * Registra o observador para que seja notificado quando arquivos de classes dinâmicas atualizadas forem encontrados.
	 * </p>
	 * <p><i>
	 * Registers the observer interested on being notified when updated LiveClass files are found.
	 * </i></p> 
	 * 
	 * @param observer - observador a ser notificado quando novos arquivos forem encontrados
	 * <br><i>observer to be notified when updated LiveClass files are found</i>
	 * @see org.esfinge.liveprog.monitor.ILiveClassFileMonitorObserver
	 */	
	public void addObserver(ILiveClassFileMonitorObserver observer);
	
	/**
	 * <p>
	 * Remove o observador da lista de observadores de arquivos de classes dinâmicas atualizadas.
	 * </p>
	 * <p><i>
	 * Removes the observer from the list of registered observers of updated LiveClass files.
	 * </i></p> 
	 * 
	 * @param observer - observador a ser removido da lista de observadores registrados
	 * <br><i>observer to be removed from the list of registered observers</i>
	 * @see org.esfinge.liveprog.monitor.ILiveClassFileMonitorObserver
	 */
	public void removeObserver(ILiveClassFileMonitorObserver observer);
}
