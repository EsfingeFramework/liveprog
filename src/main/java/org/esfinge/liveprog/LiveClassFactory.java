package org.esfinge.liveprog;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.esfinge.liveprog.db.ILiveClassPersistence;
import org.esfinge.liveprog.db.ILiveClassVersionInfo;
import org.esfinge.liveprog.db.ILiveClassVersionObserver;
import org.esfinge.liveprog.exception.IncompatibleLiveClassException;
import org.esfinge.liveprog.exception.LiveClassFactoryException;
import org.esfinge.liveprog.exception.LiveClassProxyException;
import org.esfinge.liveprog.instrumentation.InstrumentationHelper;
import org.esfinge.liveprog.monitor.ILiveClassFileMonitorObserver;
import org.esfinge.liveprog.reflect.AccessModifier;
import org.esfinge.liveprog.reflect.ClassInfo;
import org.esfinge.liveprog.reflect.MethodInfo;
import org.esfinge.liveprog.util.LiveClassUtils;

import net.sf.cglib.proxy.Enhancer;

/**
 * <p>
 * Fábrica para a criação de objetos de classes dinâmicas.
 * <p><i>
 * Factory for creating LiveClass objects.
 * </i>
 * 
 * @see org.esfinge.liveprog.annotation.LiveClass
 * @see org.esfinge.liveprog.LiveClassFactoryBuilder
 */
public class LiveClassFactory implements ILiveClassFileMonitorObserver, ILiveClassVersionObserver
{
	// mapa dos proxies criados para classes dinamicas em modo seguro
	private Map<String, List<LiveClassProxy>> mapProxiesSafeMode;
	
	// mapa dos proxies criados para classes dinamicas em modo padrao
	private Map<String, List<LiveClassProxy>> mapProxiesStdMode;
	
	// mapa dos observadores externos de classes dinamicas (sem ser os proxies)
	private Map<String, List<ILiveClassObserver>> mapObservers;	
	
	// cache das classes dinamicas atualmente criadas em modo seguro
	private Map<String, Class<?>> cacheLiveClassesSafeMode;
	
	// cache das classes dinamicas atualmente criadas em modo padrao
	private Map<String, Class<?>> cacheLiveClassesStdMode;
	
	// carregador de classes dinamicas
	private ILiveClassLoader classLoader;
	
	// gerenciador de atualizacoes de classes dinamicas 
	private LiveClassUpdateManager updateManager;
	
	// gerenciador do banco de dados
	private ILiveClassPersistence dbManager;
	
	// indica se a factory esta rodando em modo seguro
	private boolean factorySafeMode;
	

	/**
	 * <p>
	 * Constrói uma nova fábrica para a criação de objetos de classes dinâmicas.
	 * <p><i>
	 * Constructs a new factory for creating LiveClass objects. 
	 * </i>
	 * 
	 * @param dbManager gerenciador de persistência de classes dinâmicas
	 * <br><i>persistence manager of LiveClasses</i>
	 * @throws Exception caso ocorra algum erro interno de inicialização
	 * <br><i>in case of internal error during initialization</i>
	 */
	LiveClassFactory(ILiveClassPersistence dbManager) throws Exception
	{		
		this(null, dbManager, false);
	}
	
	/**
	 * <p>
	 * Constrói uma nova fábrica para a criação de objetos de classes dinâmicas.
	 * <p><i>
	 * Constructs a new factory for creating LiveClass objects. 
	 * </i>
	 * 
	 * @param dbManager gerenciador de persistência de classes dinâmicas
	 * <br><i>persistence manager of LiveClasses</i>
	 * @param safeMode <i>true</i> para que a fábrica execute em modo seguro, <i>false</i> para executar no modo padrão
	 * <br><i>true to set this factory to run in safe mode, false to run in standard mode</i> 
	 * @throws LiveClassFactoryException caso ocorra algum erro ao instanciar a fábrica
	 * <br><i>in case of error instantiating the factory</i> 
	 * @see #setSafeMode
	 * @see org.esfinge.liveprog.db.ILiveClassVersionManager
	 */
	LiveClassFactory(ILiveClassPersistence dbManager, boolean safeMode) throws LiveClassFactoryException
	{
		this(null, dbManager, safeMode);
	}

	/**
	 * <p>
	 * Constrói uma nova fábrica para a criação de objetos de classes dinâmicas.
	 * <p><i>
	 * Constructs a new factory for creating LiveClass objects. 
	 * </i>
	 * 
	 * @param classLoader classloader customizado para o carregamento de classes dinâmicas
	 * <br><i>custom classloader for loading LiveClasses</i>
	 * @param dbManager gerenciador de persistência de classes dinâmicas
	 * <br><i>persistence manager of LiveClasses</i>
	 * @param safeMode <i>true</i> para que a fábrica execute em modo seguro, <i>false</i> para executar no modo padrão
	 * <br><i>true to set this factory to run in safe mode, false to run in standard mode</i> 
	 * @throws LiveClassFactoryException caso ocorra algum erro ao instanciar a fábrica
	 * <br><i>in case of error instantiating the factory</i> 
	 * @see #setSafeMode
	 * @see org.esfinge.liveprog.db.ILiveClassVersionManager
	 */
	LiveClassFactory(ILiveClassLoader classLoader, ILiveClassPersistence dbManager, boolean safeMode) throws LiveClassFactoryException
	{
		try
		{
			this.mapProxiesSafeMode = new HashMap<String, List<LiveClassProxy>>();
			this.mapProxiesStdMode = new HashMap<String, List<LiveClassProxy>>();
			this.mapObservers = new HashMap<String, List<ILiveClassObserver>>();
			this.cacheLiveClassesSafeMode = new HashMap<String, Class<?>>();
			this.cacheLiveClassesStdMode = new HashMap<String, Class<?>>();
			this.updateManager = new LiveClassUpdateManager();
			this.dbManager = dbManager;

			// seta o modo de operacao da fabrica
			this.setSafeMode(safeMode);
			
			// verifica se foi informado um classloader customizado
			if ( classLoader != null )
				this.classLoader = classLoader;
			else
				this.classLoader = new LiveClassLoader();
			
		}
		catch ( Exception e )
		{
			// log: erro
			LiveClassUtils.logError("Erro ao instanciar a fabrica de objetos de classes dinamicas!");
			
			throw new LiveClassFactoryException("Unable to instantiate a new LiveClassFactory object!", e);
		}
	}
	
