package org.esfinge.liveprog.db;

/**
 * <p>
 * Interface a ser implementada pelas classes interessadas em ser notificadas sobre o controle de versões de uma classe dinâmica.
 * <p><i>
 * Interface to be implemented by classes interested on being notified about LiveClasses versioning. 
 * </i>
 */
public interface ILiveClassVersionObserver
{
	/**
	 * <p>
	 * Recebe a notificação de que a versão da classe dinâmica foi aceita.
	 * <p><i>
	 * Gets notified that the LiveClass version was committed.
	 * </i>
	 * 
	 * @param liveClassName nome da classe dinâmica cuja versão foi aceita
	 * <br><i>name of the LiveClass whose version was commited</i>
	 */
	public void liveClassCommitted(String liveClassName);
	
	/**
	 * <p>
	 * Recebe a notificação de que a versão da classe dinâmica foi revertida.
	 * <p><i>
	 * Gets notified that the LiveClass version was rolled back.
	 * </i>
	 * 
	 * @param liveClassName nome da classe dinâmica cuja versão foi revertida
	 * <br><i>name of the LiveClass whose version was rolled back</i>
	 */
	public void liveClassRolledBack(String liveClassName);
}
