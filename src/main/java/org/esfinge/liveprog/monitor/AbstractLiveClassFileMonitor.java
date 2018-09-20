package org.esfinge.liveprog.monitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Fornece uma classe base para a implementação de monitores de arquivos de classes dinâmicas atualizadas. 
 * <p><i>
 * Provides a base class from which other {@link ILiveClassFileMonitor} classes can be derived.
 * </i>
 * 
 * @see org.esfinge.liveprog.monitor.ILiveClassFileMonitor
 */
public abstract class AbstractLiveClassFileMonitor implements ILiveClassFileMonitor
{
	// lista de observadores a serem notificados dos arquivos 
	// de novas versoes de classes dinamicas encontrados
	protected List<ILiveClassFileMonitorObserver> observers;
	
	// filtro dos tipos de arquivos a serem monitorados
	protected ILiveClassFileFilter fileFilter;
	
	
	/**
	 * <p>
	 * Construtor padrão.
	 * <p><i>
	 * Default constructor.
	 * </i>
	 */
	public AbstractLiveClassFileMonitor()
	{
		this.observers = new ArrayList<ILiveClassFileMonitorObserver>();
	}
	
	@Override
	public void setFileFilter(ILiveClassFileFilter filter)
	{
		this.fileFilter = filter;
	}

	@Override
	public void addObserver(ILiveClassFileMonitorObserver observer)
	{
		this.observers.add(observer);
	}

	@Override
	public void removeObserver(ILiveClassFileMonitorObserver observer)
	{
		this.observers.remove(observer);
	}
	
	/**
	 * <p>
	 * Notifica os observadores de que o arquivo de uma classe dinâmica atualizada foi encontrado.
	 * <p><i>
	 * Notifies the observers that a updated LiveClass file was found.
	 * </i>
	 * 
	 * @param liveClassFile arquivo da classe dinâmica atualizada
	 * <br><i>the updated LiveClass file</i>
	 */
	protected void notifyObservers(File liveClassFile)
	{
		this.observers.forEach(obs -> obs.liveClassFileUpdated(liveClassFile));
	}
}
