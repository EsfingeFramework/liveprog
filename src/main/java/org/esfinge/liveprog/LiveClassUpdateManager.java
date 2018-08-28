package org.esfinge.liveprog;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.esfinge.liveprog.exception.IncompatibleLiveClassException;
import org.esfinge.liveprog.instrumentation.AccessModifier;
import org.esfinge.liveprog.instrumentation.ClassInfo;
import org.esfinge.liveprog.instrumentation.InstrumentationService;
import org.esfinge.liveprog.instrumentation.MethodInfo;
import org.esfinge.liveprog.monitor.ILiveClassFileMonitorObserver;
import org.esfinge.liveprog.util.Utils;

/**
 * Gerenciador de atualizacoes de classes dinamicas.
 */
class LiveClassUpdateManager implements ILiveClassFileMonitorObserver
{
	// lista de observadores a serem notificados das 
	// novas versoes das classes dinamicas criadas
	private List<ILiveClassUpdateObserver> observers;	
	
	// lista de mapas de classes (para classes que possuem classes internas)
	public List<ClassMap> classMapList;
	

	/**
	 * Cria um novo gerenciador de atualizacoes de classes dinamicas. 
	 */
	LiveClassUpdateManager()
	{
		this.classMapList = new ArrayList<ClassMap>();
		this.observers = new ArrayList<ILiveClassUpdateObserver>();
	}
	
