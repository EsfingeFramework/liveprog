package org.esfinge.liveprog;

import org.esfinge.liveprog.db.DefaultLiveClassPersistence;
import org.esfinge.liveprog.db.ILiveClassPersistence;
import org.esfinge.liveprog.db.ILiveClassVersionManager;
import org.esfinge.liveprog.exception.LiveClassFactoryBuilderException;
import org.esfinge.liveprog.monitor.FileSystemMonitor;
import org.esfinge.liveprog.monitor.ILiveClassFileMonitor;
import org.esfinge.liveprog.util.LiveClassUtils;

/**
 * <p>
 * Builder para a criação de fábricas de objetos de classes dinâmicas.
 * <p><i>
 * Builder to create factories of LiveClass objects. 
 * </i>
 * 
 * @see org.esfinge.liveprog.LiveClassFactory
 */
public class LiveClassFactoryBuilder
{
	// classloader
	private ILiveClassLoader classLoader;
	
	// monitor de arquivos das novas versoes de classes dinamicas
	private ILiveClassFileMonitor fileMonitor;
	
	// gerenciador de persistencia
	private ILiveClassPersistence dbManager;
	
	// gerenciador de versoes de classes dinamicas 
	private ILiveClassVersionManager versionManager;
		
	// modo que a fabrica sera criada
	private boolean factorySafeMode;
	
	// flag para utilizar o monitor de arquivos padrao
	private boolean defaultFileMonintor;
	
	// flag para utilizar o gerenciador de persistencia padrao
	private boolean defaultPersistenceManager;
	
	// inclui ou nao o monitoramento de subdiretorios
	private boolean includeSubdirs;
	
	// caminho para o arquivo de banco de dados
	private String dbFilePath;
	
	// diretorio a ser monitorado
	private String monitorDir;

	
	/**
	 * <p>
	 * Constrói uma nova fábrica de objetos de classes dinâmicas utilizando as configurações padrão.
	 * <br>
	 * Utiliza a implementação padrão do gerenciador de persistência e monitora por arquivos 
	 * das novas versões das classes dinâmicas no mesmo diretório da aplicação, não monitorando seus subdiretórios.
	 * <p><i>
	 * Builds a new factory of LiveClass objects, configuring it using the default parameters.
	 * <br>
	 * Uses the default persistence manager, and watchs for new versions of LiveClass class files 
	 * in the same directory of the running application, excluding the subdirs.
	 * </i>
	 * 
	 * @return uma nova fábrica de objetos de classes dinâmicas, usando as configurações padrão
	 * <br><i>a new factory of LiveClass objects using the default configuration</i>
	 * @throws LiveClassFactoryBuilderException em caso de erro na configuração de algum componente interno
	 * <br><i>if an error occurs when configuring an internal component</i>
	 * @see org.esfinge.liveprog.LiveClassFactory
	 */
	public static LiveClassFactory createDefaultFactory() throws LiveClassFactoryBuilderException
	{
		return ( new LiveClassFactoryBuilder().usingDefaultFileMonitor().usingDefaultPersistenceManager().build() );
	}	
	
	/**
	 * <p>
	 * Constrói um novo Builder para a criação da fábrica de objetos de classes dinâmicas.
	 * <p><i>
	 * Constructs a new Builder to create a factory of LiveClass objects. 
	 * </i>
	 */
	public LiveClassFactoryBuilder()
	{
		this.factorySafeMode = false;
		this.defaultPersistenceManager = false;
		this.defaultFileMonintor = false;
		this.includeSubdirs = false;
	}
	
	/**
	 * <p>
	 * Configura a fábrica para executar em modo seguro.
	 * Executando em modo seguro, os objetos dinâmicos são atualizados 
	 * somente quando a nova versão da classe dinâmica é aceita (commit). 
	 * <p><i>
	 * Sets the factory to execute in safe mode.
	 * When executing in safe mode, 'live' objects are only updated after a commit of the LiveClass new version.
	 * </i>	
	 * 
	 * @return o próximo estágio do processo de construção da fábrica
	 * <br><i>the next stage of the factory building process</i>
	 * @see org.esfinge.liveprog.LiveClassFactory#setSafeMode(boolean)
	 */
	public LiveClassFactoryBuilder inSafeMode()
	{
		this.factorySafeMode = true;
		return ( this );
	}

	/**
	 * <p>
	 * Configura a fábrica para utilizar um classloader customizado para o carregamento de classes dinâmicas.
	 * <p><i>
	 * Sets the factory to use a custom classloader for loading LiveClasses.
	 * </i>	
	 *
	 * @param classLoader classloader customizado para o carregamento de classes dinâmicas
	 * <br><i>custom classloader for loading LiveClasses</i>
	 * @return o próximo estágio do processo de construção da fábrica
	 * <br><i>the next stage of the factory building process</i>
	 * @see org.esfinge.liveprog.ILiveClassLoader
	 */
	public LiveClassFactoryBuilder usingCustomClassLoader(ILiveClassLoader classLoader)
	{
		this.classLoader = classLoader;
		return ( this );
	}
	
