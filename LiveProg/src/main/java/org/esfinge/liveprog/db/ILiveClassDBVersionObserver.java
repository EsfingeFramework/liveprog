package org.esfinge.liveprog.db;

/**
 * Interface para ser notificado quando uma versao de classe dinamica for alterada no banco de dados.
 */
public interface ILiveClassDBVersionObserver
{
	/**
	 * Notifica que uma versao de testes da classe dinamica foi promovida para versao de producao.
	 * @param className o nome da classe dinamica
	 */
	public void liveClassCommitted(String className);
	
	/**
	 * Notifica que uma versao de producao da classe dinamica retrocedeu para a versao anterior.
	 * 
	 * @param className o nome da classe dinamica
	 */
	public void liveClassRolledBack(String className);
}
