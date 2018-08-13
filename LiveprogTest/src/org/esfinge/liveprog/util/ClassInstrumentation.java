package org.esfinge.liveprog.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.objectweb.asm.ClassReader;

/**
 * Classe utilitaria que facilita a recuperacao de informacoes
 * do arquivo de uma classe Java compilada (.class).
 */
public class ClassInstrumentation
{
	// o arquivo da classe compilada
	private File classFile;
	
	// os dados da classe (bytecode)
	private byte[] classBytecode;
	
	// classe utilitaria ASM 
	private ClassReader classReader;
	
	
	/**
	 * 
	 * @param classFile o arquivo da classe compilada (.class)
	 */
	public ClassInstrumentation(File classFile) throws IOException
	{
		this.classFile = classFile;
		this.classBytecode = Files.readAllBytes(this.classFile.toPath());
		this.classReader = new ClassReader(this.classBytecode);
	}
	
	/**
	 * Obtem o arquivo da classe compilada.
	 * 
	 * @return o arquivo da classe compilada
	 */
	public File getFile()
	{
		return ( this.classFile );
	}
	
	/**
	 * Obtem os dados da classe (bytecode).
	 * 
	 * @return os bytecodes da classe compilada
	 */
	public byte[] getBytecode()
	{
		return ( this.classBytecode );
	}
	
	/**
	 * Obtem a classe utilitaria ASM.
	 * 
	 * @return a classe utilitaria ASM
	 */
	public ClassReader getClassReader()
	{
		return ( this.classReader );
	}
	
	/**
	 * Obtem o objeto Class associado a classe.
	 * 
	 * @return o objeto Class associado a classe, 
	 * ou <b>null</b> caso a classe nao possa ser carregada 
	 */
	public Class<?> getClazz()
	{
		try
		{
			return ( Class.forName(this.getClassName()) );	
		}
		catch ( ClassNotFoundException cnfe )
		{
			return ( null );
		}
	}
	
	/**
	 * Obtem o nome qualificado da classe compilada.
	 * 
	 * @return o nome completo da classe
	 */
	public String getClassName()
	{
		return ( this.classReader.getClassName().replace("/", ".") );
	}
	
	/**
	 * Obtem o nome da classe compilada.
	 * 
	 * @return o nome da classe
	 */
	public String getSimpleClassName()
	{
		String className = this.getClassName();
		
		return ( className.substring(className.lastIndexOf('.') + 1) );
	}
	
	/**
	 * Obtem o pacote da classe compilada.
	 * 
	 * @return o pacote da classe
	 */
	public String getPackage()
	{
		String className = this.getClassName();
		return ( className.substring(0, className.lastIndexOf('.')) );
	}
}
