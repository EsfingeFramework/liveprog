package org.esfinge.liveprog.db;

/**
 * <p>
 * Descritor de versionamento de classes dinâmicas.
 * <p><i>
 * Descriptor for LiveClasses versioning.
 * </i>
 */
public interface ILiveClassVersionInfo
{
	/**
	 * <p>
	 * Obtém o nome da classe dinâmica.
	 * <p><i>
	 * Gets the LiveClass name.
	 * </i>
	 * 
	 * @return o nome da classe dinâmica
	 * <br><i>the name of the LiveClass</i>
	 */
	public String getClassName();
	
	/**
	 * <p>
	 * Obtém a versão atual da classe dinâmica.
	 * <p><i>
	 * Gets the LiveClass current version.
	 * </i>
	 * 
	 * @return a versão atual da classe dinâmica
	 * <br><i>the current version of the LiveClass</i>
	 */
	public int getCurrentVersion();
	
	/**
	 * <p>
	 * Obtém a versão de modo seguro da classe dinâmica.
	 * <p><i>
	 * Gets the LiveClass safe mode version.
	 * </i>
	 * 
	 * @return a versão atual de modo seguro da classe dinâmica 
	 * <br><i>the current safe mode version of the LiveClass</i>
	 */
	public int getSafeModeVersion();
}
