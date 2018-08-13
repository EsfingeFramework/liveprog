package org.esfinge.liveprog;

/**
 * Interface para ser notificado quando uma classe dinamica for atualizada.
 */
public interface ILiveClassObserver
{
	/**
	 * Notifica que a classe dinamica foi atualizada.
	 * 
	 * @param newLiveClass a nova versao da classe dinamica
	 */
	public void classReloaded(Class<?> newLiveClass);
}
