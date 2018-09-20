package org.esfinge.liveprog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.esfinge.liveprog.annotation.IgnoreOnReload;
import org.esfinge.liveprog.annotation.InvokeOnReload;
import org.esfinge.liveprog.annotation.InvokeOnRollback;
import org.esfinge.liveprog.exception.LiveClassProxyException;
import org.esfinge.liveprog.util.Utils;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * <p>
 * Proxy para objetos de classes din�micas.
 * <p><i>
 * Proxy for objects of LiveClasses.
 * </i>
 * 
 * @see org.esfinge.liveprog.annotation.LiveClass 
 */
class LiveClassProxy implements MethodInterceptor
{
	// objeto da versao atual da classe dinamica
	private Object liveObj;
	
	// mecanismo de lock
	private Object lock;
	
	
	/**
	 * <p>
	 * Constr�i um novo proxy para o objeto de uma classe din�mica.
	 * <p><i>
	 * Constructs a new proxy for an object of a LiveClass.
	 * </i>
	 * 
	 * @param liveObj o objeto de uma classe din�mica
	 * <br><i>an object from a LiveClass</i>
	 */
	LiveClassProxy(Object liveObj)
	{
		this.liveObj = liveObj;
		this.lock = new Object();
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxyMethod) throws Throwable
	{
		// lock para invocar o metodo no objeto da classe dinamica
		synchronized( this.lock )
		{
			return ( this.invokeRealMethod(method, args) );
		}
	}
	
	/**
	 * <p>
	 * Recebe a notifica��o de que a classe din�mica foi recarregada em uma nova vers�o.
	 * <p><i>
	 * Gets notified that the LiveClass was reloaded on a new version.
	 * </i>
	 * 
	 * @param newLiveClass nova vers�o da classe din�mica
	 * <br><i>updated version of the LiveClass</i>
	 */
	void classReloaded(Class<?> newLiveClass)
	{
		// salva o objeto atual para o caso de erro ao carregar a nova versao
		Object oldObj = this.liveObj;

		try
		{
			// lock para atualizar para a nova versao da classe dinamica 
			synchronized( this.lock )
			{
				// cria o objeto da nova versao
				this.liveObj = newLiveClass.newInstance();
				
				// salva o estado do objeto antigo no objeto da nova versao
				this.copyProperties(oldObj, liveObj, false);
			}
			
			// log: objeto dinamico atualizado
			Utils.logDebug("Objeto dinamico atualizado: " + this.liveObj);
			
			// verifica se a classe possui metodo marcado com @InvokeOnReload
				for ( Method m : Utils.getAnnotadedMethods(newLiveClass, InvokeOnReload.class) )
					this.invokeRealMethod(m);
		}
		catch ( Throwable e )
		{
			// log: erro
			Utils.logError("Erro ao atualizar objeto dinamico!");
			
			// retorna para a versao antiga do objeto
			this.liveObj = oldObj;
			
			throw new LiveClassProxyException("Unable to update live object!", e);
		}
	}
	
	/**
	 * <p>
	 * Recebe a notifica��o de que a classe din�mica foi recarregada para uma vers�o anterior.
	 * <p><i>
	 * Gets notified that the LiveClass was rolled back to a previous version.
	 * </i>
	 * 
	 * @param newLiveClass nova vers�o da classe din�mica
	 * <br><i>updated version of the LiveClass</i>
	 */
	void classRolledBack(Class<?> newLiveClass)
	{
		// salva o objeto atual para o caso de erro ao carregar a nova versao
		Object oldObj = this.liveObj;

		try
		{
			// lock para atualizar para a nova versao da classe dinamica 
			synchronized( this.lock )
			{
				// cria o objeto da nova versao
				this.liveObj = newLiveClass.newInstance();
				
				// salva o estado do objeto antigo no objeto da nova versao
				this.copyProperties(oldObj, liveObj, true);
			}
			
			// log: objeto dinamico atualizado
			Utils.logDebug("Objeto dinamico revertido: " + this.liveObj);
			
			// verifica se a classe possui metodo marcado com @InvokeOnRollback
			for ( Method m : Utils.getAnnotadedMethods(newLiveClass, InvokeOnRollback.class) )
				this.invokeRealMethod(m);

			// verifica se a classe possui metodo marcado com @InvokeOnReload
				for ( Method m : Utils.getAnnotadedMethods(newLiveClass, InvokeOnReload.class) )
					this.invokeRealMethod(m);
		}
		catch ( Throwable e )
		{
			// log: erro
			Utils.logError("Erro ao reverter versao do objeto dinamico!");
			
			// retorna para a versao antiga do objeto
			this.liveObj = oldObj;
			
			throw new LiveClassProxyException("Unable to rollback live object!", e);
		}
	}
	
