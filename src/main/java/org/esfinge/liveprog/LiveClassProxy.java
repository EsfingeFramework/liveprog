package org.esfinge.liveprog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.esfinge.liveprog.annotation.IgnoreOnReload;
import org.esfinge.liveprog.annotation.InvokeOnReload;
import org.esfinge.liveprog.annotation.InvokeOnRollback;
import org.esfinge.liveprog.exception.LiveClassProxyException;
import org.esfinge.liveprog.util.LiveClassUtils;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * <p>
 * Proxy para objetos de classes dinâmicas.
 * </p>
 * <p><i>
 * Proxy for objects of LiveClasses.
 * </i></p>
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
	 * Constrói um novo proxy para o objeto de uma classe dinâmica.
	 * </p>
	 * <p><i>
	 * Constructs a new proxy for an object of a LiveClass.
	 * </i></p>
	 * 
	 * @param liveObj o objeto de uma classe dinâmica
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
			return ( this.invokeOnLiveObject(method, args) );
		}
	}
	
	/**
	 * <p>
	 * Recebe a notificação de que a classe dinâmica foi recarregada em uma nova versão.
	 * </p>
	 * <p><i>
	 * Gets notified that the LiveClass was reloaded on a new version.
	 * </i></p>
	 * 
	 * @param newLiveClass nova versão da classe dinâmica
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
			LiveClassUtils.logDebug("Objeto dinamico atualizado: " + this.liveObj);
			
			// verifica se a classe possui metodo marcado com @InvokeOnReload
				for ( Method m : LiveClassUtils.getAnnotatedMethods(newLiveClass, InvokeOnReload.class) )
					this.invokeOnLiveObject(m);
		}
		catch ( Throwable e )
		{
			// log: erro
			LiveClassUtils.logError("Erro ao atualizar objeto dinamico!");
			
			// retorna para a versao antiga do objeto
			this.liveObj = oldObj;
			
			throw new LiveClassProxyException("Unable to update live object!", e);
		}
	}
	
	/**
	 * <p>
	 * Recebe a notificação de que a classe dinâmica foi recarregada para uma versão anterior.
	 * </p>
	 * <p><i>
	 * Gets notified that the LiveClass was rolled back to a previous version.
	 * </i></p>
	 * 
	 * @param newLiveClass nova versão da classe dinâmica
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
			LiveClassUtils.logDebug("Objeto dinamico revertido: " + this.liveObj);
			
			// verifica se a classe possui metodo marcado com @InvokeOnRollback
			for ( Method m : LiveClassUtils.getAnnotatedMethods(newLiveClass, InvokeOnRollback.class) )
				this.invokeOnLiveObject(m);

			// verifica se a classe possui metodo marcado com @InvokeOnReload
				for ( Method m : LiveClassUtils.getAnnotatedMethods(newLiveClass, InvokeOnReload.class) )
					this.invokeOnLiveObject(m);
		}
		catch ( Throwable e )
		{
			// log: erro
			LiveClassUtils.logError("Erro ao reverter versao do objeto dinamico!");
			
			// retorna para a versao antiga do objeto
			this.liveObj = oldObj;
			
			throw new LiveClassProxyException("Unable to rollback live object!", e);
		}
	}
	
	/**
	 * <p>
	 * Copia as propriedades do antigo objeto para o objeto da nova versão da classe dinâmica.
	 * </p>
	 * <p><i>
	 * Copies the properties of the old objet to the object of the new version of the LiveClass.
	 * </i></p>
	 * 
	 * @param oldObj objeto da versão sendo substituída
	 * <br><i>object being replaced</i>
	 * @param newObj objeto da nova versão da classe dinâmica
	 * <br><i>object of the new version of the LiveClass</i>
	 * @param rollback <i>true</i> caso a versão atual esteja sendo revertida para uma versão anterior, <i>false</i> caso contrário
	 * <br><i>true if the current version is being rolled back to a previous version, false otherwise</i>
	 * @throws Exception caso ocorra algum erro ao copiar as propriedades entre os objetos
	 * <br><i>if an error occurs when copying the properties between objects</i>
	 */
	private void copyProperties(Object oldObj, Object newObj, boolean rollback) throws Exception
	{
		// log: debug
		LiveClassUtils.logDebug("Rollback: " + rollback);

		if ( rollback )
		{
			// verifica se os objetos implementam a a interface ILiveClassStateLoader
			if ( (oldObj instanceof ILiveClassState) && (newObj instanceof ILiveClassState) )
			{
				// log: debug
				LiveClassUtils.logDebug("As duas versoes implementam a interface ILiveClassState");

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
			LiveClassUtils.logDebug("Nova versao implementa interface ILiveClassState");

			// cria o mapa de propriedades do objeto antigo
			Map<String,Object> mapState = new HashMap<String,Object>();
			
			for ( Field oldObjField : LiveClassUtils.getFields(oldObj.getClass()) )
			{
				// ignora campos estaticos
				if ( Modifier.isStatic(oldObjField.getModifiers()) )
				{
					// log: debug
					LiveClassUtils.logDebug("Propriedade estatica ignorada: '" + oldObjField.getName() + "'");
					
					continue;
				}
				
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
			LiveClassUtils.logDebug("Nova versao NAO implementa interface ILiveClassState");
			
			// obtem as propriedades do novo objeto
			for ( Field newObjField : LiveClassUtils.getFields(newObj.getClass()) )
			{
				// ignora campos estaticos
				if ( Modifier.isStatic(newObjField.getModifiers()) )
				{
					// log: debug
					LiveClassUtils.logDebug("Propriedade estatica ignorada: '" + newObjField.getName() + "'");
					
					continue;
				}
				
				// verifica se o campo esta marcado com @IgnoreOnReload
				if ( newObjField.isAnnotationPresent(IgnoreOnReload.class) )
				{
					// log: debug
					LiveClassUtils.logDebug("Propriedade @IgnoreOnReload ignorada: '" + newObjField.getName() + "'");
					
					continue;
				}
				
				// obtem a propriedade no objeto antigo
				Field oldObjField =LiveClassUtils.getField(oldObj.getClass(), newObjField.getName());						
			
				if ( oldObjField != null )
				{
					//
					newObjField.setAccessible(true);
					oldObjField.setAccessible(true);
					
					// copia o valor antigo para o novo objeto
					newObjField.set(newObj, oldObjField.get(oldObj));

					// log: debug
					LiveClassUtils.logDebug(String.format("Propriedade copiada: [%s , %s]", newObjField.getName(), newObjField.get(newObj)));
				}
			}
		}
	}
	
	/**
	 * <p>
	 * Invoca o método chamado no proxy no objeto real.
	 * </p>
	 * <p><i>
	 * Invokes the method called in the proxy on the real object.
	 * </i></p>
	 *  
	 * @param proxyMethod o método chamado no proxy
	 * <br><i>method called in the proxy object</i>
	 * @param args os argumentos do método chamado no proxy
	 * <br><i>arguments passed to the proxy's method</i>
	 * @return o valor retornado do método invocado no objeto real
	 * <br><i>the value returned by the method invoked the real object</i>
	 * @throws Throwable caso ocorra algum erro ao invocar o método no objeto real
	 * <br><i>if an error occurs when invoking the method on the real object</i>
	 */
	private Object invokeOnLiveObject(Method proxyMethod, Object... args) throws Throwable
	{
		// log: debug
		LiveClassUtils.logDebug("Metodo interceptado: " + proxyMethod.toString());

		// obtem o metodo no objeto dinamico
		Method method = LiveClassUtils.getMethod(this.liveObj.getClass(), proxyMethod.getName(), proxyMethod.getParameterTypes());
		
		if ( method != null )
		{
			method.setAccessible(true);
			
			return ( method.invoke(this.liveObj, args) );
		}
		else
		{
			// log: erro
			LiveClassUtils.logError("Metodo nao encontrado no objeto dinamico: " + proxyMethod.toString());
			
			throw new LiveClassProxyException("Intercepted method not found on live object: " + proxyMethod.toString());
		}
	}
}