	/**
	 * <p>
	 * Cria um novo objeto de uma classe dinâmica.
	 * <p><i>
	 * Creates a new LiveClass object.
	 * </i>
	 * 
	 * @param liveClass a classe dinâmica
	 * <br><i>the LiveClass class</i>
	 * @return um novo objeto dinâmico que é atualizado automaticamente quando uma nova versão da classe dinâmica é carregada
	 * <br><i>a new 'live' object that is automatically updated when a new version of its LiveClass is loaded</i>
	 * @throws IncompatibleLiveClassException caso <b>liveClass</b> não seja uma classe dinâmica válida
	 * <br><i>in case of <b>liveCLass</b> is not a valid LiveClass</i>
	 * @see org.esfinge.liveprog.annotation.LiveClass
	 * @see org.esfinge.liveprog.instrumentation.InstrumentationHelper#checkValidLiveClass(Class)
	 */
	public <L> L createLiveObject(Class<L> liveClass) throws IncompatibleLiveClassException
	{
		return ( this.createLiveObject(liveClass, this.factorySafeMode) );
	}

	/**
	 * <p>
	 * Cria um novo objeto de uma classe dinâmica em modo seguro.
	 * <p><i>
	 * Creates a new LiveClass object in safe mode.
	 * </i>
	 * 
	 * @param liveClass a classe dinâmica
	 * <br><i>the LiveClass class</i>
	 * @return um novo objeto dinâmico que é atualizado automaticamente quando uma nova versão da classe dinâmica é aceita (commit)
	 * <br><i>a new 'live' object that is automatically updated when a new version of its LiveClass is committed</i>
	 * @throws IncompatibleLiveClassException caso <b>liveClass</b> não seja uma classe dinâmica válida
	 * <br><i>in case of <b>liveCLass</b> is not a valid LiveClass</i>
	 * @see org.esfinge.liveprog.annotation.LiveClass
	 * @see org.esfinge.liveprog.instrumentation.InstrumentationHelper#checkValidLiveClass(Class)
	 * @see #setSafeMode
	 * @see org.esfinge.liveprog.db.ILiveClassVersionManager
	 */
	public <L> L createLiveObjectInSafeMode(Class<L> liveClass) throws IncompatibleLiveClassException
	{
		return ( this.createLiveObject(liveClass, true) );
	}
	
	@Override
	public void liveClassFileUpdated(File liveClassFile)
	{
		// log: arquivo de classe dinamica recebido
		LiveClassUtils.logInfo("Arquivo de classe dinamica recebido: " + liveClassFile.getName());
		
		// redireciona para o gerenciador de atualizacoes
		this.updateManager.liveClassFileUpdated(liveClassFile);
	}
	
	@Override
	public void liveClassCommitted(String liveClassName)
	{
		// log: commit de classe dinamica
		LiveClassUtils.logInfo("Classe dinamica - commit: " + liveClassName);
		
		try
		{
			// obtem as informacoes da classe dinamica no modo seguro
			ClassInfo liveClassInfo = this.dbManager.getLiveClassInfo(liveClassName, true);
			
			// carrega a nova versao da classe dinamica
			Class<?> newLiveClass = this.classLoader.loadLiveClass(liveClassInfo);
			
			// atualiza o cache de classes do modo seguro de operacao
			this.cacheLiveClassesSafeMode.put(liveClassName, newLiveClass);
			
			// notifica os proxies em modo seguro de operacao
			this.notifyCommit(liveClassName, newLiveClass, true);
			
			// notifica os observadores externos
			this.notifyExternalObservers(liveClassName, newLiveClass);
		}
		catch ( Exception e )
		{
			// log: erro commit
			LiveClassUtils.logError("Erro ao carregar commit da classe dinamica '" + liveClassName + "'");
			LiveClassUtils.logException(e);
		}
	}

