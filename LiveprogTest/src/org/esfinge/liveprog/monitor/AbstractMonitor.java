package org.esfinge.liveprog.monitor;

import java.io.File;

import org.esfinge.liveprog.util.ClassInstrumentation;

/**
 * Estrutura base para monitores de classes dinamicas.
 */
public abstract class AbstractMonitor implements IMonitor
{
	// observador a ser notificado das atualizacoes 
	// dos arquivos de classes dinamicas
	protected IMonitorObserver observer;
	
	// filtro dos tipos de arquivos a serem monitorados
	protected IMonitorFileFilter fileFilter;
	
	// validador dos arquivos monitorados
	protected IMonitorFileValidator fileValidator;
	
	
	/**
	 * Construtor padrao.
	 */
	public AbstractMonitor()
	{
		this.setObserver(null);
	}
	
	@Override
	public void setFileFilter(IMonitorFileFilter filter)
	{
		this.fileFilter = filter;
	}

	@Override
	public void setFileValidator(IMonitorFileValidator validator)
	{
		this.fileValidator = validator;
	}

	@Override
	public void setObserver(IMonitorObserver observer)
	{
		if ( observer == null )
			this.observer = new NullObserver();
		else
			this.observer = observer;
	}
	
	/**
	 * Padrao NullObject para quando nao for especificado um observador.
	 */
	private static class NullObserver implements IMonitorObserver
	{
		@Override
		public void classFileUpdated(ClassInstrumentation classInstr)
		{
			// nao faz nada..
		}
	}
}
