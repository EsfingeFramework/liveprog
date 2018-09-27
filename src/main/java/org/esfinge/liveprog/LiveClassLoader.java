package org.esfinge.liveprog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.esfinge.liveprog.exception.LiveClassLoaderException;
import org.esfinge.liveprog.reflect.ClassInfo;
import org.esfinge.liveprog.util.LiveClassUtils;

/**
 * <p>
 * Carregador de classes dinâmicas.
 * <p><i>
 * LiveClass classloader.
 * </i>
 * 
 * @see org.esfinge.liveprog.annotation.LiveClass
 */
class LiveClassLoader implements ILiveClassLoader
{
	// metodo para carregamento de classes do ClassLoader do sistema
	private Method defineClassMethod;
	
	
	/**
	 * <p>
	 * Constrói um novo carregador de classes dinâmicas.
	 * <p><i>
	 * Constructs a new LiveClass loader.
	 * </i>
	 * 
	 * @throws LiveClassLoaderException caso não consiga obter o método 'defineClass' por reflexão
	 * <br><i>if unable to retrieve the 'defineClass' method by reflection from the System Classloader</i>
	 */
	LiveClassLoader() throws LiveClassLoaderException
	{
		try
		{
			// obtem o metodo de carregamento de classes do ClassLoader do sistema
			this.defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
			this.defineClassMethod.setAccessible(true);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			// log: erro reflection
			LiveClassUtils.logError("Erro ao recuperar metodo 'ClassLoader.defineClass()' por reflexão!");
			
			throw new LiveClassLoaderException("Unable to retrive method 'defineClass' from System Classloader by reflection");
		}
	}
	
	@Override
	public Class<?> loadLiveClass(ClassInfo liveClassInfo) throws LiveClassLoaderException
	{
		try
		{
			// log:
			LiveClassUtils.logInfo("Classe dinamica a ser carregada: '" + liveClassInfo.getName() + "'");

			// verifica se a classe ja esta carregada
			Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(liveClassInfo.getName());
			
			// log:
			LiveClassUtils.logInfo("Classe dinamica ja estava carregada");
			
			// a classe ja esta carregada, retorna
			return ( clazz );
		}
		catch ( ClassNotFoundException cnfe )
		{
			// log:
			LiveClassUtils.logInfo("Classe dinamica NAO estava carregada");
			
			// bytecode da classe
			byte[] classBytecode = liveClassInfo.getBytecode();
			
			try
			{
				// tenta carregar a classe dinamica
				Class<?> clazz = (Class<?>) this.defineClassMethod.invoke(ClassLoader.getSystemClassLoader(), 
						liveClassInfo.getName(), classBytecode, 0, classBytecode.length);
				
				// carrega as classes internas
				for ( ClassInfo innerClassInfo : liveClassInfo.getInnerClassesInfo() )
					this.loadLiveClass(innerClassInfo);
				
				/*
				// verifica se o pacote da classe esta definido
				String packageName = null;
				int pos = name.lastIndexOf('.');
				if ( pos != -1 )
				{
					packageName = name.substring(0, pos);
				
					Package pkg = getPackage(packageName);
					
					if ( pkg == null )
						definePackage(packageName, null, null, null, null, null, null, null);
				}
				*/
				
				// log:
				LiveClassUtils.logInfo("Classe dinamica carregada com sucesso: '" + liveClassInfo.getName() + "'");
				
				//
				return ( clazz );
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				// log: erro
				LiveClassUtils.logError("Erro ao carregar classe dinamica: '" + liveClassInfo.getName() + "'");
				
				throw new LiveClassLoaderException("Error loading live class!", e);
			}
		}
	}
}
