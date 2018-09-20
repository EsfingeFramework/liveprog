package org.esfinge.liveprog.db;

/**
 * <p>
 * Descritor de versionamento de classes din�micas.
 * </p>
 * <p><i>
 * Descriptor for LiveClasses versioning.
 * </i></p>
 */
public interface ILiveClassVersionInfo
{
	/**
	 * <p>
	 * Obt�m o nome da classe din�mica.
	 * </p>
	 * <p><i>
	 * Gets the LiveClass name.
	 * </i></p>
	 * 
	 * @return o nome da classe din�mica
	 * <br><i>the name of the LiveClass</i>
	 */
	public String getClassName();
	
	/**
	 * <p>
	 * Obt�m a vers�o atual da classe din�mica.
	 * </p>
	 * <p><i>
	 * Gets the LiveClass current version.
	 * </i></p>
	 * 
	 * @return a vers�o atual da classe din�mica
	 * <br><i>the current version of the LiveClass</i>
	 */
	public int getCurrentVersion();
	
	/**
	 * <p>
	 * Obt�m a vers�o de modo seguro da classe din�mica.
	 * </p>
	 * <p><i>
	 * Gets the LiveClass safe mode version.
	 * </i></p>
	 * 
	 * @return a vers�o atual de modo seguro da classe din�mica 
	 * <br><i>the current safe mode version of the LiveClass</i>
	 */
	public int getSafeModeVersion();
}
