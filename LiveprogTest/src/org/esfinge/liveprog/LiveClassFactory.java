package org.esfinge.liveprog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.esfinge.liveprog.annotation.LiveClass;
import org.esfinge.liveprog.monitor.IMonitor;
import org.esfinge.liveprog.monitor.IMonitorObserver;
import org.esfinge.liveprog.util.ClassInstrumentation;

import net.sf.cglib.proxy.Enhancer;

/**
 * Factory para criacao de objetos de classes dinamicas.
 * 
 * @see org.esfinge.liveprog.annotation.LiveClass
 */
public class LiveClassFactory implements IMonitorObserver
{
	// mapa dos proxies criados para cada tipo de classe dinamica
	private Map<String, List<ILiveClassObserver>> mapProxies;
	
	// mapa dos observadores externos de classes dinamicas (sem ser os proxies)
	private Map<String, List<ILiveClassObserver>> mapObservers;	
	
	// mapa das classes dinamicas atualmente criadas
	private Map<String, Class<?>> mapLiveClasses;
	
	// carregador de classes dinamicas
	private LiveClassLoader classLoader;


	/**
	 * Cria uma nova fabrica para criacao de objetos de classe dinamicas.
	 * 
	 * @throws Exception caso ocorra algum erro interno de inicializacao 
	 * @see org.esfinge.liveprog.monitor.IMonitor
	 */
	public LiveClassFactory(IMonitor monitor) throws Exception
	{
		this.classLoader = new LiveClassLoader();
		this.mapProxies = new HashMap<String, List<ILiveClassObserver>>();
		this.mapObservers = new HashMap<String, List<ILiveClassObserver>>();
		this.mapLiveClasses = new HashMap<String, Class<?>>();
		
		// se registra no monitor para receber notificacoes 
		// de novas versoes de classes dinamicas
		monitor.setObserver(this);
	}
	
	/**
	 * Cria um novo objeto de uma classe dinamica.
	 * 
	 * Se a classe informada no parametro nao for do tipo dinamica,
	 * tenta retornar uma instancia normal da classe.
	 * 
	 * A classe deve ter implementado um construtor padrao (vazio) 
	 * para que possa ser instanciada.
	 * 
	 * @param objClass a classe cujo objeto dinamico sera criado
	 * @return um objeto dinamico da classe informada
	 * @throws Exception caso nao consiga criar uma instancia da classe 
	 * informada como parametro (i.e. se a classe nao possuir um construtor padrao)
	 * @see org.esfinge.liveprog.annotation.LiveClass
	 */
	@SuppressWarnings("unchecked")
	public <L> L createObject(Class<L> objClass) throws Exception
	{
		// nome da classe
		String className = objClass.getName();
		
		// verifica se a classe eh do tipo dinamica
		if ( objClass.isAnnotationPresent(LiveClass.class) )
		{
			// obtem a versao atual, se houver
			Class<?> liveClass = this.mapLiveClasses.get(className);			
			if ( liveClass == null )
			{
				// primeiro uso da classe
				liveClass = objClass;
				
				// salva no mapa de classes dinamicas
				this.mapLiveClasses.put(className, liveClass);
			}
			
			// cria o objeto da classe dinamica
			Object liveObj = liveClass.newInstance();
			
			// cria um proxy para o objeto da classe dinamica
			LiveClassProxy proxy = new LiveClassProxy(liveObj);
			Enhancer e = new Enhancer();
			e.setSuperclass(objClass);
			e.setInterfaces(ClassUtils.getAllInterfaces(objClass).toArray(new Class[0]));
			e.setCallback(proxy);
			
			// registra o proxy para ser notificado quando a classe dinamica mudar
			this.registerProxy(className, proxy);

			return ( (L) e.create() );
		}
		// utiliza a propria classe do ClassLoader padrao
		else
		{
			return ( objClass.newInstance() );
		}
	}
	
	@Override
	public void classFileUpdated(File classFile)
	{
		try
		{
			//
			ClassInstrumentation classInstr = new ClassInstrumentation(classFile);
			
			// tenta carregar a nova versao da classe
			Class<?> newClass = this.classLoader.loadUpdatedClass(classFile);
			
			if ( newClass != null )
			{
				// TODO: debug
				System.out.println("FACTORY >> " + "Classe atualizada: " + classInstr.getSimpleClassName() + "[" + newClass + "]");
				
				// salva a nova versao da classe
				this.mapLiveClasses.put(classInstr.getClassName(), newClass);
				
				// notifica os proxies que a classe foi atualizada
				if ( this.mapProxies.containsKey(classInstr.getClassName()) )
					for ( ILiveClassObserver proxy : this.mapProxies.get(classInstr.getClassName()) )
						proxy.classReloaded(newClass);
				
				// notifica os observadores que a classe foi atualizada
				if ( this.mapObservers.containsKey(classInstr.getClassName()) )
					for ( ILiveClassObserver observer : this.mapObservers.get(classInstr.getClassName()) )
						observer.classReloaded(newClass);
			}
		}
		catch ( Exception e )
		{
			// TODO: debug
			System.out.println("FACTORY >> " + "Erro ao carregar classe: " + classFile.getName());
			e.printStackTrace();
		}
	}
	
	/**
	 * Adiciona um observador para que seja notificado quando a classe dinamica 
	 * for atualizada para uma nova versao.
	 * 
	 * @param clazz a classe dinamica a ser observada
	 * @param observer interessado na notificacao quando a classe for atualizacao
	 * @see org.esfinge.liveprog.ILiveClassObserver
	 */
	public void addLiveClassObserver(Class<?> clazz, ILiveClassObserver observer)
	{
		// verifica se a classe eh do tipo dinamica
		if (! clazz.isAnnotationPresent(LiveClass.class) )
			return;

		// recupera a lista de observadores da classe informada
		List<ILiveClassObserver> lstObservers = this.mapObservers.get(clazz.getName());
		
		if ( lstObservers == null )
			lstObservers = new ArrayList<ILiveClassObserver>();
		
		// adiciona o novo observador
		lstObservers.add(observer);
		
		// atualiza o mapa
		this.mapObservers.put(clazz.getName(), lstObservers);
	}
	
	/**
	 * Remove um observador registrado para receber notificacoes quando uma classe dinamica 
	 * eh atualizada para uma nova versao.
	 * 
	 * @param clazz a classe dinamica observada
	 * @param observer observador a ser removido da lista de observadores registrados para a classe dinamica
	 * @see org.esfinge.liveprog.ILiveClassObserver
	 */
	public void removeLiveClassObserver(Class<?> clazz, ILiveClassObserver observer)
	{
		// verifica se a classe eh do tipo dinamica
		if (! clazz.isAnnotationPresent(LiveClass.class) )
			return;

		// recupera a lista de observadores da classe informada
		List<ILiveClassObserver> lstObservers = this.mapObservers.get(clazz.getName());
		
		if ( lstObservers == null )
			return;
		
		// remove o observador
		lstObservers.remove(observer);
		
		// atualiza o mapa
		this.mapObservers.put(clazz.getName(), lstObservers);
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
}
