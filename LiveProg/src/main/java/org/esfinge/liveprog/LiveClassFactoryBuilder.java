package org.esfinge.liveprog;

import org.esfinge.liveprog.db.LiveClassDB;
import org.esfinge.liveprog.monitor.FileSystemMonitor;
import org.esfinge.liveprog.monitor.IMonitor;

/**
 * Builder para a criacao da fabrica de criacao de objetos de classes dinamicas.
 */
public class LiveClassFactoryBuilder
{
	// modo que a fabrica sera criada
	private boolean testMode;
	
	// monitor de arquivos de novas versoes de classes dinamicas
	private IMonitor monitor;
	
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
	 * @return o proximo estagio do builder para a criacao da fabrica de objetos dinamicosmoni
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
	 * @param monitor o monitor de arquivos de novas versoes de classes dinamicas
	 * @return o proximo estagio do builder para a criacao da fabrica de objetos dinamicos
	 * @throws Exception em caso de erro na criacao de algum componente interno
	 * @see org.esfinge.liveprog.monitor.IMonitor
	 */
	public LiveClassFactoryBuilderDB monitoringThrough(IMonitor monitor) throws Exception
	{
		this.monitor = monitor;
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
			monitor = new FileSystemMonitor(this.dir, true);
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
			monitor = new FileSystemMonitor(this.dir, false);
			return ( new LiveClassFactoryBuilderDB() );
		}
	}
	
	/**
	 * Classe auxiliar para configuracao do Banco de Dados.
	 */
	public class LiveClassFactoryBuilderDB
	{
		/**
		 * Especifica o caminho para o arquivo do banco de dados.
		 * 
		 * @param dbFilePath caminho do arquivo do banco de dados
		 * @return o proximo estagio do builder para a criacao da fabrica de objetos dinamicos
		 */
		public LiveClassFactoryBuilderCreate usingDatabaseFile(String dbFilePath)
		{
			LiveClassFactoryBuilder.this.dbFilePath = dbFilePath;
			return ( new LiveClassFactoryBuilderCreate() );
		}
	}
	
	/**
	 * Classe auxiliar para finalizar a construcao da fabrica de objetos dinamicos.
	 */
	public class LiveClassFactoryBuilderCreate
	{
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
			LiveClassFactory factory = new LiveClassFactory(testMode);

			LiveClassDB.setDatabaseFilePath(dbFilePath);
			monitor.addObserver(updateManager);
			updateManager.addObserver(factory);
			monitor.start();
			
			return ( factory );
		}
	}
}
