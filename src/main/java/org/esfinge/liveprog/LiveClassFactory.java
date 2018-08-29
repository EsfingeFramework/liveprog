package org.esfinge.liveprog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.esfinge.liveprog.db.ILiveClassDB;
import org.esfinge.liveprog.db.ILiveClassDBVersionObserver;
import org.esfinge.liveprog.exception.IncompatibleLiveClassException;
import org.esfinge.liveprog.instrumentation.ClassInfo;
import org.esfinge.liveprog.instrumentation.InstrumentationService;

import net.sf.cglib.proxy.Enhancer;

/**
 * Factory para criacao de objetos de classes dinamicas.
 * 
 * @see org.esfinge.liveprog.annotation.LiveClass
 */
public class LiveClassFactory implements ILiveClassUpdateObserver, ILiveClassDBVersionObserver
{
	// mapa dos proxies criados para classes dinamicas em modo teste
	private Map<String, List<ILiveClassObserver>> mapProxiesTest;
	
	// mapa dos proxies criados para classes dinamicas em modo producao
	private Map<String, List<ILiveClassObserver>> mapProxiesProd;
	
	// mapa dos observadores externos de classes dinamicas (sem ser os proxies)
	private Map<String, List<ILiveClassObserver>> mapObservers;	
	
	// cache das classes dinamicas atualmente criadas em modo teste
	private Map<String, Class<?>> cacheLiveClassesTest;
	
	// cache das classes dinamicas atualmente criadas em modo producao
	private Map<String, Class<?>> cacheLiveClassesProd;
	
	// carregador de classes dinamicas
	private LiveClassLoader classLoader;
	
	// gerenciador do banco de dados
	private ILiveClassDB dbManager;
	
	// indica se a factory esta rodando em modo de teste ou de producao
	private boolean factoryTestMode;
	

	/**
	 * Cria uma nova fabrica para a criacao de objetos de classes dinamicas.
	 * 
	 * @param dbManager gerenciador para a persistencia de classes dinamicas
	 * @throws Exception caso ocorra algum erro interno de inicializacao
	 */
	LiveClassFactory(ILiveClassDB dbManager) throws Exception
	{
		
		this(dbManager, false);
	}

	/**
	 * Cria uma nova fabrica para a criacao de objetos de classes dinamicas.
	 * 
	 * @param dbManager gerenciador para a persistencia de classes dinamicas
	 * @param testMode <b>true</b> para a fabrica executar em modo de testes - usa sempre a versao mais recente da classe dinamica,
	 * <b>false</b> para executar em modo de producao
	 * @throws Exception caso ocorra algum erro interno de inicializacao
	 */
	LiveClassFactory(ILiveClassDB dbManager, boolean testMode) throws Exception
	{
		this.classLoader = new LiveClassLoader();
		this.mapProxiesTest = new HashMap<String, List<ILiveClassObserver>>();
		this.mapProxiesProd = new HashMap<String, List<ILiveClassObserver>>();
		this.mapObservers = new HashMap<String, List<ILiveClassObserver>>();
		this.cacheLiveClassesTest = new HashMap<String, Class<?>>();
		this.cacheLiveClassesProd = new HashMap<String, Class<?>>();
		this.dbManager = dbManager;
		this.factoryTestMode = testMode;
	}
	
	/**
	 * Cria um novo objeto de uma classe dinamica.
	 * 
	 * A classe deve atender aos requisitos para ser considerada uma classe dinamica.
	 * 
	 * @param objClass a classe cujo objeto dinamico sera criado
	 * @return um novo objeto dinamico
	 * @throws IncompatibleLiveClassException caso a classe nao seja compativel com os requisitos de classes dinamicas
	 * @see org.esfinge.liveprog.annotation.LiveClass
	 * @see org.esfinge.liveprog.instrumentation.InstrumentationService#checkValidLiveClass(Class)
	 */
	public <L> L createLiveObject(Class<L> objClass) throws IncompatibleLiveClassException
	{
		return ( this.createLiveObject(objClass, this.factoryTestMode || false) );
	}

