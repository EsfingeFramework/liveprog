package org.esfinge.liveprog;

/**
 * <p>
 * Interface a ser implementada pelas classes interessadas em ser notificadas sobre atualiza��es de classes din�micas.
 * <p><i>
 * Interface to be implemented by classes interested on being notified about LiveClasses updates.
 * </i>
 * 
 * @see org.esfinge.liveprog.annotation.LiveClass
 */
public interface ILiveClassObserver
{
	/**
	 * <p>
	 * Recebe a notifica��o de que a classe din�mica foi atualizada para uma nova vers�o.
	 * <p><i>
	 * Gets notified that the LiveClass was updated to a new version.
	 * </i>
	 * 
	 * @param liveClassName nome da classe din�mica que foi atualizada
	 * <br><i>name of the updated LiveClass</i>
	 * @param newLiveClass nova vers�o da classe din�mica
	 * <br><i>updated version of the LiveClass</i>
	 */
	public void liveClassUpdated(String liveClassName, Class<?> newLiveClass);
}