	/**
	 * <p>
	 * Utiliza por padrão um monitor que procura pelos arquivos das novas versões das classes dinâmicas 
	 * no mesmo diretório da aplicação, não monitorando seus subdiretórios.
	 * <p><i>
	 * Uses the default file monitor that watchs for new versions of LiveClass class files 
	 * in the same directory of the running application, excluding the subdirs.
	 * </i>
	 * 
	 * @return o próximo estágio do processo de construção da fábrica
	 * <br><i>the next stage of the factory building process</i>
	 * @see org.esfinge.liveprog.monitor.ILiveClassFileMonitor
	 */
	public LiveClassFactoryBuilderPersistence usingDefaultFileMonitor()
	{
		this.defaultFileMonintor = true;
		this.monitorDir = ".";
		return ( new LiveClassFactoryBuilderPersistence() );
	}
	
	/**
	 * <p>
	 * Especifica o diretório a ser monitorado em busca dos arquivos das novas versões das classes dinâmicas.
	 * <p><i>
	 * Sets the directory to watch for new versions of LiveClass class files.
	 * </i>
	 * 
	 * @param dir diretório a ser monitorado
	 * <br><i>the directory to be monitored</i>
	 * @return o próximo estágio do processo de construção da fábrica
	 * <br><i>the next stage of the factory building process</i>
	 * @see org.esfinge.liveprog.monitor.FileSystemMonitor
	 */
	public LiveClassFactoryBuilderFileSystemMonitor monitoringDirectory(String dir)
	{
		this.defaultFileMonintor = true;
		this.monitorDir = dir;
		return ( new LiveClassFactoryBuilderFileSystemMonitor() );
	}

	/**
	 * <p>
	 * Especifica o monitor responsável pela busca dos arquivos das novas versões das classes dinâmicas.
	 * <p><i>
	 * Sets the file monitor to watch for new versions of LiveClass class files.
	 * </i>
	 * 
	 * @param fileMonitor monitor de arquivos de novas versões de classes dinâmicas
	 * <br><i>the monitor to watch for new versions of LiveClass class files</i>
	 * @return o próximo estágio do processo de construção da fábrica
	 * <br><i>the next stage of the factory building process</i>
	 * @see org.esfinge.liveprog.monitor.ILiveClassFileMonitor
	 */
	public LiveClassFactoryBuilderPersistence monitoringFilesThrough(ILiveClassFileMonitor fileMonitor)
	{
		this.defaultFileMonintor = false;
		this.fileMonitor = fileMonitor;
		return ( new LiveClassFactoryBuilderPersistence() );
	}
	
	
	/**
	 * <p>
	 * Configuração do monitor de diretórios.
	 * <p><i>
	 * Settings for the FileSystem monitor.
	 * </i>
	 */
	public class LiveClassFactoryBuilderFileSystemMonitor
	{
		/**
		 * <p>
		 * Inclui os subdiretórios para serem monitorados.
		 * <p><i>
		 * Includes the subdirs to also be monitored.
		 * </i>
		 * 
		 * @return o próximo estágio do processo de construção da fábrica
		 * <br><i>the next stage of the factory building process</i>
		 * @see org.esfinge.liveprog.monitor.FileSystemMonitor#FileSystemMonitor(String, boolean)
		 */
		public LiveClassFactoryBuilderPersistence includingSubdirs()
		{
			includeSubdirs = true;
			return ( new LiveClassFactoryBuilderPersistence() );
		}

		/**
		 * <p>
		 * Exclui os subdiretórios de serem monitorados.
		 * <p><i>
		 * Excludes the subdirs to be monitored.
		 * </i>
		 * 
		 * @return o próximo estágio do processo de construção da fábrica
		 * <br><i>the next stage of the factory building process</i>
		 * @see org.esfinge.liveprog.monitor.FileSystemMonitor#FileSystemMonitor(String, boolean)
		 */
		public LiveClassFactoryBuilderPersistence excludingSubdirs()
		{
			includeSubdirs = false;
			return ( new LiveClassFactoryBuilderPersistence() );
		}
	}
	
	
	/**
	 * <p>
	 * Configuração do gerenciador de persistência.
	 * <p><i>
	 * Settings for the persistence manager.
	 * </i>
	 */
	public class LiveClassFactoryBuilderPersistence
	{		
		/**
		 * <p>
		 * Utiliza o gerenciador de persistência padrão, cujo nome do arquivo onde as informações das classes dinâmicas são persistidas 
		 * chama-se <i>'liveclasses.db'</i> e fica armazenado no mesmo diretório da aplicação.
		 * <p><i>
		 * Uses the default persistence manager, with a database file named 'liveclasses.db' stored in the same directory of the running application.
		 * </i>
		 * 
		 * @return o próximo estágio do processo de construção da fábrica
		 * <br><i>the next stage of the factory building process</i>
		 * @see org.esfinge.liveprog.db.DefaultLiveClassPersistence
		 */
		public LiveClassFactoryBuilderCreate usingDefaultPersistenceManager()
		{
			defaultPersistenceManager = true;
			return ( new LiveClassFactoryBuilderCreate() );
		}
		