	/**
	 * Cria um novo objeto de uma classe dinamica em modo de testes.
	 * 
	 * A classe deve atender aos requisitos para ser considerada uma classe dinamica.
	 * 
	 * @param objClass a classe cujo objeto dinamico sera criado
	 * @return um novo objeto dinamico
	 * @throws IncompatibleLiveClassException caso a classe nao seja compativel com os requisitos de classes dinamicas
	 * @see org.esfinge.liveprog.annotation.LiveClass
	 * @see org.esfinge.liveprog.instrumentation.InstrumentationService#checkValidLiveClass(Class)
	 */
	public <L> L createLiveObjectInTestMode(Class<L> objClass) throws IncompatibleLiveClassException
	{
		return ( this.createLiveObject(objClass, true) );
	}
	
	@Override
	public void liveClassUpdated(String liveClassName, ClassInfo newLiveClassInfo)
	{
		try
		{
			// salva a nova versao (de testes) no BD
			this.dbManager.saveLiveClass(liveClassName, newLiveClassInfo);
			
			// TODO: debug..
			System.out.println("FACTORY >> Classe dinamica atualizada: " + liveClassName);
			
			// carrega a classe dinamica
			Class<?> newLiveClass = this.classLoader.loadLiveClass(newLiveClassInfo);
			
			// TODO: debug..
			System.out.println("FACTORY >> Classe dinamica carregada: " + newLiveClass.getName());

			// atualiza o cache (de testes)
			this.cacheLiveClassesTest.put(liveClassName, newLiveClass);
			
			// notifica os proxies (modo teste)
			this.notifyProxies(liveClassName, newLiveClass, true);
			
			// notifica os observadores externos
			this.notifyExternalObservers(liveClassName, newLiveClass);
		}
		catch ( Exception e )
		{
			// TODO: debug..
			System.out.println("FACTORY >> Erro ao carregar nova versao da classe dinamica: " + liveClassName);
			
			e.printStackTrace();
		}
	}
	
	@Override
	public void liveClassCommitted(String className)
	{
		// verifica se a factory esta em modo de testes
		// se estiver, todos os objetos sao criados em modo teste, utilizando a versao mais atual
		if ( this.factoryTestMode )
			return;
		
		try
		{
			// obtem as informacoes da classe dinamica (modo producao)
			ClassInfo liveClassInfo = this.dbManager.getLiveClass(className, false);
			
			// carrega a classe dinamica
			Class<?> newLiveClass = this.classLoader.loadLiveClass(liveClassInfo);
			
			// TODO: debug..
			System.out.println("FACTORY >> Classe dinamica - commit: " + className);

			// atualiza o cache (de producao)
			this.cacheLiveClassesProd.put(className, newLiveClass);
			
			// notifica os proxies (modo producao)
			this.notifyProxies(className, newLiveClass, false);
			
			// notifica os observadores externos
			this.notifyExternalObservers(className, newLiveClass);
		}
		catch ( Exception e )
		{
			// TODO: debug..
			System.out.println("FACTORY >> Erro ao carregar commit da classe dinamica: " + className);
			
			e.printStackTrace();
		}
	}

	@Override
	public void liveClassRolledBack(String className)
	{
		try
		{
			// obtem as informacoes da classe dinamica (modo producao)
			ClassInfo liveClassInfo = this.dbManager.getLiveClass(className, false);
			
			// carrega a versao da classe dinamica
			Class<?> newLiveClass = this.classLoader.loadLiveClass(liveClassInfo);
			
			// TODO: debug..
			System.out.println("FACTORY >> Classe dinamica - rollback: " + className);

			// atualiza os caches
			this.cacheLiveClassesTest.put(className, newLiveClass);
			
			// notifica os proxies
			this.notifyProxies(className, newLiveClass, true);
			
			// verifica se a factory esta em modo producao
			if (! this.factoryTestMode )
			{
				this.cacheLiveClassesProd.put(className, newLiveClass);
				this.notifyProxies(className, newLiveClass, false);
			}
			
			// notifica os observadores externos
			this.notifyExternalObservers(className, newLiveClass);
		}
		catch ( Exception e )
		{
			// TODO: debug..
			System.out.println("FACTORY >> Erro ao carregar rollback da classe dinamica: " + className);
			
			e.printStackTrace();
		}
	}

