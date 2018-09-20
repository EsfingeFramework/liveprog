package org.esfinge.liveprog.monitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.esfinge.liveprog.util.Utils;

/**
 * <p>
 * Monitora por arquivos de classes dinâmicas atualizadas no sistema de arquivos.
 * </p>
 * <p><i>
 * Monitors for updated LiveClass files in the file system.
 * </i><p>
 * 
 * @see org.esfinge.liveprog.monitor.ILiveClassFileMonitor
 */
public class FileSystemMonitor extends AbstractLiveClassFileMonitor
{
	// diretorio a ser monitorado
	private Path rootDir;
	
	// mapa dos diretorios/subdiretorios sendo monitorados
	private Map<WatchKey,Path> mapKeyPath;
	
	// monitor de mudancas de sistemas de arquivos
	private WatchService watchService;
	
	// thread para execucao do monitoramento
	private ExecutorService executorService;
	
	// flag de status de execucao do monitor
	private boolean isRunning;
	
	
	/**
	 * <p>
	 * Constrói um novo monitor de sistema de arquivos.
	 * </p>
	 * <p><i>
	 * Constructs a new file system monitor.
	 * </i></p>
	 * 
	 * @param dir - diretório para monitorar por arquivos de classes dinâmicas atualizadas
	 * <br><i>directory for monitoring updated LiveClass files</i>
	 * @param includeSubdirs - <i>true</i> para monitorar também os subdiretórios, 
	 * <i>false</i> para monitorar somente o diretório informado
	 * <br><i>true to also monitor subdirectories, false to monitor the specified directory only</i>
	 * @throws Exception caso ocorra algum erro interno de inicialização
	 * <br><i>in case of internal error during initialization</i>
	 */
	public FileSystemMonitor(String dir, boolean includeSubdirs) throws Exception
	{
		this.rootDir = Paths.get(dir);
		this.mapKeyPath = new HashMap<WatchKey,Path>();
		this.watchService = FileSystems.getDefault().newWatchService();
		this.executorService = Executors.newSingleThreadExecutor();
		this.isRunning = false;
		
		// filtros: classes java, anotados com @LiveClass
		this.setFileFilter(new FileFilterComposite(new FileExtensionFilter("class"), new AnnotatedLiveClassFileFilter()));

		// registra o diretorio/subdiretorios a serem monitorados
		this.registerDirectory(this.rootDir, includeSubdirs);
	}
	
	@Override
	public void start()
	{
		// verifica se ja em execucao
		if (! this.isRunning )
			this.executorService.submit(new MonitorRunnable());
	}

	@Override
	public void stop()
	{
		//
		this.isRunning = false;
	}

	@Override
	public void shutdown()
	{
		//
		this.isRunning = false;
		this.executorService.shutdown();
		
		try
		{
			// fecha o watchservice
			this.watchService.close();
		}
		catch (IOException e)
		{
		}
	}

	
	/**
	 * <p>
	 * Registra o diretório a ser monitorado.
	 * </p>
	 * <p><i>
	 * Registers the directory to be monitored.
	 * </i></p>
	 * 
	 * @param dir - diretório a ser monitorado
	 * <br><i>the directory to be monitored</i>
	 * @param includeSubdirs - <i>true</i> para monitorar também os subdiretórios, 
	 * <i>false</i> para monitorar somente o diretório informado
	 * <br><i>true to also monitor subdirectories, false to monitor the specified directory only</i>
	 * @throws IOException caso ocorra algum erro ao registrar os diretórios
	 * <br><i>if an error occurs when registering the directories to be monitored</i>
	 */
	private void registerDirectory(final Path dir, boolean includeSubdirs) throws IOException
	{
		// verifica se eh um diretorio
		 if (! Files.isDirectory(dir, LinkOption.NOFOLLOW_LINKS))
			 return;
		 
		// monitora eventos do tipo MODIFICACAO
		WatchKey key = dir.register(this.watchService,
						// StandardWatchEventKinds.ENTRY_CREATE,
						// StandardWatchEventKinds.ENTRY_DELETE,
						StandardWatchEventKinds.ENTRY_MODIFY);
		
		// mapeia a chave e o caminho do diretorio
		this.mapKeyPath.put(key, dir);
		
		// registra os subdiretorios
		if ( includeSubdirs )
			for ( File f : dir.toFile().listFiles() )
				this.registerDirectory(f.toPath(), true);
	}
	
	
	/**
	 * <p>
	 * Thread responsável por monitorar os diretórios registrados.
	 * </p>
	 * <p><i>
	 * Thread that monitors the registed directories.
	 * </i></p>
	 */
	private class MonitorRunnable implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				// log: o diretorio sendo monitorado
				Utils.logInfo("Monitorando diretorio [" + rootDir.toAbsolutePath() + "]");
				
				//
				isRunning = true;
				
		    	WatchKey key;
				while ( isRunning && ((key = watchService.take()) != null) )
				{
					// Prevent receiving two separate ENTRY_MODIFY events: file modified
					// and timestamp updated. Instead, receive one ENTRY_MODIFY event
					// with two counts.
					Thread.sleep( 50 );
					
				    for (WatchEvent<?> event : key.pollEvents()) 
				    {
				    	if ( event.kind() == StandardWatchEventKinds.OVERFLOW )
				    		continue;
				    	
				    	// obtem o caminho do arquivo modificado
			    		Path parent = mapKeyPath.get(key);
			    		File arquivo = parent.resolve((Path) event.context()).toFile();
			    		
				    	// verifica se o arquivo eh uma classe Java compilada
			    		// e do tipo dinamica
				    	if ( fileFilter.acceptFile(arquivo) )
			    		{
				    		// log: arquivo aceito
				    		Utils.logInfo("Novo arquivo de classe encontrado: " + arquivo.getName() );

				    		// notifica os observadores
				    		FileSystemMonitor.this.notifyObservers(arquivo);
				    	}
				    }
				    
				    key.reset();
				}
			}
			catch (Exception e)
			{
				// log: erro
				Utils.logError("Erro durante o monitoramento!");
				Utils.logException(e);
			}
			
			finally
			{
				// log: encerrando monitoramento
				Utils.logInfo("Monitoramente encerrado!");
				
				//
				isRunning = false;
			}
		}
	}
}