	/**
	 * <p>
	 * Copia as propriedades do antigo objeto para o objeto da nova vers�o da classe din�mica.
	 * <p><i>
	 * Copies the properties of the old objet to the object of the new version of the LiveClass.
	 * </i>
	 * 
	 * @param oldObj objeto da vers�o sendo substitu�da
	 * <br><i>object being replaced</i>
	 * @param newObj objeto da nova vers�o da classe din�mica
	 * <br><i>object of the new version of the LiveClass</i>
	 * @param rollback <i>true</i> caso a vers�o atual esteja sendo revertida para uma vers�o anterior, <i>false</i> caso contr�rio
	 * <br><i>true if the current version is being rolled back to a previous version, false otherwise</i>
	 * @throws Exception caso ocorra algum erro ao copiar as propriedades entre os objetos
	 * <br><i>if an error occurs when copying the properties between objects</i>
	 */
	private void copyProperties(Object oldObj, Object newObj, boolean rollback) throws Exception
	{
		// log: debug
		Utils.logDebug("Rollback: " + rollback);

		if ( rollback )
		{
			// verifica se os objetos implementam a a interface ILiveClassStateLoader
			if ( (oldObj instanceof ILiveClassState) && (newObj instanceof ILiveClassState) )
			{
				// log: debug
				Utils.logDebug("As duas versoes implementam a interface ILiveClassState");

				// prepara o mapa de estado do objeto antigo
				Map<String,Object> mapState = ((ILiveClassState) oldObj).prepareToRollback();
				
				// carrega o mapa de estado no novo objeto
				((ILiveClassState) newObj).load(mapState);
				
				return;				
			}
		}
		
		// verifica se a nova classe implementa a interface IStateLoader
		if ( newObj instanceof ILiveClassState )
		{
			// log: debug
			Utils.logDebug("Nova versao implementa interface ILiveClassState");

			// cria o mapa de propriedades do objeto antigo
			Map<String,Object> mapState = new HashMap<String,Object>();
			
			for ( Field oldObjField : Utils.getFields(oldObj.getClass()) )
			{
				// armazena a propriedade no objeto antigo
				oldObjField.setAccessible(true);
				mapState.put(oldObjField.getName(), oldObjField.get(oldObj));
			}
			
			// carrega o mapa de estado no novo objeto
			((ILiveClassState) newObj).load(mapState);
		}
		else
		{
			// log: debug
			Utils.logDebug("Nova versao NAO implementa interface ILiveClassState");
			
			// obtem as propriedades do novo objeto
			for ( Field newObjField : Utils.getFields(newObj.getClass()) )
			{
				// verifica se o campo esta marcado com @IgnoreOnReload
				if ( newObjField.isAnnotationPresent(IgnoreOnReload.class) )
				{
					// log: debug
					Utils.logDebug("Propriedade ignorada: '" + newObjField.getName() + "'");
					
					continue;
				}
				
				// obtem a propriedade no objeto antigo
				Field oldObjField =Utils.getField(oldObj.getClass(), newObjField.getName());						
			
				if ( oldObjField != null )
				{
					//
					newObjField.setAccessible(true);
					oldObjField.setAccessible(true);
					
					// copia o valor antigo para o novo objeto
					newObjField.set(newObj, oldObjField.get(oldObj));

					// log: debug
					Utils.logDebug(String.format("Propriedade copiada: [%s , %s]", newObjField.getName(), newObjField.get(newObj)));
				}
			}
		}
	}
	
	/**
	 * <p>
	 * Invoca o m�todo chamado no proxy no objeto real.
	 * <p><i>
	 * Invokes the method called in the proxy on the real object.
	 * </i>
	 *  
	 * @param proxyMethod o m�todo chamado no proxy
	 * <br><i>method called in the proxy object</i>
	 * @param args os argumentos do m�todo chamado no proxy
	 * <br><i>arguments passed to the proxy's method</i>
	 * @return o valor retornado do m�todo invocado no objeto real
	 * <br><i>the value returned by the method invoked the real object</i>
	 * @throws Throwable caso ocorra algum erro ao invocar o m�todo no objeto real
	 * <br><i>if an error occurs when invoking the method on the real object</i>
	 */
	private Object invokeRealMethod(Method proxyMethod, Object... args) throws Throwable
	{
		return ( this.liveObj.getClass().getMethod(proxyMethod.getName(), proxyMethod.getParameterTypes()).invoke(this.liveObj, args) );
	}
}