	/**
	 * Adiciona um observador para que seja notificado quando a classe dinamica 
	 * for atualizada para uma nova versao.
	 * 
	 * @param clazz a classe dinamica a ser observada
	 * @param observer interessado na notificacao quando a classe for atualizada
	 * @return <b>true</b> se o observador foi adicionado na lista de observadores
	 * da classe informada, <b>false</b> caso contrario (i.e. caso a classe informada
	 * nao seja compativel com os requisitos de classes dinamicas)
	 * @see org.esfinge.liveprog.ILiveClassObserver
	 * @see org.esfinge.liveprog.instrumentation.InstrumentationService#checkValidLiveClass(Class)
	 */
	public boolean addLiveClassObserver(Class<?> clazz, ILiveClassObserver observer)
	{
		try
		{
			// recupera a lista de observadores da classe informada
			List<ILiveClassObserver> lstObservers = this.mapObservers.get(clazz.getName());
			
			if ( lstObservers == null )
			{
				// verifica se a classe cumpre os requisitos de classes dinamicas
				InstrumentationService.checkValidLiveClass(clazz);
				
				// cria uma lista de observadores para a classe dinamica informada
				lstObservers = new ArrayList<ILiveClassObserver>();
			}
			
			// adiciona o novo observador
			lstObservers.add(observer);
			
			// atualiza o mapa
			this.mapObservers.put(clazz.getName(), lstObservers);
			
			return ( true );
		}
		catch ( IncompatibleLiveClassException e )
		{
			// TODO: debug..
			e.printStackTrace();
			
			return ( false );
		}
	}
	
	/**
	 * Remove um observador da lista de observadores da classe dinamica.
	 * 
	 * @param clazz a classe dinamica observada
	 * @param observer observador a ser removido da lista de observadores registrados para a classe dinamica
	 * @see org.esfinge.liveprog.ILiveClassObserver
	 */
	public void removeLiveClassObserver(Class<?> clazz, ILiveClassObserver observer)
	{
		// recupera a lista de observadores da classe informada
		List<ILiveClassObserver> lstObservers = this.mapObservers.get(clazz.getName());
		
		// remove o observador
		if ( lstObservers != null )
			lstObservers.remove(observer);
	}
	