		/**
		 * <p>
		 * Especifica o nome e o caminho do arquivo de persistência das classes dinâmicas, 
		 * a ser utilizado pelo gerenciador de persistência padrão.
		 * <p><i>
		 * Sets the database filename and path to be used by the default persistence manager.
		 * </i>
		 * 
		 * @param filePath nome e caminho para o arquivo de base de dados
		 * <br><i>filename and path of the database file</i>
		 * @return o próximo estágio do processo de construção da fábrica
		 * <br><i>the next stage of the factory building process</i>
		 * @see org.esfinge.liveprog.db.DefaultLiveClassPersistence#setDatabaseFilePath(String)
		 */
		public LiveClassFactoryBuilderCreate usingDatabaseFilePath(String filePath)
		{
			defaultPersistenceManager = true;
			dbFilePath = filePath;
			return ( new LiveClassFactoryBuilderCreate() );
		}

		/**
		 * <p>
		 * Especifica o gerenciador de persistência de classes dinâmicas.
		 * <p><i>
		 * Sets the LiveClasses persistence manager.
		 * </i>
		 * 
		 * @param dbManager gerenciador de persistência de classes dinâmicas
		 * <br><i>the persistence manager for LiveClasses</i>
		 * @return o próximo estágio do processo de construção da fábrica
		 * <br><i>the next stage of the factory building process</i>
		 * @see org.esfinge.liveprog.db.ILiveClassPersistence
		 */
		public LiveClassFactoryBuilderCreate usingPersistenceManager(ILiveClassPersistence dbManager)
		{
			defaultPersistenceManager = false;
			LiveClassFactoryBuilder.this.dbManager = dbManager;
			return ( new LiveClassFactoryBuilderCreate() );
		}
	}
	
	
	/**
	 * <p>
	 * Estágio final do processo de construção da fábrica.
	 * <p><i>
	 * Final stage of the factory building process.
	 * </i>
	 */
	public class LiveClassFactoryBuilderCreate
	{
		/**
		 * <p>
		 * Especifica o gerenciador de versões de classes dinâmicas.
		 * <p><i>
		 * Sets the LiveClasses version manager.
		 * </i>  
		 * 
		 * @param versionManager gerenciador de versões de classes dinamicas
		 * <br><i>the version manager for LiveClasses</i>
		 * @return o próximo estágio do processo de construção da fábrica
		 * <br><i>the next stage of the factory building process</i>
		 * @see org.esfinge.liveprog.db.ILiveClassVersionManager
		 */
		public LiveClassFactoryBuilderCreate usingVersionManager(ILiveClassVersionManager versionManager)
		{
			LiveClassFactoryBuilder.this.versionManager = versionManager;
			return ( this );
		}
		
		/**
		 * <p>
		 * Cria a fábrica de objetos de classes dinâmicas, configurando-a conforme os parâmetros informados no builder.
		 * <p><i>
		 * Builds the factory of LiveClass objects, configuring it using the parameters specified in the building process.
		 * </i>
		 *  
		 * @return uma nova fábrica de objetos de classes dinâmicas
		 * <br><i>a new factory of LiveClass objects</i>
		 * @throws LiveClassFactoryBuilderException em caso de erro na configuração de algum componente interno
		 * <br><i>if an error occurs when configuring an internal component</i>
		 * @see org.esfinge.liveprog.LiveClassFactory
		 */
		public LiveClassFactory build() throws LiveClassFactoryBuilderException
		{
			try
			{
				// verifica se esta utilizando o monitor de arquivos padrao
				if ( defaultFileMonintor || (fileMonitor == null) )
					fileMonitor = new FileSystemMonitor(monitorDir, includeSubdirs);

				// verifica se esta utilizando o gerenciador de persistencia padrao
				if ( defaultPersistenceManager || (dbManager == null) )
				{
					if ( dbFilePath != null )
						DefaultLiveClassPersistence.setDatabaseFilePath(dbFilePath);
					
					dbManager = DefaultLiveClassPersistence.getInstance();
				}
				
				// cria a fabrica
				LiveClassFactory factory = new LiveClassFactory(classLoader, dbManager, factorySafeMode);

				// configura os observadores
				if ( versionManager != null )
					versionManager.addObserver(factory);
				
				fileMonitor.addObserver(factory);
				fileMonitor.start();
				
				return ( factory );
			}
			catch ( Exception e )
			{
				// log: erro
				LiveClassUtils.logError("Erro ao criar fabrica de objeto dinamicos!");
				LiveClassUtils.logException(e);
				
				throw new LiveClassFactoryBuilderException("Unable to create a new LiveClassFactory object!", e);
			}
		}
	}
}
