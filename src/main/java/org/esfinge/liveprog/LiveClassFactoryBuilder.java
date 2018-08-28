package org.esfinge.liveprog;

import org.esfinge.liveprog.db.DefaultLiveClassDB;
import org.esfinge.liveprog.db.ILiveClassDB;
import org.esfinge.liveprog.db.ILiveClassDBVersionManager;
import org.esfinge.liveprog.monitor.FileSystemMonitor;
import org.esfinge.liveprog.monitor.ILiveClassFileMonitor;

/**
 * Builder para a criacao da fabrica de criacao de objetos de classes dinamicas.
 */
public class LiveClassFactoryBuilder
{
	// modo que a fabrica sera criada
	private boolean testMode;
	
	// monitor de arquivos de novas versoes de classes dinamicas
	private ILiveClassFileMonitor fileMonitor;
	
	// gerenciador de banco de dados
	private ILiveClassDB dbManager;
	
	// gerenciador de versoes de classes dinamicas persistidas 
	private ILiveClassDBVersionManager versionManager;
	
	// gerenciador de atualizacoes de classes dinamicas 
	private LiveClassUpdateManager updateManager;	
	
	// caminho para o arquivo de banco de dados
	private String dbFilePath;
	
	
	/**
	 * Inicializa a criacao de uma fabrica de objetos dinamicos em modo producao.
	 * 
	 * @throws Exception em caso de erro na criacao de algum componente interno
	 * @see org.esfinge.liveprog.LiveClassFactory#LiveClassFactory(boolean)
	 */
	public LiveClassFactoryBuilder() throws Exception
	{
		this.testMode = false;
		this.updateManager = new LiveClassUpdateManager();
	}
	
	/**
	 * Inicializa a criacao de uma fabrica de objetos dinamicos em modo de testes.
	 * 
	 * @throws Exception em caso de erro na criacao de algum componente interno
	 * @see org.esfinge.liveprog.LiveClassFactory#LiveClassFactory(boolean)
	 */
	public LiveClassFactoryBuilder inTestMode() throws Exception
	{
		this.testMode = true;
		return ( this );
	}
	
	/**
	 * Monitora o diretorio especificado em busca de arquivos de novas versoes de classes dinamicas.
	 * 
	 * @param dir o diretorio a ser monitorado na deteccao de arquivos de novas versoes de classes dinamicas
	 * @return o proximo estagio do builder para a criacao da fabrica de objetos dinamicos
	 * @throws Exception em caso de erro na criacao de algum componente interno
	 * @see org.esfinge.liveprog.monitor.FileSystemMonitor
	 */
	public LiveClassFactoryBuilderFileSystemMonitor monitoringDirectory(String dir) throws Exception
	{
		return ( new LiveClassFactoryBuilderFileSystemMonitor(dir) );
	}

	/**
	 * Busca os arquivos de novas versoes de classes dinamicas atraves do monitor especificado.
	 * 
	 * @param fileMonitor o monitor de arquivos de novas versoes de classes dinamicas
	 * @return o proximo estagio do builder para a criacao da fabrica de objetos dinamicos
	 * @throws Exception em caso de erro na criacao de algum componente interno
	 * @see org.esfinge.liveprog.monitor.ILiveClassFileMonitor
	 */
	public LiveClassFactoryBuilderDB monitoringFilesThrough(ILiveClassFileMonitor fileMonitor) throws Exception
	{
		this.fileMonitor = fileMonitor;
		return ( new LiveClassFactoryBuilderDB() );
	}
	
	
	/**
	 * Classe auxiliar para configuracao do monitor de diretorios.
	 */
	public class LiveClassFactoryBuilderFileSystemMonitor
	{
		// diretorio a ser monitorado
		private String dir;
		
		
		/**
		 * Inicializa a configuracao do monitor de diretorios.
		 *  
		 * @param dir o diretorio a ser monitorado
		 */
		public LiveClassFactoryBuilderFileSystemMonitor(String dir)
		{
			this.dir = dir;
		}
		
