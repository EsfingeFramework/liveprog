package org.esfinge.liveprog.monitor;

/**
 * Interface para monitoramento de novas versoes de classes dinamicas.
 */
public interface IMonitor
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
	 * @see IMonitorFileFilter
	 */
	public void setFileFilter(IMonitorFileFilter filter);
	
	/**
	 * Atribui o validador do formato dos arquivos monitorados.
	 * 
	 * @param validator validador para os arquivos encontrados pelo monitor
	 * @see IMonitorFileValidator
	 */
	public void setFileValidator(IMonitorFileValidator validator);
	
	/**
	 * Atribui o observador a ser notificado quando uma nova versao 
	 * de uma classe dinamica for encontrada.
	 * 
	 * @param observer interessado na notificacao de novas versoes de classes dinamicas, 
	 * para que possam ser recarregadas
	 * @see IMonitorObserver
	 */
	public void setObserver(IMonitorObserver observer);
}
