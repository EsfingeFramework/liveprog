package org.esfinge.liveprog.db;

/**
 * Interface para gerenciamento de versoes de classes dinamicas persistidas. 
 */
public interface ILiveClassDBVersionManager
{
	/**
	 * Adiciona um observador para ser notificado quando uma versao de classe dinamica 
	 * for alterada no banco de dados.
	 * 
	 * @param observer interessado na notificacao quando uma versao de classe dinamica 
	 * for alterada no banco de dados
	 * @see org.esfinge.liveprog.db.ILiveClassDBVersionObserver
	 */	
	public void addObserver(ILiveClassDBVersionObserver observer);
	
	/**
	 * Remove um observador da lista de observadores.
	 * 
	 * @param observer observador a ser removido da lista de observadores
	 * @see org.esfinge.liveprog.db.ILiveClassDBVersionObserver
	 */
	public void removeObserver(ILiveClassDBVersionObserver observer);
}
