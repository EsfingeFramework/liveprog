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

/**
 * Monitora novas versoes de classes dinamicas no sistema de arquivos.
 * 
 * @see org.esfinge.LiveClass
 * @see ILiveClassFileMonitor
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
	 * Cria um novo monitor de arquivos de classes dinamicas.
	 * 
	 * @param dir o diretorio para monitorar por novas versoes de classes dinamicas 
	 * @param includeSubdirs monitorar tambem os subdiretorios
	 * @throws Exception caso ocorra algum erro interno de inicializacao 
	 */
	public FileSystemMonitor(String dir, boolean includeSubdirs) throws Exception
	{
		this.rootDir = Paths.get(dir);
		this.mapKeyPath = new HashMap<WatchKey,Path>();
		this.watchService = FileSystems.getDefault().newWatchService();
		this.executorService = Executors.newSingleThreadExecutor();
		this.isRunning = false;
		
		// tipo de arquivos: classes Java
		this.setFileFilter(new FileExtensionFilter("class"));
		
		// validador de classes Java
		this.setFileValidator(new JavaclassValidator());

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
	 * Registra o diretorio (e seus subdiretorios) para monitorar novas versoes de classes dinamicas.
	 * 
	 * @param dir o diretorio (e seus subdiretorios) a ser monitorado
	 * @throws IOException caso ocorra algum erro de IO 
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
		
		// mapeia a chave e o path do diretorio
		this.mapKeyPath.put(key, dir);
		
		// registra os subdiretorios
		if ( includeSubdirs )
			for ( File f : dir.toFile().listFiles() )
				this.registerDirectory(f.toPath(), true);
	}
	
	
	/**
	 * Thread responsavel por monitorar o(s) diretorio(s) de classes dinamicas.
	 */
	private class MonitorRunnable implements Runnable
	{
		public void run()
		{
			try
			{
				// TODO: debug
				// informa qual o (root) diretorio que esta sendo monitorado 
				System.out.println("MONITOR >> Monitorando diretorio [" + rootDir.toAbsolutePath() + "]");
				
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
				    	if ( fileFilter.acceptFile(arquivo) && fileValidator.isValid(arquivo) )
			    		{
				    		// TODO: debug
				    		System.out.println("MONITOR >> Novo arquivo encontrado: " + arquivo.getName() );

				    		// notifica os observadores
				    		FileSystemMonitor.this.notifyObservers(arquivo);
				    	}
				    }
				    
				    key.reset();
				}
			}
			catch (Exception e)
			{
				// TODO: debug
				e.printStackTrace();
			}
			
			finally
			{
				System.out.println("MONITOR >> Monitoramento encerrado!" );
				//
				isRunning = false;
			}
		}
	}
}
