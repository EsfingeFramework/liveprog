package org.esfinge.liveprog;

import java.lang.reflect.Method;

import org.esfinge.liveprog.annotation.InvokeOnReload;
import org.esfinge.liveprog.util.Utils;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Proxy para objetos de classes dinamicas.
 * 
 * @see org.esfinge.LiveClass 
 */
class LiveClassProxy implements MethodInterceptor, ILiveClassObserver
{
	// objeto da versao atual da classe dinamica
	private Object liveObj;
	
	// mecanismo de lock
	private Object lock;
	
	
	/**
	 * Cria um novo proxy para objetos de classes dinamicas.
	 * 
	 * @param liveObj objeto da versao atual da classe dinamica
	 */
	LiveClassProxy(Object liveObj)
	{
		this.liveObj = liveObj;
		this.lock = new Object();
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable
	{
		// lock para invocar o metodo no objeto da classe dinamica
		synchronized( this.lock )
		{
			return ( this.invokeRealMethod(method, args) );
		}
	}
	
	@Override
	public void classReloaded(Class<?> newClass)
	{
		// 
		Object oldObj = this.liveObj;

		try
		{
			// lock para atualizar para a nova versao da classe dinamica 
			synchronized( this.lock )
			{
				// cria o objeto da nova versao
				this.liveObj = newClass.newInstance();
				
				// salva o estado do objeto antigo no objeto da nova versao
				Utils.copyProperties(oldObj, liveObj);
			}
			
			// TODO: debug
			System.out.println("PROXY >> Objeto atualizado: " + this.liveObj );
			
			// verifica se a classe possui metodo marcado com @InvokeOnReload
				for ( Method m : Utils.getAnnotadedMethods(newClass, InvokeOnReload.class) )
					this.invokeRealMethod(m);
		}
		catch ( Throwable e)
		{
			// TODO: debug
			e.printStackTrace();
			
			// retorna para a versao antiga do objeto
			this.liveObj = oldObj;
		}
	}
	
	/**
	 * Retorna o metodo real do objeto dinamico que foi invocado via proxy.
	 *  
	 * @param proxyMethod o metodo invocado via proxy
	 * @return o metodo real do objeto da classe dinamica
	 * @throws Throwable caso ocorra algum erro de reflexao para a obtencao do metodo no objeto dinamico
	 */
	private Object invokeRealMethod(Method proxyMethod, Object... args) throws Throwable
	{
		return( this.liveObj.getClass().getMethod(proxyMethod.getName(), proxyMethod.getParameterTypes()).invoke(this.liveObj, args) );
	}
}
