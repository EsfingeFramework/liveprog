package org.esfinge.liveprog.db;

import java.util.List;

import org.esfinge.liveprog.reflect.ClassInfo;

/**
 * <p>
 * Interface para a persist�ncia de classes din�micas.
 * </p>
 * <p><i>
 * Interface to be implemented by classes to provide LiveClasses persistence.
 * </i></p>
 */
public interface ILiveClassPersistence
{
	/**
	 * <p>
	 * Obt�m as informa��es da classe din�mica.
	 * </p>
	 * <p><i>
	 * Gets the persisted LiveClass information.
	 * </i></p>
	 * 
	 * @param liveClassName - nome da classe din�mica
	 * <br><i>name of the persisted LiveClass</i>
	 * @param safeMode - <i>true</i> para recuperar as informa��es da classe din�mica do modo seguro, 
	 * <i>false</i> para as informa��es do modo normal
	 * <br><i>true to get the information of the LiveClass on its safe mode, false for the information of the standard mode</i>
	 * @return as informa��es da classe din�mica persistida, ou <i>null</i> se nenhuma vers�o for encontrada
	 * <br><i>the information of the persisted LiveClass, or null if none is found</i> 
	 * @throws Exception em caso de erros ao recuperar as informa��es da classe din�mica persistida
 	 * <br><i>if an error occurs when retrieving the information of the persisted LiveClass</i>
	 */
	public ClassInfo getLiveClassInfo(String liveClassName, boolean safeMode) throws Exception;
	
	/**
	 * <p>
	 * Persiste as informa��es da classe din�mica.
	 * </p>
	 * <p><i>
	 * Persists the LiveClass information.
	 * </i></p>
	 * 
	 * @param liveClassName - nome da classe din�mica
	 * <br><i>name of the LiveClass to be persisted</i>
	 * @param liveClassInfo - informa��es da classe din�mica a ser persistida
	 * <br><i>information of the LiveClass to be persisted</i>
	 * @throws Exception em caso de erros ao persistir a classe din�mica
 	 * <br><i>if an error occurs when persisting the LiveClass</i>
	 */
	public void saveLiveClassInfo(String liveClassName, ClassInfo liveClassInfo) throws Exception;
	
	
	/**
	 * <p>
	 * Obt�m as informa��es de versionamento da classe din�mica.
	 * <br>
	 * Caso a classe ainda n�o tenha sido persistida, os m�todos de versionamento de {@link ILiveClassVersionInfo} 
	 * devem retornar o valor inteiro <b>-1</b>.
	 * </p>
	 * <p><i>
	 * Gets the LiveClass versioning information.
	 * <br>
	 * If the LiveClass has not yet been persisted, all versioning methods of {@link ILiveClassVersionInfo} must return 
	 * the integer value <b>-1</b>.
	 * </i></p>
	 * 
	 * @param liveClassName - nome da classe din�mica
	 * <br><i>name of the persisted LiveClass</i>
	 * @return as informa��es de versionamento da classe din�mica informada
	 * <br><i>the versioning information of the specified LiveClass</i>
	 * @throws Exception em caso de erros ao recuperar as informa��es de versionamento da classe din�mica
 	 * <br><i>if an error occurs when retrieving the versioning information of the specified LiveClass</i>
	 * @see org.esfinge.liveprog.db.ILiveClassVersionInfo
	 */
	public ILiveClassVersionInfo getLiveClassVersionInfo(String liveClassName) throws Exception;

	/**
	 * <p>
	 * Obt�m as informa��es de versionamento de todas as classes din�micas persistidas.
	 * </p>
	 * <p><i>
	 * Gets the versioning information of all persisted LiveClasses.
	 * </i></p>
	 * 
	 * @return as informa��es de versionamento de todas as classes din�micas persistidas
	 * <br><i>the versioning information of all persisted LiveClasses</i>
	 * @throws Exception em caso de erros ao recuperar as informa��es de versionamento das classes din�micas
 	 * <br><i>if an error occurs when retrieving the versioning information of the persisted LiveClasses</i>
	 * @see org.esfinge.liveprog.db.ILiveClassVersionInfo
	 */
	public List<ILiveClassVersionInfo> getAllLiveClassesVersionInfo() throws Exception;
	
	/**
	 * <p>
	 * Aceita a vers�o atual da classe din�mica, promovendo-a como uma vers�o segura. 
	 * </p>
	 * <p><i>
	 * Commits the current LiveClass version, promoting it as a safe version.
	 * </i></p>
	 * 
	 * @param liveClassName - nome da classe din�mica
	 * <br><i>name of the persisted LiveClass</i>
	 * @return <i>true</i> se a vers�o atual p�de ser promovida como uma vers�o segura, <i>false</i> caso contr�rio 
	 * (i.e se a vers�o segura e a atual j� forem iguais)
	 * <br><i>true if the current version was correctly promoted as a safe version, false otherwise 
	 * (i.e if both versions are already the same)</i>
	 * @throws Exception em caso de erros ao persistir o versionamento da classe din�mica
 	 * <br><i>if an error occurs when persisting the LiveClass versioning</i>
 	 * @see org.esfinge.liveprog.db.ILiveClassVersionManager
	 */
	public boolean commitLiveClass(String liveClassName) throws Exception;

	/**
	 * <p>
	 * Descarta a vers�o atual da classe din�mica, retrocendo-a para a vers�o anterior.
	 * </p>
	 * <p><i>
	 * Rolls back the current LiveClass version, reverting it to its previous version.
	 * </i></p>
	 * 
	 * @param liveClassName - nome da classe din�mica
	 * <br><i>name of the persisted LiveClass</i>
	 * @return <i>true</i> se a vers�o atual p�de ser retrocedida para a vers�o anterior, <i>false</i> caso contr�rio 
	 * (i.e se a vers�o atual for a primeira vers�o)
	 * <br><i>true if the current version was correctly rolled back to its previous version, false otherwise 
	 * (i.e if the current version is in its first version)</i>
	 * @throws Exception em caso de erros ao persistir o versionamento da classe din�mica
 	 * <br><i>if an error occurs when persisting the LiveClass versioning</i>
 	 * @see org.esfinge.liveprog.db.ILiveClassVersionManager
	 */
	public boolean rollbackLiveClass(String liveClassName) throws Exception;
}
