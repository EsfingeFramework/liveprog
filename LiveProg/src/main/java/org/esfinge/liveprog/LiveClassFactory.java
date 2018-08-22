package org.esfinge.liveprog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.esfinge.liveprog.db.LiveClassDB;
import org.esfinge.liveprog.exception.IncompatibleLiveClassException;
import org.esfinge.liveprog.instrumentation.ClassInfo;
import org.esfinge.liveprog.instrumentation.InstrumentationService;

import net.sf.cglib.proxy.Enhancer;

/**
 * Factory para criacao de objetos de classes dinamicas.
 * 
 * @see org.esfinge.liveprog.annotation.LiveClass
 */
public class LiveClassFactory implements ILiveClassUpdateObserver
{
	// mapa dos proxies criados para cada tipo de classe dinamica
	private Map<String, List<ILiveClassObserver>> mapProxies;
	
	// mapa dos observadores externos de classes dinamicas (sem ser os proxies)
	private Map<String, List<ILiveClassObserver>> mapObservers;	
	
	// cache das classes dinamicas atualmente criadas
	private Map<String, Class<?>> cacheLiveClasses;
	
	// carregador de classes dinamicas
	private LiveClassLoader classLoader;
	
	// indica se a aplicacao esta rodando em modo de teste ou de producao
	private boolean testMode;


	/**
	 * Cria uma nova fabrica para criacao de objetos de classes dinamicas.
	 * 
	 * Assume que a aplicacao esta rodando em modo de producao.
	 * 
	 * @throws Exception caso ocorra algum erro interno de inicializacao 
	 */
	LiveClassFactory() throws Exception
	{
		this(false);
	}
	
	/**
	 * Cria uma nova fabrica para a criacao de objetos de classes dinamicas.
	 * 
	 * @param testMode <b>true</b> se a aplicacao estiver rodando em modo de testes - usa a versao mais recente da classe dinamica,
	 * <b>false</b> se a aplicacao estiver rodando em modo de producao
	 * @throws Exception caso ocorra algum erro interno de inicializacao
	 */
	LiveClassFactory(boolean testMode) throws Exception
	{
		this.classLoader = new LiveClassLoader();
		this.mapProxies = new HashMap<String, List<ILiveClassObserver>>();
		this.mapObservers = new HashMap<String, List<ILiveClassObserver>>();
		this.cacheLiveClasses = new HashMap<String, Class<?>>();
		this.testMode = testMode;
		
		// TODO: debug..
		System.out.format("FACTORY >> Iniciando em modo %s!\n", this.testMode ? "TESTER" : "PRODUCAO");
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
	@SuppressWarnings("unchecked")
	public <L> L createLiveObject(Class<L> objClass) throws IncompatibleLiveClassException
	{
		try
		{
			// nome da classe
			String className = objClass.getName();

			// verifica se a classe esta no cache
			Class<?> liveClass = this.cacheLiveClasses.get(className);
			
			if ( liveClass == null )
			{
				// verifica se a classe cumpre os requisitos de classes dinamicas
				InstrumentationService.checkValidLiveClass(objClass);				
				
				// obtem as informacoes da classe armazenadas no BD, se houver
				ClassInfo liveClassInfo = LiveClassDB.getInstance().getLiveClassInfo(className, this.testMode);
				
				// verifica se ja tem alguma versao salva no banco
				if ( liveClassInfo == null )
				{
					// primeiro uso da classe dinamica
					liveClassInfo = InstrumentationService.inspect(objClass);
					
					// salva no banco de dados
					LiveClassDB.getInstance().saveLiveClassInfo(liveClassInfo);
				}
				
				// carrega a versao da classe dinamica
				liveClass = this.classLoader.loadLiveClass(liveClassInfo);

				// salva no cache
				this.cacheLiveClasses.put(className, liveClass);
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
			this.registerProxy(className, proxy);

			return ( (L) e.create() );
		}
		catch ( Exception e)
		{
			// problemas ao criar objeto dinamico
			throw new IncompatibleLiveClassException("Unable to create a new live object!", e);
		}
	}
	
	@Override
	public void liveClassUpdated(String liveClassName, ClassInfo newLiveClassInfo)
	{
		try
		{
			// salva a nova versao no BD
			LiveClassDB.getInstance().updateLiveClassInfo(liveClassName, newLiveClassInfo);
			
			// TODO: debug..
			System.out.println("FACTORY >> Classe dinamica atualizada: " + liveClassName);
			
			// verifica se esta rodando em modo teste
			if ( this.testMode )
			{
				// carrega a versao da classe dinamica
				Class<?> newLiveClass = this.classLoader.loadLiveClass(newLiveClassInfo);
				
				// TODO: debug..
				System.out.println("FACTORY >> Classe dinamica carregada: " + newLiveClass.getName());

				// atualiza o cache
				this.cacheLiveClasses.put(liveClassName, newLiveClass);
				
				// notifica os proxies
				this.notifyProxies(liveClassName, newLiveClass);
				
				// notifica os observadores externos
				this.notifyExternalObservers(liveClassName, newLiveClass);
			}
		}
		catch ( Exception e )
		{
			// TODO: debug..
			System.out.println("FACTORY >> Erro ao carregar nova versao da classe dinamica: " + liveClassName);
			
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
	 * Registra os proxies criados para que os mesmos sejam notificados quando 
	 * a classe dinamica for atualizada para uma nova versao.
	 *  
	 * @param className o nome da classe dinamica
	 * @param proxy o proxy para a classe dinamica
	 * @see org.esfinge.liveprog.ILiveClassObserver
	 */
	private void registerProxy(String className, ILiveClassObserver proxy)	
	{
		// recupera a lista de proxies da classe informada
		List<ILiveClassObserver> lstProxies = this.mapProxies.get(className);
		
		if ( lstProxies == null )
			lstProxies = new ArrayList<ILiveClassObserver>();
		
		// adiciona o novo proxy
		lstProxies.add(proxy);
		
		// atualiza o mapa
		this.mapProxies.put(className, lstProxies);
	}
	
	private void notifyProxies(String className, Class<?> newLiveClass)
	{
		if ( this.mapProxies.containsKey(className) )
			this.mapProxies.get(className).forEach(obs -> obs.classReloaded(newLiveClass));
	}
	
	private void notifyExternalObservers(String className, Class<?> newLiveClass)
	{
		if ( this.mapObservers.containsKey(className) )
			this.mapObservers.get(className).forEach(obs -> obs.classReloaded(newLiveClass));
	}
}