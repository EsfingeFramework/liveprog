package org.esfinge.liveprog.monitor;

/**
 * Interface para monitoramento de arquivos de novas versoes de classes dinamicas.
 */
public interface ILiveClassFileMonitor
{
	/**
	 * Inicia o monitoramento de novas versoes de classes dinamicas.
	 */
	public void start();
	
	/**
	 * Interrompe o monitoramento de novas versoes de classes dinamicas.
	 */
	public void stop();
	
	/**
	 * Termina o monitoramento de novas versoes de classes dinamicas.
	 */
	public void shutdown();
	
	/**
	 * Atribui o filtro dos tipos de arquivos a serem monitorados.
	 * 
	 * @param filter filtro para os tipos de arquivos a serem monitorados
	 * @see org.esfinge.liveprog.monitor.ILiveClassFileFilter
	 */
	public void setFileFilter(ILiveClassFileFilter filter);
	
	/**
	 * Atribui o validador do formato dos arquivos monitorados.
	 * 
	 * @param validator validador para os arquivos encontrados pelo monitor
	 * @see org.esfinge.liveprog.monitor.ILiveClassFileValidator
	 */
	public void setFileValidator(ILiveClassFileValidator validator);
	
	/**
	 * Adiciona um observador para ser notificado quando arquivos de 
	 * novas versoes de classes dinamicas forem encontrados.
	 * 
	 * @param observer interessado na notificacao quando novos arquivos forem encontrados
	 * @see org.esfinge.liveprog.monitor.ILiveClassFileMonitorObserver
	 */
	public void addObserver(ILiveClassFileMonitorObserver observer);
	
	/**
	 * Remove um observador da lista de observadores.
	 * 
	 * @param observer observador a ser removido da lista de observadores
	 * @see org.esfinge.liveprog.monitor.ILiveClassFileMonitorObserver
	 */
	public void removeObserver(ILiveClassFileMonitorObserver observer);
}