	@Override
	public void liveClassRolledBack(String liveClassName)
	{
		try
		{
			// log: rollback de classe dinamica
			LiveClassUtils.logInfo("Classe dinamica - rollback (modo padrao): " + liveClassName);

			// obtem as informacoes da classe dinamica no modo padrao
			ClassInfo liveClassInfo = this.dbManager.getLiveClassInfo(liveClassName, false);
			
			// carrega a nova versao da classe dinamica
			Class<?> newLiveClass = this.classLoader.loadLiveClass(liveClassInfo);
			
			// atualiza o cache de classes do modo padrao de operacao
			this.cacheLiveClassesStdMode.put(liveClassName, newLiveClass);
			
			// notifica os proxies em modo padrao de operacao
			this.notifyRollback(liveClassName, newLiveClass, false);
			
			// verifica se alterou tambem a versao do modo seguro
			if (! newLiveClass.equals(this.cacheLiveClassesSafeMode.put(liveClassName, newLiveClass)) )
			{
				// log: rollback do modo seguro
				LiveClassUtils.logInfo("Classe dinamica - rollback (modo seguro): " + liveClassName);

				// notifica os proxies em modo seguro de operacao
				this.notifyRollback(liveClassName, newLiveClass, true);
			}
			
			// notifica os observadores externos
			this.notifyExternalObservers(liveClassName, newLiveClass);
		}
		catch ( Exception e )
		{
			// log: erro rollback
			LiveClassUtils.logError("Erro ao carregar rollback da classe dinamica '" + liveClassName + "'");
			LiveClassUtils.logException(e);
		}
	}
	
	/**
	 * <p>
	 * Atribui o modo de operação da fábrica.
	 * Se a fábrica estiver executando em modo seguro, os objetos dinâmicos só serão atualizados 
	 * quando a nova versão da classe dinâmica for aceita (commit). 
	 * <p><i>
	 * Sets the operation mode of this factory.
	 * When executing in safe mode, 'live' objects are only updated after a commit of the LiveClass new version.
	 * </i>
	 * 
	 * @param safeMode <i>true</i> para que a fábrica execute em modo seguro, <i>false</i> para executar no modo padrão
	 * <br><i>true to set this factory to run in safe mode, false to run in standard mode</i> 
	 */
	public void setSafeMode(boolean safeMode)
	{
		this.factorySafeMode = safeMode;
		
		// log: modo de operacao
		LiveClassUtils.logDebug("Safe mode: " + this.factorySafeMode);
	}

	/**
	 * <p>
	 * Registra o observador para que seja notificado sobre atualizações da classe dinâmica informada.
	 * <p><i>
	 * Registers the observer interested on being notified about updates of the specified LiveClass.
	 * </i> 
	 * 
	 * @param liveClass classe dinâmica a ser observada
	 * <br><i>the LiveClass to be observed</i>
	 * @param observer observador a ser notificado sobre atualizações da classe informada
	 * <br><i>observer to be notified about updates of the specified LiveClass</i>
	 * @return <i>true</i> se o observador foi registrado corretamente, <i>false</i> caso contrário (i.e. caso <b>liveClass</b> não seja uma classe dinâmica válida)
	 * <br><i>true if the observer was successfully registered, false otherwise (i.e in case of <b>liveCLass</b> is not a valid LiveClass)</i>
	 * @see org.esfinge.liveprog.ILiveClassObserver
	 * @see org.esfinge.liveprog.instrumentation.InstrumentationHelper#checkValidLiveClass(Class)
	 */
	public boolean addLiveClassObserver(Class<?> liveClass, ILiveClassObserver observer)
	{
		try
		{
			// recupera a lista de observadores da classe informada
			List<ILiveClassObserver> lstObservers = this.mapObservers.get(liveClass.getName());
			
			if ( lstObservers == null )
			{
				// verifica se a classe cumpre os requisitos de classes dinamicas
				InstrumentationHelper.checkValidLiveClass(liveClass);
				
				// cria uma lista de observadores para a classe dinamica informada
				lstObservers = new ArrayList<ILiveClassObserver>();
			}
			
			// adiciona o novo observador
			lstObservers.add(observer);
			
			// atualiza o mapa
			this.mapObservers.put(liveClass.getName(), lstObservers);
			
			return ( true );
		}
		catch ( IncompatibleLiveClassException e )
		{
			// log: erro ao registrar observador
			LiveClassUtils.logError("Erro ao registrar observador para a classe '" + liveClass.getName() + "'");
			LiveClassUtils.logException(e);
			
			return ( false );
		}
	}
	
	/**
	 * <p>
	 * Remove o observador da lista de observadores da classe dinâmica informada.
	 * <p><i>
	 * Removes the observer from the list of registered observers of the specified LiveClass.
	 * </i> 
	 * 
	 * @param liveClass classe dinâmica sendo observada
	 * <br><i>the observed LiveClass</i>
	 * @param observer observador a ser removido da lista de observadores registrados da classe informada
	 * <br><i>observer to be removed from the list of registered observers of the specified LiveClass</i>
	 * @see org.esfinge.liveprog.ILiveClassObserver
	 */
	public void removeLiveClassObserver(Class<?> liveClass, ILiveClassObserver observer)
	{
		// recupera a lista de observadores da classe informada
		List<ILiveClassObserver> lstObservers = this.mapObservers.get(liveClass.getName());
		
		// remove o observador
		if ( lstObservers != null )
			lstObservers.remove(observer);
	}
	
