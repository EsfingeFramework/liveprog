package org.esfinge.liveprog;

/**
 * <p>
 * Interface a ser implementada pelas classes interessadas em ser notificadas sobre atualizações de classes dinâmicas.
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
	 * Recebe a notificação de que a classe dinâmica foi atualizada para uma nova versão.
	 * <p><i>
	 * Gets notified that the LiveClass was updated to a new version.
	 * </i>
	 * 
	 * @param liveClassName nome da classe dinâmica que foi atualizada
	 * <br><i>name of the updated LiveClass</i>
	 * @param newLiveClass nova versão da classe dinâmica
	 * <br><i>updated version of the LiveClass</i>
	 */
	public void liveClassUpdated(String liveClassName, Class<?> newLiveClass);
}
