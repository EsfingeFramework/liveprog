package org.esfinge.liveprog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.esfinge.liveprog.util.LiveClassUtils;

import net.sf.cglib.proxy.Factory;

/**
 * <p>
 * Aspecto para interceptar comportamentos não capturados pelo mecanismo padrão de proxy.
 * <p><i>
 * Aspect that intercepts behaviors not captured by the default proxy mechanism.
 * </i>
 */
public aspect LiveClassProxyAspect 
{
	/**
	 * <p>
	 * Copia os campos/propriedades que são alteradas diretamente no objeto
	 * criado pelo CGLIB para o objeto dinâmico manipulado pelo proxy.
	 * <p><i>
	 * Copies the field value changed directly in the object created by CGLIB framework
	 * to the live object manipulated by the proxy.
	 * </i>
	 * 
	 * @param enhancer objeto da classe dinâmica criado pelo CGLIB
	 * <br><i>the object created by the CGLIB framework</i>
	 * @param newValue valor do campo/propriedade sendo alterado
	 * <br><i>the new value being set</i>
	 */
	before(Factory enhancer, Object newValue) : set(* *) && target(enhancer) && args(newValue) && !within(LiveClassProxyAspect +)
	{
		// obtem o nome do campo/propriedade sendo alterado
		String fieldName = thisJoinPoint.getSignature().getName();

		// log: debug
		LiveClassUtils.logDebug(String.format("Interceptado alteracao do campo '%s = %s' no objeto '%s'", fieldName, newValue, enhancer.getClass()));

		try
		{
			// obtem o objeto da classe dinamica manipulado do proxy
			Object liveObj = this.getLiveObject(enhancer);
			
			// altera o campo/propriedade do objeto dinamico
			Field field = LiveClassUtils.getField(liveObj.getClass(), fieldName);
			field.setAccessible(true);
			field.set(liveObj, newValue);
		}
		catch ( Exception e ) 
		{
			// log: erro
			LiveClassUtils.logError(String.format("Erro ao replicar valor do campo '%s = %s' no objeto dinamico!", fieldName, newValue));
			LiveClassUtils.logException(e);
		}
	}
	
	/**
	 * <p>
	 * Intercepta métodos internos para serem invocados no objeto dinâmico manipulado pelo proxy.
	 * <p><i>
	 * Intercepts internal methods to be invoked on the live object manipulated by the proxy.
	 * </i>
	 * 
	 * @param enhancer objeto da classe dinâmica criado pelo CGLIB
	 * <br><i>the object created by the CGLIB framework</i>
	 * @return o valor de retorno do método interceptado, invocado no objeto dinâmico
	 * <br><i>the returned value of the intercepted method invoked on the live object</i>
	 */
	Object around(Factory enhancer) : target(enhancer) && execution(private * *(..)) && !(execution(* net.sf.cglib.proxy.Factory.*(..))) && !within(LiveClassProxyAspect +)
	{
		// log: debug
		LiveClassUtils.logDebug(String.format("Interceptado metodo interno nao capturado pelo proxy: %s", thisJoinPoint.getSignature()));

		try
		{
			// obtem o objeto da classe dinamica manipulado do proxy
			Object liveObj = this.getLiveObject(enhancer);
			
			// obtem o nome do metodo invocado
			String methodName = thisJoinPoint.getSignature().getName();
			Object[] args = thisJoinPoint.getArgs();
			
			// obtem o metodo
			Method method = LiveClassUtils.getMethod(liveObj.getClass(), methodName, this.getArgumentTypes(args));
			method.setAccessible(true);
			
			// invoca o metodo no objeto dinamico
			return ( method.invoke(liveObj, args) );			
		}
		catch ( Exception e )
		{
			// log: erro
			LiveClassUtils.logError(String.format("Erro ao invocar metodo interno '%s' no objeto dinamico!", thisJoinPoint.getSignature().getName()));
			LiveClassUtils.logException(e);
			
			return proceed(enhancer);	
		}
	}

	/**
	 * <p>
	 * Obtém o objeto dinâmico manipulado pelo proxy.
	 * <p><i>
	 * Gets the live object manipulated by the proxy.
	 * </i>
	 * 
	 * @param enhancer objeto da classe dinâmica criado pelo CGLIB
	 * <br><i>the object created by the CGLIB framework</i>
	 * @return o proxy vinculado ao objeto da classe dinâmica criado pelo CGLIB
	 * <br><i>the proxy object associated to the object created by CGLIB framework</i>
	 * @throws IllegalArgumentException caso ocorra algum erro ao recuperar o objeto dinâmico
	 * <br><i>if an error occurs when retrieving the live object</i>
	 * @throws IllegalAccessException caso ocorra algum erro ao recuperar o objeto dinâmico
	 * <br><i>if an error occurs when retrieving the live object</i>
	 */
	private Object getLiveObject(Factory enhancer) throws IllegalArgumentException, IllegalAccessException
	{
		// obtem o objeto proxy
		LiveClassProxy proxy = (LiveClassProxy) enhancer.getCallback(0);
	
		// obtem o objeto da classe dinamica manipulado do proxy
		Field field = LiveClassUtils.getField(LiveClassProxy.class, "liveObj");
		field.setAccessible(true);
		Object liveObj = field.get(proxy);
		
		return ( liveObj );
	}
	
	/**
	 * <p>
	 * Obtém os tipos dos argumentos informados.
	 * <p><i>
	 * Gets the type array of the specified arguments.
	 * </i>
	 * 
	 * @param args argumentos de um método
	 * <br><i>the arguments of a method</i>
	 * @return um array com os tipos dos argumentos informados
	 * <br><i>an array of the types of the specified arguments</i>
	 */
	private Class<?>[] getArgumentTypes(Object[] args)
	{
		List<Class<?>> argsList = new ArrayList<Class<?>>();
		
		for ( int i = 0; args != null && i < args.length; i++ )
			argsList.add(args[i].getClass());
		
		return ( argsList.toArray(new Class<?>[0]) );
	}	
}