	/**
	 * Cria o objeto da classe dinamica informada.
	 * 
	 * @param objClass a classe cujo objeto dinamico sera criado
	 * @param testMode <b>true</b> para criar o objeto em modo de testes - usa a versao mais recente da classe dinamica,
	 * <b>false</b> para criar o objeto em modo de producao
	 * @return um novo objeto dinamico
	 * @throws IncompatibleLiveClassException caso a classe nao seja compativel com os requisitos de classes dinamicas
	 * @see org.esfinge.liveprog.annotation.LiveClass
	 * @see org.esfinge.liveprog.instrumentation.InstrumentationService#checkValidLiveClass(Class)
	 */
	@SuppressWarnings("unchecked")
	private <L> L createLiveObject(Class<L> objClass, boolean testMode) throws IncompatibleLiveClassException
	{
		try
		{
			// cache das classes dinamicas ja criadas
			Map<String,Class<?>> cacheLiveClasses = testMode ? this.cacheLiveClassesTest : this.cacheLiveClassesProd;

			// nome da classe
			String className = objClass.getName();			
			
			// verifica se a classe esta no cache
			Class<?> liveClass = cacheLiveClasses.get(className);
			
			if ( liveClass == null )
			{
				// verifica se a classe cumpre os requisitos de classes dinamicas
				InstrumentationService.checkValidLiveClass(objClass);				
				
				// obtem as informacoes da classe armazenadas no BD, se houver
				ClassInfo liveClassInfo = this.dbManager.getLiveClass(className, testMode);
				
				// verifica se ja tem alguma versao salva no banco
				if ( liveClassInfo == null )
				{
					// primeiro uso da classe dinamica
					liveClassInfo = InstrumentationService.inspect(objClass);
					
					// salva no banco de dados
					this.dbManager.saveLiveClass(liveClassInfo.getName(), liveClassInfo);
				}
				
				// carrega a versao da classe dinamica
				liveClass = this.classLoader.loadLiveClass(liveClassInfo);

				// salva no cache	
				cacheLiveClasses.put(className, liveClass);
			}
			
			// cria o objeto da classe dinamica
			Object liveObj = liveClass.newInstance();
			
			// cria um proxy para o objeto da classe dinamica
			LiveClassProxy proxy = new LiveClassProxy(liveObj);
			Enhancer e = new Enhancer();
			e.setSuperclass(objClass);
			e.setInterfaces(ClassUtils.getAllInterfaces(objClass).toArray(new Class[0]));
			e.setCallback(proxy);
			
			// registra o proxy para ser notificado quando a classe dinamica for atualizada
			this.registerProxy(className, proxy, testMode);

			return ( (L) e.create() );
		}
		catch ( Exception e)
		{
			// problemas ao criar objeto dinamico
			throw new IncompatibleLiveClassException("Unable to create a new live object!", e);
		}
	}
	
	/**
	 * Registra os proxies criados para que os mesmos sejam notificados quando 
	 * a classe dinamica for atualizada para uma nova versao.
	 *  
	 * @param className o nome da classe dinamica
	 * @param proxy o proxy para a classe dinamica
	 * @param testMode <b>true</b> para proxy de objeto criado em modo de testes - usa a versao mais recente da classe dinamica,
	 * <b>false</b> para proxy de objeto criado em modo de producao
	 * @see org.esfinge.liveprog.ILiveClassObserver
	 */
	private void registerProxy(String className, ILiveClassObserver proxy, boolean testMode)	
	{
		// mapa dos proxies
		Map<String, List<ILiveClassObserver>> mapProxies = testMode ? this.mapProxiesTest : this.mapProxiesProd;
		
		// recupera a lista de proxies da classe informada
		List<ILiveClassObserver> lstProxies = mapProxies.get(className);
		
		if ( lstProxies == null )
			lstProxies = new ArrayList<ILiveClassObserver>();
		
		// adiciona o novo proxy
		lstProxies.add(proxy);

		// atualiza o mapa
		mapProxies.put(className, lstProxies);
	}
	
	/**
	 * Notifica os proxies de uma nova versao de classe dinamica.
	 * 
	 * @param className o nome da classe dinamica
	 * @param newLiveClass a nova versao da classe dinamica
	 * @param testMode <b>true</b> para notificar os proxies de objetos criados em modo de testes - usa a versao mais recente da classe dinamica,
	 * <b>false</b> para notificar os proxies de objetos criados em modo de producao
	 */
	private void notifyProxies(String className, Class<?> newLiveClass, boolean testMode)
	{
		// mapa dos proxies
		Map<String, List<ILiveClassObserver>> mapProxies = testMode ? this.mapProxiesTest : this.mapProxiesProd;

		if ( mapProxies.containsKey(className) )
			mapProxies.get(className).forEach(obs -> obs.classReloaded(newLiveClass));
	}
	
	/**
	 * Notifica os observadores externos de uma nova versao de classe dinamica.
	 * 
	 * @param className o nome da classe dinamica
	 * @param newLiveClass a nova versao da classe dinamica
	 */
	private void notifyExternalObservers(String className, Class<?> newLiveClass)
	{
		if ( this.mapObservers.containsKey(className) )
			this.mapObservers.get(className).forEach(obs -> obs.classReloaded(newLiveClass));
	}
}