	/**
	 * <p>
	 * Cria um objeto dinâmico da classe informada.
	 * <p><i>
	 * Creates a 'live' object of the specified LiveClass.
	 * </i>
	 * 
	 * @param liveClass a classe dinâmica
	 * <br><i>the LiveClass class</i>
	 * @param safeMode <i>true</i> para que a objeto seja criado em modo seguro, <i>false</i> para ser criado no modo normal
	 * <br><i>true creates the object in safe mode, false on standard mode</i>
	 * @return um novo objeto dinâmico que é atualizado automaticamente nas alterações da classe dinâmica
	 * <br><i>a new 'live' object that is automatically updated when its LiveClass changes</i>
	 * @throws IncompatibleLiveClassException caso <b>liveClass</b> não seja uma classe dinâmica válida
	 * <br><i>in case of <b>liveCLass</b> is not a valid LiveClass</i>
	 */
	@SuppressWarnings("unchecked")
	private <L> L createLiveObject(Class<L> liveClass, boolean safeMode) throws IncompatibleLiveClassException
	{
		try
		{
			// cache das classes dinamicas ja criadas
			Map<String,Class<?>> cacheLiveClasses = safeMode ? this.cacheLiveClassesSafeMode : this.cacheLiveClassesStdMode;

			// nome da classe
			String className = liveClass.getName();
			
			// verifica se a classe esta no cache
			Class<?> clazz = cacheLiveClasses.get(className);
			
			if ( clazz == null )
			{
				// log: classe dinamica nao encontrada no cache
				LiveClassUtils.logDebug("Classe dinamica nao encontrada no cache: '" + liveClass.getName() + "'");
				
				// verifica se a classe cumpre os requisitos de classes dinamicas
				InstrumentationHelper.checkValidLiveClass(liveClass);
				
				// obtem as informacoes da classe armazenadas no BD, se houver
				ClassInfo liveClassInfo = this.dbManager.getLiveClassInfo(className, safeMode);
				
				// verifica se ja tem alguma versao salva no banco
				if ( liveClassInfo == null )
				{
					// log: classe dinamica ainda nao foi persistida
					LiveClassUtils.logDebug("Classe dinamica ainda nao foi persistida: '" + liveClass.getName() + "'");
										
					// primeiro uso da classe dinamica
					liveClassInfo = InstrumentationHelper.inspect(liveClass);
					
					// salva no banco de dados
					this.dbManager.saveLiveClassInfo(liveClassInfo.getName(), liveClassInfo);
				}
				
				// carrega a versao da classe dinamica
				clazz = this.classLoader.loadLiveClass(liveClassInfo);

				// salva no cache	
				cacheLiveClasses.put(className, clazz);
			}
			
			// cria o objeto da classe dinamica
			Object liveObj = clazz.newInstance();
			
			// cria um proxy para o objeto da classe dinamica
			LiveClassProxy proxy = new LiveClassProxy(liveObj);
			Enhancer e = new Enhancer();
			e.setSuperclass(liveClass);
			e.setInterfaces(liveClass.getInterfaces());
			e.setCallback(proxy);
			
			// registra o proxy para ser notificado quando a classe dinamica for atualizada
			this.registerProxy(className, proxy, safeMode);

			return ( (L) e.create() );
		}
		catch ( Exception e)
		{
			// log: erro ao criar objeto dinamico
			LiveClassUtils.logError("Erro ao criar objeto dinamico da classe '" + liveClass.getName() + "'");
			LiveClassUtils.logException(e);
			
			// problemas ao criar objeto dinamico
			throw new IncompatibleLiveClassException("Unable to create a new live object!", e);
		}
	}
	
	/**
	 * <p>
	 * Registra os proxies criados para que eles sejam notificados quando a classe dinâmica for atualizada.
	 * <p><i>
	 * Registers the proxies in order to notify them about updates of the LiveClass they are bound to. 
	 * </i>
	 *  
	 * @param liveClassName nome da classe dinâmica
	 * <br><i>name of the LiveClass class</i>
	 * @param proxy proxy para a classe dinâmica
	 * <br><i>proxy bounded to the LiveClass</i>
	 * @param safeMode <i>true</i> se o objeto dinâmico foi criado em modo seguro, <i>false</i> se foi criado no modo normal
	 * <br><i>true if the 'live' object was created in safe mode, false if created in standard mode</i> 
	 */
	private void registerProxy(String liveClassName, LiveClassProxy proxy, boolean safeMode)	
	{
		// mapa dos proxies
		Map<String, List<LiveClassProxy>> mapProxies = safeMode ? this.mapProxiesSafeMode : this.mapProxiesStdMode;
		
		// recupera a lista de proxies da classe informada
		List<LiveClassProxy> lstProxies = mapProxies.get(liveClassName);
		
		if ( lstProxies == null )
			lstProxies = new ArrayList<LiveClassProxy>();
		
		// adiciona o novo proxy
		lstProxies.add(proxy);

		// atualiza o mapa
		mapProxies.put(liveClassName, lstProxies);
	}
	
