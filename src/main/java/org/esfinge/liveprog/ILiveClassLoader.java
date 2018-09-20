package org.esfinge.liveprog;

import org.esfinge.liveprog.exception.LiveClassLoaderException;
import org.esfinge.liveprog.reflect.ClassInfo;

/**
 * <p>
 * Interface para customizar o carregamento de classes din�micas.
 * </p>
 * <p><i>
 * Interface to be implemented by classes to customize the loading process of LiveClass' classes.
 * </i></p>
 */
public interface ILiveClassLoader
{
	/**
	 * <p>
	 * Carrega a classe din�mica.
	 * </p>
	 * <p><i>
	 * Loads the LiveClass.
	 * </i></p>
	 * 
	 * @param liveClassInfo - informa��es da classe din�mica a ser carregada
	 * <br><i>information of the LiveClass to be loaded</i>
	 * @return a classe da classe din�mica carregada
	 * <br><i>the loaded LiveClass class</i>
	 * @throws LiveClassLoaderException em caso de erros no carregamento da classe
	 * <br><i>if an error occurs when loading the LiveClass</i>
	 */
	public Class<?> loadLiveClass(ClassInfo liveClassInfo) throws LiveClassLoaderException;
}
