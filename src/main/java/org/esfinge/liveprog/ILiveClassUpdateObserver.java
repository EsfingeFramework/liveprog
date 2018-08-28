package org.esfinge.liveprog;

import org.esfinge.liveprog.instrumentation.ClassInfo;

/**
 * Interface para ser notificado quando novas versoes de classes dinamicas forem criadas.
 */
public interface ILiveClassUpdateObserver
{
	/**
	 * Notifica que uma nova versao de classe dinamica foi criada.
	 * 
	 * @param liveClassName o nome da classe dinamica original
	 * @param newLiveClassInfo as informacoes da nova versao da classe dinamica
	 */
	public void liveClassUpdated(String liveClassName, ClassInfo newLiveClassInfo);
}