	/**
	 * <p>
	 * Recebe a notificação de que uma classe dinâmica foi atualizada para uma nova versão.
	 * <p><i>
	 * Is notified that a LiveClass was updated to a new version.
	 * </i>
	 * 
	 * @param liveClassName nome da classe dinâmica
	 * <br><i>name of the LiveClass</i>
	 * @param newLiveClassInfo informações da nova versão da classe dinâmica
	 * <br><i>information of its new version</i>
	 */
	private void liveClassUpdated(String liveClassName, ClassInfo newLiveClassInfo)
	{
		try
		{
			// verifica se a versao original (primeira versao) da classe ja foi persistida
			ILiveClassVersionInfo versionInfo = this.dbManager.getLiveClassVersionInfo(liveClassName);
			
			if ( versionInfo.getCurrentVersion() < 1 )
			{
				// log: classe dinamica ainda nao foi persistida
				LiveClassUtils.logInfo("Classe dinamica original ainda nao foi persistida: '" + liveClassName + "'");
				
				// obtem a classe original
				Class<?> origLiveClass = Class.forName(liveClassName);
				
				// verifica se a classe cumpre os requisitos de classes dinamicas
				InstrumentationHelper.checkValidLiveClass(origLiveClass);
									
				// obtem informacoes da primeira versao
				ClassInfo origLiveClassInfo = InstrumentationHelper.inspect(origLiveClass);
				
				// salva a primeira versao no banco de dados
				this.dbManager.saveLiveClassInfo(liveClassName, origLiveClassInfo);
			}			
			
			// persiste a nova versao da classe
			this.dbManager.saveLiveClassInfo(liveClassName, newLiveClassInfo);
			
			// log: classe dinamica atualizada
			LiveClassUtils.logInfo("Classe dinamica atualizada: " + liveClassName);
			
			// carrega a classe dinamica
			Class<?> newLiveClass = this.classLoader.loadLiveClass(newLiveClassInfo);

			// log:  classe dinamica carregada
			LiveClassUtils.logInfo("Classe dinamica carregada: " + newLiveClass.getName());

			// atualiza o cache de classes do modo normal de operacao
			this.cacheLiveClassesStdMode.put(liveClassName, newLiveClass);
			
			// notifica os proxies em modo normal de operacao
			this.notifyCommit(liveClassName, newLiveClass, false);
			
			// notifica os observadores externos
			this.notifyExternalObservers(liveClassName, newLiveClass);
		}
		catch ( Exception e )
		{
			// log: erro ao carregar classe dinamica
			LiveClassUtils.logError("Erro ao carregar nova versao da classe dinamica '" + liveClassName + "'");
			LiveClassUtils.logException(e);
		}
	}
	
	/**
	 * <p>
	 * Notifica os proxies que a classe dinâmica foi recarregada em uma nova versão.
	 * <p><i>
	 * Notifies the proxies that the LiveClass was reloaded on a new version.
	 * </i>
	 * 
	 * @param liveClassName nome da classe dinâmica
	 * <br><i>name of the LiveClass</i>
	 * @param newLiveClass nova versão da classe dinâmica
	 * <br><i>updated version of the LiveClass</i>
	 * @param safeMode <i>true</i> para notificar os proxies de objetos criados em modo seguro, 
	 * <i>false</i> para os criados no modo normal
	 * <br><i>true to notify the proxies of 'live' object created in safe mode, false for those created in standard mode</i> 
	 */
	private void notifyCommit(String liveClassName, Class<?> newLiveClass, boolean safeMode) throws LiveClassProxyException
	{
		// mapa dos proxies
		Map<String, List<LiveClassProxy>> mapProxies = safeMode ? this.mapProxiesSafeMode : this.mapProxiesStdMode;

		if ( mapProxies.containsKey(liveClassName) )
			for ( LiveClassProxy proxy : mapProxies.get(liveClassName) )
				proxy.classReloaded(newLiveClass);
	}
	
	/**
	 * <p>
	 * Notifica os proxies que a classe dinâmica foi revertida para uma versão anterior.
	 * <p><i>
	 * Notifies the proxies that the LiveClass was rolled back to a previous version.
	 * </i>
	 * 
	 * @param liveClassName nome da classe dinâmica
	 * <br><i>name of the LiveClass</i>
	 * @param newLiveClass nova versão da classe dinâmica
	 * <br><i>updated version of the LiveClass</i>
	 * @param safeMode <i>true</i> para notificar os proxies de objetos criados em modo seguro, 
	 * <i>false</i> para os criados no modo normal
	 * <br><i>true to notify the proxies of 'live' object created in safe mode, false for those created in standard mode</i> 
	 */
	private void notifyRollback(String liveClassName, Class<?> newLiveClass, boolean safeMode) throws LiveClassProxyException
	{
		// mapa dos proxies
		Map<String, List<LiveClassProxy>> mapProxies = safeMode ? this.mapProxiesSafeMode : this.mapProxiesStdMode;

		if ( mapProxies.containsKey(liveClassName) )
			for ( LiveClassProxy proxy : mapProxies.get(liveClassName) )
				proxy.classRolledBack(newLiveClass);
	}
	
	/**
	 * <p>
	 * Notifica os observadores externos que a classe dinâmica foi recarregada em uma nova versão.
	 * <p><i>
	 * Notifies the observers that the LiveClass was reloaded on a new version.
	 * </i>
	 * 
	 * @param liveClassName nome da classe dinâmica
	 * <br><i>name of the LiveClass</i>
	 * @param newLiveClass nova versão da classe dinâmica
	 * <br><i>updated version of the LiveClass</i>
	 */
	private void notifyExternalObservers(String liveClassName, Class<?> newLiveClass)
	{
		if ( this.mapObservers.containsKey(liveClassName) )
			this.mapObservers.get(liveClassName).forEach(obs -> obs.liveClassUpdated(liveClassName, newLiveClass));
	}
	
	
	/**
	 * <p>
	 * Gerenciador de atualizações de classes dinâmicas.
	 * <p><i>
	 * LiveClasses update manager.
	 * </i>
	 */
	private class LiveClassUpdateManager implements ILiveClassFileMonitorObserver
	{
		// lista de mapas de classes (para classes que possuem classes internas)
		public List<ClassMap> classMapList;
		

		/**
		 * <p>
		 * Constrói um novo gerenciador de atualizações de classes dinâmicas.
		 * <p><i>
		 * Constructs a new manager for LiveClasses updates.
		 * </i>
		 */
		LiveClassUpdateManager()
		{
			this.classMapList = new ArrayList<ClassMap>();
		}
		
