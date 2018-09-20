package org.esfinge.liveprog.db;

/**
 * <p>
 * Interface a ser implementada pelas classes interessadas em ser notificadas sobre o controle de vers�es de uma classe din�mica.
 * </p>
 * <p><i>
 * Interface to be implemented by classes interested on being notified about LiveClasses versioning. 
 * </i></p>
 */
public interface ILiveClassVersionObserver
{
	/**
	 * <p>
	 * Recebe a notifica��o de que a vers�o da classe din�mica foi aceita.
	 * </p>
	 * <p><i>
	 * Gets notified that the LiveClass version was committed.
	 * </i></p>
	 * 
	 * @param liveClassName - nome da classe din�mica cuja vers�o foi aceita
	 * <br><i>name of the LiveClass whose version was commited</i>
	 */
	public void liveClassCommitted(String liveClassName);
	
	/**
	 * <p>
	 * Recebe a notifica��o de que a vers�o da classe din�mica foi revertida.
	 * </p>
	 * <p><i>
	 * Gets notified that the LiveClass version was rolled back.
	 * </i></p>
	 * 
	 * @param liveClassName - nome da classe din�mica cuja vers�o foi revertida
	 * <br><i>name of the LiveClass whose version was rolled back</i>
	 */
	public void liveClassRolledBack(String liveClassName);
}
