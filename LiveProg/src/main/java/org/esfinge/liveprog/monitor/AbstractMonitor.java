package org.esfinge.liveprog.monitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Estrutura base para monitores de arquivos de classes dinamicas.
 */
public abstract class AbstractMonitor implements IMonitor
{
	// lista de observadores a serem notificados dos arquivos 
	// de novas versoes de classes dinamicas encontrados
	protected List<IMonitorObserver> observers;
	
	// filtro dos tipos de arquivos a serem monitorados
	protected IMonitorFileFilter fileFilter;
	
	// validador dos arquivos monitorados
	protected IMonitorFileValidator fileValidator;
	
	
	/**
	 * Construtor padrao.
	 */
	public AbstractMonitor()
	{
		this.observers = new ArrayList<IMonitorObserver>();
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
	public void addObserver(IMonitorObserver observer)
	{
		this.observers.add(observer);
	}

	@Override
	public void removeObserver(IMonitorObserver observer)
	{
		this.observers.remove(observer);
	}
	
	/**
	 * Notifica os observadores que um arquivo de uma nova versao de classe dinamica foi encontrado.
	 * 
	 * @param classFile o arquivo da nova versao da classe dinamica
	 */
	protected void notifyObservers(File classFile)
	{
		this.observers.forEach(obs -> obs.classFileUpdated(classFile));
	}
}