	@Override
	public synchronized void classFileUpdated(File classFile)
	{
		try
		{
			// obtem as informacoes da classe do arquivo recebido
			ClassInfo classInfo = InstrumentationService.inspect(classFile);
			
			// TODO: debug..
			System.out.println("FILE MANAGER >> Classe recebida: " + classInfo.getName());
			
			
			// mapeamento das classes que compoem a classe do arquivo recebido
			ClassMap classMap = this.findClassMap(classInfo.getName());
			classMap.getClassNode(classInfo.getName()).setClassInfo(classInfo);
			
			// verifica se as classes internas ja foram mapeadas
			if ( classInfo.hasInnerClasses() )
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
				if (! classMap.containsClass(classInfo.getOuterClassName()) ) 
				{
					// mapa que contem a classe externa
					ClassMap outerClassMap = this.findClassMap(classInfo.getOuterClassName());
					
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
				
				// TODO: debug..
				System.out.println("FILE MANAGER >> Nova versao da classe dinamica: " + classInfo.getName());

				try
				{
					// verifica a compatibilidade da nova classe com a versao original
					this.checkClassCompatibility(classInfo);
					
					// cria a nova versao da classe
					ClassInfo newClassInfo = this.createNewVersion(classInfo);
					
					// notifica os observadores
					this.notifyObservers(classInfo.getName(), newClassInfo);
				}			
				catch ( Exception e )
				{
					// TODO: debug..
					System.out.println("FILE MANAGER >> Erro ao criar nova versao da classe dinamica: " + classInfo.getName());
					
					e.printStackTrace();
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
			// TODO: debug..
			e.printStackTrace();
		}
	}
	
	/**
	 * Adiciona um observador para ser notificado quando uma nova versao de classe dinamica for criada.
	 * 
	 * @param observer interessado na notificacao quando novos arquivos forem encontrados
	 * @see org.esfinge.liveprog.ILiveClassUpdateObserver
	 */	
	public void addObserver(ILiveClassUpdateObserver observer)
	{
		this.observers.add(observer);
	}

	/**
	 * Remove um observador da lista de observadores.
	 * 
	 * @param observer observador a ser removido da lista de observadores
	 * @see org.esfinge.liveprog.ILiveClassUpdateObserver
	 */
	public void removeObserver(ILiveClassUpdateObserver observer)
	{
		this.observers.remove(observer);
	}
	
	/**
	 * Verifica a compatibilidade da nova versao com a versao original da classe dinamica.
	 * 
	 * A nova classe deve ter a mesma interface publica da versao original.
	 * 
	 * @param newClassInfo as informacoes da nova classe
	 * @throws IncompatibleLiveClassException se a nova classe nao for compativel com a versao antiga
	 */
	private void checkClassCompatibility(ClassInfo newClassInfo) throws IncompatibleLiveClassException
	{
		try
		{
			// obtem a classe original
			Class<?> origClass = Class.forName(newClassInfo.getName());
			
			// obtem os metodos publicos da nova classe
			List<MethodInfo> newClassMethods = Utils.filterFromCollection(newClassInfo.getMethodsInfo(), 
														m -> m.getAccessPermission() == AccessModifier.PUBLIC);
			
			// verifica se a nova classe possui os metodos publicos da classe original
			for ( Method origMethod : origClass.getDeclaredMethods() )
			{
				// ignora os metodos nao publicos
				if (! Modifier.isPublic(origMethod.getModifiers()) )
					continue;

				// tenta obter o mesmo metodo na nova classe
				boolean cont = false;
				List<MethodInfo> newMethods = Utils.filterFromCollection(newClassMethods, 
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
			
			// TODO: verificar os campos publicos???
			
			
			//TODO: debug..
			System.out.println("FILE MANAGER >> Nova versao da classe dinamica compativel com a original!");
		}
		catch ( Exception e )
		{
			throw new IncompatibleLiveClassException("New class is incompatible with original class public interface!", e);
		}
	}
	
	/**
	 * Instrumenta e cria a nova versao da classe.
	 * 
	 * @param classInfo as informacoes da classe atualizada, carregadas do arquivo .class
	 * @return as informacoes da nova versao da classe
	 */
	private ClassInfo createNewVersion(ClassInfo classInfo)
	{
		// nome da nova versao da classe
		String newName = String.format("%s_%X", classInfo.getName(), System.currentTimeMillis());
		
		// instrumenta para a nova versao da classe
		return ( InstrumentationService.transform(classInfo, classInfo.getName(), newName) );
	}
	
	/**
	 * Notifica os observadores que uma nova versao de classe dinamica foi criada.
	 * 
	 * @param originalClassName o nome da classe dinamica original
	 * @param newLiveClassInfo as informacoes da nova versao da classe dinamica
	 */
	private void notifyObservers(String originalClassName, ClassInfo newLiveClassInfo)
	{
		this.observers.forEach(obs -> obs.liveClassUpdated(originalClassName, newLiveClassInfo));
	}

	/**
	 * Retorna o mapa de classes que contem a classe informada.
	 * 
	 * Se nao encontrar nenhum que a contenha, cria um novo para ela.
	 * 
	 * @param className o nome da classe
	 * @return o mapa de classes que contem a classe informada
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
	 * Combina dois mapas de classes relacionados.
	 * 
	 * @param cm1 mapa de classes relacionadas
	 * @param cm2 mapa de classes relacionadas
	 * @return um novo mapa de classes relacionadas 
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
	 * Classe auxiliar para mapeamento de classes relacionadas.
	 * 
	 * Quando uma classe dinamica possui classes internas, todos os arquivos compilados
	 * dessa classe devem ser manipulados e carregados em conjunto. 
	 */
	private class ClassMap
	{
		// mapa da classe dinamica e suas classes internas
		private Map<String,ClassNode> classMap;

		
		/**
		 * Construtor padrao.
		 */
		public ClassMap()
		{
			this.classMap = new HashMap<String,ClassNode>();
		}
		
		/**
		 * Adiciona um novo node com informacoes da classe.
		 * 
		 * @param c node com informacoes da classe
		 */
		public void addClassNode(ClassNode c)
		{
			this.classMap.put(c.getClassName(), c);

		}
		
		/**
		 * Retorna o node com informacoes da classe informada.
		 * 
		 * @param className o nome da classe 
		 * @return o nodo com informacoes da classe
		 */
		public ClassNode getClassNode(String className)
		{
			return ( this.classMap.get(className) );
		}
		
		/**
		 * Verifica se contem informacoes da classe informada.
		 * 
		 * @param className o nome da classe 
		 * @return <b>true</b> se a classe constar no mapa de classes,
		 * <b>false</b> caso contrario
		 */
		public boolean containsClass(String className)
		{
			return ( this.classMap.containsKey(className) );
		}
		
		/**
		 * Verifica se todas as classes ja foram mapeadas com suas informacoes.
		 * 
		 * Se o mapa estiver completo, pode-se validar a classe dinamica 
		 * e suas classes internas e carrega-las em sua nova versao.
		 * 
		 * @return <b>true</b> se o mapa de classes estiver completo,
		 * <b>false</b> caso contrario
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
		 * Retorna a classe raiz do mapeamento de classes.
		 * 
		 * @return a classe raiz do mapeamento de classes
		 */
		public ClassInfo getRootClass()
		{
			return ( Utils.getFromCollection(this.classMap.values(), n -> !n.getClassInfo().isInnerClass()).getClassInfo() );
		}

		/**
		 * Organiza o mapeamento das classes internas, adicionando-as na classe raiz.
		 */
		public void doArrange()
		{
			// verifica se o mapeamento de classes ja esta completo
			if (! this.isComplete() )
				return;
			
			// obtem a classe raiz
			ClassInfo rootClassInfo = this.getRootClass();
			
			// adiciona as classes internas
			this.arrangeInnerClasses(rootClassInfo);
		}
		
		/**
		 * Adiciona as informacoes das classes internas da classe informada.
		 * 
		 * @param classInfo a classe cujas informacoes das classes internas serao adicionadas
		 * @return a classe com as informacoes das classes internas adicionadas
		 */
		private ClassInfo arrangeInnerClasses(ClassInfo classInfo)
		{
			for ( String innerClassName : classInfo.getInnerClassNames() )
				classInfo.addInnerClassInfo(this.arrangeInnerClasses(this.getClassNode(innerClassName).getClassInfo()));
			
			return ( classInfo );
		}
	}
	
	
	/**
	 * Classe auxiliar para mapeamento de classes relacionadas.
	 * 
	 * Representa um node no grafo de classes relacionadas.
	 */
	private class ClassNode
	{
		// nome da classe
		private String className;
		
		// informacoes da classe
		private ClassInfo classInfo;

		
		/**
		 * Retorna o nome da classe.
		 * 
		 * @return o nome da classe
		 */
		public String getClassName()
		{
			return className;
		}

		/**
		 * Atribui o nome da classe.
		 * 
		 * @param className o nome da classe
		 */
		public void setClassName(String className)
		{
			this.className = className;
		}

		/**
		 * Retorna as informacoes da classe.
		 * 
		 * @return as informacoes da classe
		 * @see org.esfinge.liveprog.instrumentation.ClassInfo
		 */
		public ClassInfo getClassInfo()
		{
			return classInfo;
		}

		/**
		 * Atribui as informacoes da classe.
		 * 
		 * @param classInfo as informacoes da classe
		 * @see org.esfinge.liveprog.instrumentation.ClassInfo
		 */
		public void setClassInfo(ClassInfo classInfo)
		{
			this.classInfo = classInfo;
		}

		/**
		 * Verifica se as informacoes da classe ja foram carregadas.
		 * 
		 * As informacoes sao carregadas quando o seu arquivo .class 
		 * eh encontrado pelo monitor e recebido pelo LiveClassUpdateManager.
		 * 
		 * @return <b>true</b> caso as informacoes da classe ja tenham sido carregadas,
		 * <b>false</b> caso contrario
		 * @see org.esfinge.liveprog.instrumentation.ClassInfo
		 */
		public boolean isLoaded()
		{
			return ( this.classInfo != null );
		}
	}
}