		/**
		 * Monitora tambem os subdiretorios do diretorio especificado.
		 * 
		 * @return o proximo estagio do builder para a criacao da fabrica de objetos dinamicos
		 * @throws Exception em caso de erro na criacao de algum componente interno
		 * @see org.esfinge.liveprog.monitor.FileSystemMonitor
		 */
		public LiveClassFactoryBuilderDB includingSubdirs() throws Exception
		{
			fileMonitor = new FileSystemMonitor(this.dir, true);
			return ( new LiveClassFactoryBuilderDB() );
		}

		/**
		 * Nao monitora os subdiretorios do diretorio especificado.
		 * 
		 * @return o proximo estagio do builder para a criacao da fabrica de objetos dinamicos
		 * @throws Exception em caso de erro na criacao de algum componente interno
		 * @see org.esfinge.liveprog.monitor.FileSystemMonitor
		 */
		public LiveClassFactoryBuilderDB excludingSubdirs() throws Exception
		{
			fileMonitor = new FileSystemMonitor(this.dir, false);
			return ( new LiveClassFactoryBuilderDB() );
		}
	}
	
	/**
	 * Classe auxiliar para configuracao do gerenciador de persistencia das classes dinamicas.
	 */
	public class LiveClassFactoryBuilderDB
	{
		/**
		 * Especifica o caminho para o arquivo do banco de dados, 
		 * utilizando o gerenciador de persistencia padrao (SQLite).
		 * 
		 * @param dbFilePath caminho do arquivo do banco de dados
		 * @return o proximo estagio do builder para a criacao da fabrica de objetos dinamicos
		 */
		public LiveClassFactoryBuilderCreate usingDatabaseFile(String dbFilePath)
		{
			LiveClassFactoryBuilder.this.dbFilePath = dbFilePath;
			return ( new LiveClassFactoryBuilderCreate() );
		}

		/**
		 * Especifica o gerenciador de persistencia para as classes dinamicas.
		 * 
		 * @param dbManager o gerenciador de persistencia a ser utilizado
		 * @return o proximo estagio do builder para a criacao da fabrica de objetos dinamicos
		 * @see org.esfinge.liveprog.db.ILiveClassDB
		 */
		public LiveClassFactoryBuilderCreate usingDatabaseManager(ILiveClassDB dbManager)
		{
			LiveClassFactoryBuilder.this.dbManager = dbManager;
			return ( new LiveClassFactoryBuilderCreate() );
		}
	}
	
	/**
	 * Classe auxiliar para finalizar a construcao da fabrica de objetos dinamicos.
	 */
	public class LiveClassFactoryBuilderCreate
	{
		/**
		 * Especifica o gerenciador de versoes de classes dinamicas persistidas.  
		 * 
		 * @param versionManager o gerenciador de versoes de classes dinamicas
		 * @return o proximo estagio do builder para a criacao da fabrica de objetos dinamicos
		 */
		public LiveClassFactoryBuilderCreate usingVersionManager(ILiveClassDBVersionManager versionManager)
		{
			LiveClassFactoryBuilder.this.versionManager = versionManager;
			return ( this );
		}
		
		/**
		 * Cria e configura a fabrica de criacao de objetos de classes dinamicas com
		 * os parametros escolhidos no builder.
		 *  
		 * @return a fabrica de objetos dinamicos 
		 * @throws Exception em caso de erro na criacao de algum componente interno
		 * @see org.esfinge.liveprog.LiveClassFactory
		 */
		public LiveClassFactory build() throws Exception
		{
			// verifica se esta utilizando o gerenciador de banco de dados padrao
			if ( dbFilePath != null )
			{
				DefaultLiveClassDB.setDatabaseFilePath(dbFilePath);
				dbManager = DefaultLiveClassDB.getInstance(); 
			}

			// cria a fabrica
			LiveClassFactory factory = new LiveClassFactory(dbManager, testMode);

			// configura os observadores
			if ( versionManager != null )
				versionManager.addObserver(factory);
			
			fileMonitor.addObserver(updateManager);
			updateManager.addObserver(factory);
			fileMonitor.start();
			
			return ( factory );
		}
	}
}