		@Override
		public synchronized void liveClassFileUpdated(File liveClassFile)
		{
			try
			{
				// obtem as informacoes da classe do arquivo recebido
				ClassInfo classInfo = InstrumentationHelper.inspect(liveClassFile);

				// log: arquivo de classe recebido
				LiveClassUtils.logInfo("Classe recebida: '" + classInfo.getName() + "'");
				
				// mapeamento das classes que compoem a classe do arquivo recebido
				ClassMap classMap = this.findClassMap(classInfo.getName());
				classMap.getClassNode(classInfo.getName()).setClassInfo(classInfo);
				
				// verifica se as classes internas ja foram mapeadas
				if ( classInfo.containsInnerClasses() )
					for ( String innerClass : classInfo.getInnerClassNames() )
						if (! classMap.containsClass(innerClass) )
						{
							// mapa que contem a classe interna
							ClassMap innerClassMap = this.findClassMap(innerClass);
							
							// incorpora os mapas relacionados
							classMap = this.mergeClassMaps(classMap, innerClassMap);
						}
				
				// verifica se eh uma classe interna
				if ( classInfo.isInnerClass() )
					if (! classMap.containsClass(classInfo.getEnclosingClassName()) ) 
					{
						// mapa que contem a classe externa
						ClassMap outerClassMap = this.findClassMap(classInfo.getEnclosingClassName());
						
						// incorpora os mapas relacionados
						classMap = this.mergeClassMaps(classMap, outerClassMap);
					}
				
				// verifica se mapeamento das classes esta completo
				if ( classMap.isComplete() )
				{
					// organiza as classes internas
					classMap.doArrange();
					
					// obtem a classe dinamica (classe raiz)
					classInfo = classMap.getRootClass();
					
					// log: nova versao de classe dinamica
					LiveClassUtils.logInfo("Nova versao da classe dinamica: '" + classInfo.getName() + "'");

					try
					{
						// verifica a compatibilidade da nova classe com a versao original
						this.checkClassCompatibility(classInfo);
						
						// cria a nova versao da classe
						ClassInfo newClassInfo = this.createNewVersion(classInfo);
						
						// notifica a fabrica
						LiveClassFactory.this.liveClassUpdated(classInfo.getName(), newClassInfo);
					}			
					catch ( Exception e )
					{
						// log: erro ao criar nova versao da classe dinamica
						LiveClassUtils.logError("Erro ao criar nova versao da classe dinamica '" + classInfo.getName() + "'");
						LiveClassUtils.logException(e);
					}			
					finally
					{
						// remove o mapa de classes completo da lista
						this.classMapList.remove(classMap);
					}
				}
			}
			catch ( Exception e )
			{
				// log: erro
				LiveClassUtils.logException(e);
			}
		}
		
		/**
		 * <p>
		 * Verifica a compatibilidade da nova versão da classe dinâmica com a sua versão original.
		 * A nova classe deve ter a mesma interface pública da versão original.
		 * <p><i>
		 * Checks if the new version of the LiveClass is compatible with its original version.
		 * The new class must comply with the original public interface. 
		 * </i>
		 * 
		 * @param newClassInfo as informações da nova classe
		 * <br><i>information of its new version</i>
		 * @throws IncompatibleLiveClassException se a nova classe não for compatível com a versão original
		 * <br><i>if the new version is not compatible with its original class</i>
		 */
		private void checkClassCompatibility(ClassInfo newClassInfo) throws IncompatibleLiveClassException
		{
			try
			{
				// obtem a classe original
				Class<?> origClass = Class.forName(newClassInfo.getName());
				
				// obtem os metodos publicos da nova classe
				List<MethodInfo> newClassMethods = LiveClassUtils.filterFromCollection(newClassInfo.getMethodsInfo(), 
															m -> m.getAccessPermission() == AccessModifier.PUBLIC);
				
				// verifica se a nova classe possui os metodos publicos da classe original
				for ( Method origMethod : origClass.getDeclaredMethods() )
				{
					// ignora os metodos nao publicos
					if (! Modifier.isPublic(origMethod.getModifiers()) )
						continue;

					// tenta obter o mesmo metodo na nova classe
					boolean cont = false;
					List<MethodInfo> newMethods = LiveClassUtils.filterFromCollection(newClassMethods, 
															m -> m.getName().equals(origMethod.getName()) && 
															    (m.getParametersInfo().size() == origMethod.getParameterCount()));
					for ( MethodInfo m : newMethods )
					{
						// a principio, assume que o metodo vai ser encontrado
						cont = true;
						
						// verifica se tem os mesmos tipos de parametro
						Class<?>[] params = origMethod.getParameterTypes();
						
						for ( int i = 0; (i < params.length) && cont; i++ )
							cont = params[i].isAssignableFrom(m.getParameterAtIndex(i).getType().getTypeClass());
						
						// verifica se o tipo de retorno eh compativel	
						if ( cont )
							cont = origMethod.getReturnType().isAssignableFrom(m.getReturnType().getTypeClass());
						
						// verifica se as excecoes sao compativeis
						if ( cont )
							for ( Class<?> e : origMethod.getExceptionTypes() )
							{
								// assume que a excecao nao foi encontrada
								cont = false;
								
								for ( String e1 : m.getThrownExceptionNames() )
									cont |= e.isAssignableFrom(Class.forName(e1));
								
								// nao encontrou excecao compativel, erro..
								if (! cont )
									break;
							}
					}
					
					// nao encontrou o metodo!
					if (! cont )
						throw new NoSuchMethodException("Method not found on new class: " + origMethod);
				}
				
				// log: versao compativel
				LiveClassUtils.logInfo("Nova versao da classe dinamica compativel com a original!");
			}
			catch ( Exception e )
			{
				// log: versao incompativel
				LiveClassUtils.logError("Nova versao da classe dinamica NAO compativel com a original!");

				throw new IncompatibleLiveClassException("New class is incompatible with original class public interface!", e);
			}
		}
		
