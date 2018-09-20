package org.esfinge.liveprog.db;

/**
 * <p>
 * Interface para o gerenciamento das versões de classes dinâmicas.
 * <p><i>
 * Interface to be implemented by classes to provide LiveClasses versioning management.
 * </i>
 * 
 * @see org.esfinge.liveprog.db.ILiveClassVersionInfo
 */
public interface ILiveClassVersionManager
{
	/**
	 * <p>
	 * Registra o observador para que seja notificado sobre versionamento de classes dinâmicas.
	 * <p><i>
	 * Registers the observer interested on being notified about LiveClasses versioning.
	 * </i> 
	 * 
	 * @param observer observador a ser notificado sobre versionamento de classes dinâmicas
	 * <br><i>observer to be notified about LiveClasses versioning</i>
	 * @see org.esfinge.liveprog.db.ILiveClassVersionObserver
	 */	
	public void addObserver(ILiveClassVersionObserver observer);
	
	/**
	 * <p>
	 * Remove o observador da lista de observadores de versionamento de classes dinâmicas.
	 * <p><i>
	 * Removes the observer from the list of registered observers of LiveClass versioning.
	 * </i> 
	 * 
	 * @param observer observador a ser removido da lista de observadores registrados
	 * <br><i>observer to be removed from the list of registered observers</i>
	 * @see org.esfinge.liveprog.db.ILiveClassVersionObserver
	 */
	public void removeObserver(ILiveClassVersionObserver observer);
}
