package org.esfinge.liveprog.instrumentation.transformer;

import org.esfinge.liveprog.instrumentation.ClassInfo;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * Classe utilitaria para instrumentacao de classes.
 */
public class TransformerHelper
{
	/**
	 * Instrumenta a classe para substitui-la pela nova versao.
	 *  
	 * @param classInfo as informacoes da classe, carregadas do arquivo da classe compilada (.class)
	 * @param className o nome original da classe dinamica
	 * @param newClassName o nome da nova versao da classe 
	 * @return os bytecodes da classe instrumentado para a nova versao
	 * @see org.esfinge.liveprog.instrumentation.ClassInfo
	 */
	public byte[] transform(ClassInfo classInfo, String className, String newClassName)
	{
		return ( transform(classInfo.getBytecode(), className, newClassName) );
	}
	
	/**
	 * Instrumenta a classe para substitui-la pela nova versao.
	 *  
	 * @param classBytecode os bytecodes da classe
	 * @param className o nome original da classe dinamica
	 * @param newClassName o nome da nova versao da classe 
	 * @return os bytecodes da classe instrumentado para a nova versao
	 * @see org.esfinge.liveprog.instrumentation.ClassInfo
	 */
	public byte[] transform(byte[] classBytecode, String className, String newClassName)
	{
		ClassWriter classWriter = new ClassWriter(0);
		ClassTransformer transformer = new ClassTransformer(className, newClassName, classWriter);
		
		ClassReader reader = new ClassReader(classBytecode);
		reader.accept(transformer, 0);
		
		return ( classWriter.toByteArray() );
	}	
}
