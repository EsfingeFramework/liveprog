package org.esfinge.liveprog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.esfinge.liveprog.exception.LiveClassLoadException;
import org.esfinge.liveprog.instrumentation.ClassInfo;

/**
 * Carregador de classes dinamicas.
 * 
 * @see org.esfinge.LiveClass
 */
class LiveClassLoader
{
	// metodo para carregamento de classes do ClassLoader do sistema
	private Method defineClassMethod;
	
	
	/**
	 * Cria um novo carregador de classes dinamicas.
	 */
	LiveClassLoader()
	{
		try
		{
			this.defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
			this.defineClassMethod.setAccessible(true);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			// TODO: debug..
			System.out.println("CLASS LOADER >> Erro ao recuperar o metodo 'ClassLoader.defineClass()'!");
			
			e.printStackTrace();
		}
	}
	
	/**
	 * Carrega a classe dinamica.
	 * 
	 * @param liveClassInfo informacoes da classe dinamica a ser carregada
	 * @return a classe da classe dinamica carregada
	 * @throws LiveClassLoadException em caso de erros no carregamento da classe
	 */
	public Class<?> loadLiveClass(ClassInfo liveClassInfo) throws LiveClassLoadException
	{
		try
		{
			// TODO: debug..
			System.out.println("CLASSLOADER >> Classe dinamica a ser carregada: " + liveClassInfo.getName());

			// verifica se a classe ja esta carregada
			Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(liveClassInfo.getName());
			
			// TODO: debug..
			System.out.println("CLASSLOADER >> Classe dinamica ja estava carregada: " + liveClassInfo.getName());
			
			// a classe ja esta carregada, retorna
			return ( clazz );
		}
		catch ( ClassNotFoundException cnfe )
		{
			// TODO: debug..
			System.out.println("CLASSLOADER >> Classe dinamica NAO estava carregada: " + liveClassInfo.getName());
			
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
				
				// TODO: debug..
				System.out.println("CLASSLOADER >> Classe dinamica carregada: " + liveClassInfo.getName());
				
				//
				return ( clazz );
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				throw new LiveClassLoadException("Error loading live class!", e);
			}
		}
	}
}