		/**
		 * <p>
		 * Cria a nova versão da classe dinâmica.
		 * <p><i>
		 * Creates a new version of the LiveClass.
		 * </i>
		 * 
		 * @param classInfo as informações da classe atualizada, carregadas do seu arquivo .class
		 * <br><i>information of the updated class, read from its class file</i>
		 * @return as informações da classe criada em sua nova versão
		 * <br><i>information of the newly created class on its new version</i>
		 */
		private ClassInfo createNewVersion(ClassInfo classInfo)
		{
			// nome da nova versao da classe
			String newName = String.format("%s_%X", classInfo.getName(), System.currentTimeMillis());
			
			// instrumenta para a nova versao da classe
			return ( InstrumentationHelper.transform(classInfo, classInfo.getName(), newName) );
		}
		
		/**
		 * <p>
		 * Retorna o mapa de classes que contém a classe informada.
		 * Se não encontrar, cria um novo mapa para ela.
		 * <p><i>
		 * Returns the classmap that contains the specified class.
		 * If none is found, creates a new map for it.
		 * </i>
		 * 
		 * @param className o nome da classe
		 * <br><i>the class name</i>
		 * @return o mapa de classes que contém a classe informada
		 * <br><i>the classmap that contains the specified class</i>
		 */
		private ClassMap findClassMap(String className)
		{
			// verifica se a classe ja foi mapeada
			for ( ClassMap cm : this.classMapList )
				if ( cm.containsClass(className) )
					return ( cm );
					
			
			// a classe nao foi mapeada, cria um novo para ela
			ClassNode classNode = new ClassNode();
			classNode.setClassName(className);
			
			ClassMap classMap = new ClassMap();
			classMap.addClassNode(classNode);
			
			// adiciona o mapa de classes na lista
			this.classMapList.add(classMap);
			
			return ( classMap );
		}
		
		/**
		 * <p>
		 * Combina dois mapas de classes relacionados.
		 * <p><i>
		 * Merges two related classmaps.
		 * </i>
		 * 
		 * @param cm1 o mapa de classes relacionadas
		 * <br><i>the related classmap</i>
		 * @param cm2 o mapa de classes relacionadas
		 * <br><i>the related classmap</i>
		 * @return um novo mapa de classes contendo as classes dos mapas informados
		 * <br><i>a new classmap comprising both classmaps</i>
		 */
		private ClassMap mergeClassMaps(ClassMap cm1, ClassMap cm2)
		{
			ClassMap newClassMap = new ClassMap();
			newClassMap.classMap.putAll(cm1.classMap);
			newClassMap.classMap.putAll(cm2.classMap);
		
			// remove os mapas antigos da lista
			this.classMapList.remove(cm1);
			this.classMapList.remove(cm2);
			
			// adiciona o novo mapa
			this.classMapList.add(newClassMap);
			
			return ( newClassMap );
		}

		
		/**
		 * <p>
		 * Classe auxiliar para mapeamento de uma classe dinâmica e suas classes internas.
		 * Para que uma classe dinâmica possa ser instrumentada e carregada em sua nova versão,
		 * todos os arquivos das suas classes internas (que também podem conter classes internas) devem ser
		 * encontrados e mapeados. Esta classe monta o grafo de relacionamento dessas classes.  
		 * <p><i>
		 * Utilitary class for mapping a LiveClass and its inner classes.
		 * For a LiveClass to be instrumented and loaded on its new version, all inner classes files (and those of their inner classes)
		 * must be gathered and mapped. This class helps on building the graph of these related classes.
		 * </i>
		 */
		private class ClassMap
		{
			// mapa da classe dinamica e suas classes internas
			private Map<String,ClassNode> classMap;

			
			/**
			 * <p>
			 * Constrói um novo mapa de classes.
			 * <p><i>
			 * Constructs a new classmap. 
			 * </i>
			 */
			ClassMap()
			{
				this.classMap = new HashMap<String,ClassNode>();
			}
			
			/**
			 * <p>
			 * Adiciona um novo nó com informações de uma classe relacionada a esse mapa.
			 * <p><i>
			 * Adds a new class node related to this classmap.
			 * </i>
			 * 
			 * @param node nó com informações da classe relacionada a esse mapa
			 * <br><i>class node related to this classmap</i>
			 */
			public void addClassNode(ClassNode node)
			{
				this.classMap.put(node.getClassName(), node);
			}
			
			/**
			 * <p>
			 * Obtém o nó com informações da classe informada.
			 * <p><i>
			 * Gets the class node associated with the specified class.
			 * </i>
			 * 
			 * @param className nome da classe
			 * <br><i>name of the class</i> 
			 * @return o nó com as informações da classe informada
			 * <br><i>the class node associated with the specified class</i>
			 */
			public ClassNode getClassNode(String className)
			{
				return ( this.classMap.get(className) );
			}
			
