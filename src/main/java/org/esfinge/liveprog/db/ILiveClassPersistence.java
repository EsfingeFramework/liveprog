package org.esfinge.liveprog.db;

import java.util.List;

import org.esfinge.liveprog.reflect.ClassInfo;

/**
 * <p>
 * Interface para a persistência de classes dinâmicas.
 * </p>
 * <p><i>
 * Interface to be implemented by classes to provide LiveClasses persistence.
 * </i></p>
 */
public interface ILiveClassPersistence
{
	/**
	 * <p>
	 * Obtém as informações da classe dinâmica.
	 * </p>
	 * <p><i>
	 * Gets the persisted LiveClass information.
	 * </i></p>
	 * 
	 * @param liveClassName - nome da classe dinâmica
	 * <br><i>name of the persisted LiveClass</i>
	 * @param safeMode - <i>true</i> para recuperar as informações da classe dinâmica do modo seguro, 
	 * <i>false</i> para as informações do modo normal
	 * <br><i>true to get the information of the LiveClass on its safe mode, false for the information of the standard mode</i>
	 * @return as informações da classe dinâmica persistida, ou <i>null</i> se nenhuma versão for encontrada
	 * <br><i>the information of the persisted LiveClass, or null if none is found</i> 
	 * @throws Exception em caso de erros ao recuperar as informações da classe dinâmica persistida
 	 * <br><i>if an error occurs when retrieving the information of the persisted LiveClass</i>
	 */
	public ClassInfo getLiveClassInfo(String liveClassName, boolean safeMode) throws Exception;
	
	/**
	 * <p>
	 * Persiste as informações da classe dinâmica.
	 * </p>
	 * <p><i>
	 * Persists the LiveClass information.
	 * </i></p>
	 * 
	 * @param liveClassName - nome da classe dinâmica
	 * <br><i>name of the LiveClass to be persisted</i>
	 * @param liveClassInfo - informações da classe dinâmica a ser persistida
	 * <br><i>information of the LiveClass to be persisted</i>
	 * @throws Exception em caso de erros ao persistir a classe dinâmica
 	 * <br><i>if an error occurs when persisting the LiveClass</i>
	 */
	public void saveLiveClassInfo(String liveClassName, ClassInfo liveClassInfo) throws Exception;
	
	
	/**
	 * <p>
	 * Obtém as informações de versionamento da classe dinâmica.
	 * <br>
	 * Caso a classe ainda não tenha sido persistida, os métodos de versionamento de {@link ILiveClassVersionInfo} 
	 * devem retornar o valor inteiro <b>-1</b>.
	 * </p>
	 * <p><i>
	 * Gets the LiveClass versioning information.
	 * <br>
	 * If the LiveClass has not yet been persisted, all versioning methods of {@link ILiveClassVersionInfo} must return 
	 * the integer value <b>-1</b>.
	 * </i></p>
	 * 
	 * @param liveClassName - nome da classe dinâmica
	 * <br><i>name of the persisted LiveClass</i>
	 * @return as informações de versionamento da classe dinâmica informada
	 * <br><i>the versioning information of the specified LiveClass</i>
	 * @throws Exception em caso de erros ao recuperar as informações de versionamento da classe dinâmica
 	 * <br><i>if an error occurs when retrieving the versioning information of the specified LiveClass</i>
	 * @see org.esfinge.liveprog.db.ILiveClassVersionInfo
	 */
	public ILiveClassVersionInfo getLiveClassVersionInfo(String liveClassName) throws Exception;

	/**
	 * <p>
	 * Obtém as informações de versionamento de todas as classes dinâmicas persistidas.
	 * </p>
	 * <p><i>
	 * Gets the versioning information of all persisted LiveClasses.
	 * </i></p>
	 * 
	 * @return as informações de versionamento de todas as classes dinâmicas persistidas
	 * <br><i>the versioning information of all persisted LiveClasses</i>
	 * @throws Exception em caso de erros ao recuperar as informações de versionamento das classes dinâmicas
 	 * <br><i>if an error occurs when retrieving the versioning information of the persisted LiveClasses</i>
	 * @see org.esfinge.liveprog.db.ILiveClassVersionInfo
	 */
	public List<ILiveClassVersionInfo> getAllLiveClassesVersionInfo() throws Exception;
	
	/**
	 * <p>
	 * Aceita a versão atual da classe dinâmica, promovendo-a como uma versão segura. 
	 * </p>
	 * <p><i>
	 * Commits the current LiveClass version, promoting it as a safe version.
	 * </i></p>
	 * 
	 * @param liveClassName - nome da classe dinâmica
	 * <br><i>name of the persisted LiveClass</i>
	 * @return <i>true</i> se a versão atual pôde ser promovida como uma versão segura, <i>false</i> caso contrário 
	 * (i.e se a versão segura e a atual já forem iguais)
	 * <br><i>true if the current version was correctly promoted as a safe version, false otherwise 
	 * (i.e if both versions are already the same)</i>
	 * @throws Exception em caso de erros ao persistir o versionamento da classe dinâmica
 	 * <br><i>if an error occurs when persisting the LiveClass versioning</i>
 	 * @see org.esfinge.liveprog.db.ILiveClassVersionManager
	 */
	public boolean commitLiveClass(String liveClassName) throws Exception;

	/**
	 * <p>
	 * Descarta a versão atual da classe dinâmica, retrocendo-a para a versão anterior.
	 * </p>
	 * <p><i>
	 * Rolls back the current LiveClass version, reverting it to its previous version.
	 * </i></p>
	 * 
	 * @param liveClassName - nome da classe dinâmica
	 * <br><i>name of the persisted LiveClass</i>
	 * @return <i>true</i> se a versão atual pôde ser retrocedida para a versão anterior, <i>false</i> caso contrário 
	 * (i.e se a versão atual for a primeira versão)
	 * <br><i>true if the current version was correctly rolled back to its previous version, false otherwise 
	 * (i.e if the current version is in its first version)</i>
	 * @throws Exception em caso de erros ao persistir o versionamento da classe dinâmica
 	 * <br><i>if an error occurs when persisting the LiveClass versioning</i>
 	 * @see org.esfinge.liveprog.db.ILiveClassVersionManager
	 */
	public boolean rollbackLiveClass(String liveClassName) throws Exception;
}