			/**
			 * <p>
			 * Verifica se este mapa contém um nó associado à classe informada.
			 * <p><i>
			 * Checks if this classmap contains a class node associated with the specified class
			 * </i>
			 * 
			 * @param className nome da classe 
			 * <br><i>name of the class</i> 
			 * @return <i>true</i> se a classe constar no mapa de classes,
			 * <i>false</i> caso contrário
			 * <br><i>true if the specified class belongs to this classmap, false otherwise</i>
			 */
			public boolean containsClass(String className)
			{
				return ( this.classMap.containsKey(className) );
			}
			
			/**
			 * <p>
			 * Verifica se o mapeamento de classes está completo.
			 * <p><i>
			 * Checks if the classes mapping is complete.
			 * </i>
			 * 
			 * @return <i>true</i> se todos os nós das classes relacionadas foram carregados neste mapa de classes, 
			 * <i>false</i> caso contrário
			 * <br><i>true if all related class nodes were loaded on this classmap, false otherwise</i>
			 */
			public boolean isComplete()
			{
				// verifica se alguma classe ainda nao teve suas informacoes carregadas
				for ( ClassNode n : this.classMap.values() )
					if (! n.isLoaded() )
						return ( false );
				
				return ( true );
			}
			
			/**
			 * <p>
			 * Obtém as informações da classe raíz deste mapa de classes - ou seja, da classe dinâmica.
			 * <p><i>
			 * Gets the information of the top class of this classmap - the LiveClass itself.
			 * </i>
			 * 
			 * @return as informações da classe dinâmica
			 * <br><i>information of the LiveClass itself</i>
			 */
			public ClassInfo getRootClass()
			{
				return ( LiveClassUtils.getFromCollection(this.classMap.values(), n -> !n.getClassInfo().isInnerClass()).getClassInfo() );
			}

			/**
			 * <p>
			 * Organiza os mapeamentos internos deste mapa de classes.
			 * <p><i>
			 * Organizes the inner mappings of this classmap.
			 * </i>
			 */
			public void doArrange()
			{
				// verifica se o mapeamento de classes ja esta completo
				if (! this.isComplete() )
					return;
				
				// obtem a classe raiz
				ClassInfo rootClassInfo = this.getRootClass();
				
				// ordena as classes internas
				this.arrangeInnerClasses(rootClassInfo);
			}
			
			/**
			 * <p>
			 * Ordena as informações das classes internas da classe informada. 
			 * <p><i>
			 * Provides the inner classes arrangement of the specified class.
			 * </i>
			 * @param classInfo classe a ser organizada
			 * <br><i>class to be arranged</i>
			 * @return a classe com as informações das classes internas adicionadas
			 * <br><i>the specified class and its inner classes sorted out</i>
			 */
			private ClassInfo arrangeInnerClasses(ClassInfo classInfo)
			{
				for ( String innerClassName : classInfo.getInnerClassNames() )
					classInfo.addInnerClassInfo(this.arrangeInnerClasses(this.getClassNode(innerClassName).getClassInfo()));
				
				return ( classInfo );
			}
		}
		
		
		/**
		 * <p>
		 * Armazena informações sobre uma classe associada a um mapa de classes.  
		 * <p><i>
		 * Stores information about a class related to a classmap.
		 * </i>
		 */
		private class ClassNode
		{
			// nome da classe
			private String className;
			
			// informacoes da classe
			private ClassInfo classInfo;

			
			/**
			 * <p>
			 * Obtém o nome da classe.
			 * <p><i>
			 * Gets the class name.
			 * </i>
			 * 
			 * @return o nome da classe
			 * <br><i>the name of the class</i>
			 */
			public String getClassName()
			{
				return className;
			}

			/**
			 * <p>
			 * Atribui o nome da classe.
			 * <p><i>
			 * Sets the class name.
			 * </i>
			 * @param className nome da classe
			 * <br><i>name of the class</i>
			 */
			public void setClassName(String className)
			{
				this.className = className;
			}

			/**
			 * <p>
			 * Obtém as informações da classe.
			 * <p><i>
			 * Gets the information of the class. 
			 * </i>
			 * 
			 * @return as informações da classe
			 * <br><i>the information of the class</i>
			 * @see org.esfinge.liveprog.reflect.ClassInfo
			 */
			public ClassInfo getClassInfo()
			{
				return classInfo;
			}

			/**
			 * <p>
			 * Atribui as informações da classe.
			 * <p><i>
			 * Sets the information of the class.
			 * </i>
			 * 
			 * @param classInfo as informações da classe
			 * <br><i>information of the class</i>
			 * @see org.esfinge.liveprog.reflect.ClassInfo
			 */
			public void setClassInfo(ClassInfo classInfo)
			{
				this.classInfo = classInfo;
			}

			/**
			 * <p>
			 * Verifica se as informações da classe já foram carregadas.
			 * As informações são carregadas quando lidas do seu arquivo .class. 
			 * <p><i>
			 * Checks if the class information is already loaded, read from its class file.
			 * </i>
			 * 
			 * @return <i>true</i> caso as informações da classe já tenham sido carregadas,
			 * <i>false</i> caso contrário
			 * <br><i>true if already loaded, false otherwise</i>
			 * @see org.esfinge.liveprog.reflect.ClassInfo
			 */
			public boolean isLoaded()
			{
				return ( this.classInfo != null );
			}
		}
	}	
}