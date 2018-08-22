package org.esfinge.liveprog.instrumentation.inspector;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;

import org.esfinge.liveprog.instrumentation.ClassInfo;
import org.esfinge.liveprog.instrumentation.FieldInfo;
import org.esfinge.liveprog.instrumentation.InstrumentationService;
import org.esfinge.liveprog.instrumentation.MethodInfo;
import org.esfinge.liveprog.instrumentation.ParameterInfo;
import org.esfinge.liveprog.instrumentation.TypeWrapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Classe utilitaria para recuperacao de informacoes de classes.
 */
public class InspectorHelper
{	
	/**
	 * Retorna informacoes sobre uma classe.
	 *  
	 * @param classFile o arquivo da classe compilada (.class) a ser inspecionada
	 * @return as informacoes sobre a classe informada
	 * @throws IOException em caso de erro de leitura do arquivo
	 * @see org.esfinge.liveprog.instrumentation.ClassInfo
	 */
	public ClassInfo inspect(File classFile) throws IOException
	{
		return ( new ClassInspector(classFile, Files.readAllBytes(classFile.toPath())).getClassInfo() );
	}
	
	/**
	 * Retorna informacoes sobre uma classe.
	 * 
	 * @param classBytecode os bytecodes da classe a ser inspecionada
	 * @return as informacoes sobre a classe informada
	 * @see org.esfinge.liveprog.instrumentation.ClassInfo
	 */
	public ClassInfo inspect(byte[] classBytecode)
	{
		return ( new ClassInspector(null, classBytecode).getClassInfo() );
	}
	
	/**
	 * Retorna uma String com as informacoes de bytecode de uma classe.
	 * 
	 * @param classFile o arquivo da classe compilada (.class) cujo bytecode sera inspecionado 
	 * @return as informacoes de bytecode da classe informada
	 * @throws IOException em caso de erro de leitura do arquivo
	 */
	public String inspectToString(File classFile) throws IOException
	{
		return ( inspectToString(Files.readAllBytes(classFile.toPath())) );
	}

	/**
	 * Retorna uma String com as informacoes de bytecode de uma classe.
	 * 
	 * @param classBytecode os bytecodes da classe 
	 * @return as informacoes de bytecode da classe informada
	 */
	public String inspectToString(byte[] classBytecode)
	{
		ClassReader classReader = new ClassReader(classBytecode);
		StringWriter buffer = new StringWriter();
		TraceClassVisitor tracer = new TraceClassVisitor(new PrintWriter(buffer));
		classReader.accept(tracer, 0);
		
		return ( buffer.toString() );
	}
	
	/**
	 * Retorna informacoes sobre uma classe.
	 * 
	 * @param clazz a classe a ser inspecionada 
	 * @return as informacoes sobre a classe informada
	 * @see org.esfinge.liveprog.instrumentation.ClassInfo
	 */
	public ClassInfo inspect(Class<?> clazz)
	{
		if ( clazz == null )
			return null;
		
		ClassInfo classInfo = new ClassInfo();
		classInfo.setVersion(-1);
		classInfo.setAccessFlag(clazz.getModifiers());
		classInfo.setName(clazz.getName());
		classInfo.setBytecode(InstrumentationService.getClassBytecode(clazz));
		classInfo.setClassFile(InstrumentationService.getClassFile(clazz));
		
		// nome da classe pai
		classInfo.setSuperclassName(clazz.getSuperclass() == null ? null : clazz.getSuperclass().getName());
		
		// nomes das interfaces
		for ( Class<?> interf : clazz.getInterfaces() )
			classInfo.addInterfaceName(interf.getName());

		// se for uma classe interna, o nome da classe externa a qual pertence
		if ( clazz.getEnclosingClass() != null )
		{
			classInfo.setOuterClassName(clazz.getEnclosingClass().getName());
			
			// TODO: REMOVIDO PARA EVITAR PROBLEMAS DE REFERENCIA CIRCULAR
//			classInfo.setOuterClassInfo(inspect(clazz.getEnclosingClass()));
		}
		
		// classes internas
		for ( Class<?> innerClass : clazz.getDeclaredClasses() )
		{
			classInfo.addInnerClassName(innerClass.getName());
			classInfo.addInnerClassInfo(inspect(innerClass));
		}
		
		// metodos
		for ( Method method : clazz.getDeclaredMethods() )
			classInfo.addMethodInfo(inspect(method));
		
		// campos
		for ( Field field : clazz.getDeclaredFields() )
			classInfo.addFieldInfo(inspect(field));		
		
		// anotacoes
		// TODO: TO BE DONE..
		
		return ( classInfo );
	}
	
	/**
	 * Retorna informacoes sobre um metodo.
	 * 
	 * @param method o metodo a ser inspecionado
	 * @return as informacoes sobre o metodo informado
	 * @see org.esfinge.liveprog.instrumentation.MethodInfo
	 */
	public MethodInfo inspect(Method method)
	{
		if ( method == null )
			return ( null );
		
		MethodInfo methodInfo = new MethodInfo();
		methodInfo.setAccessFlag(method.getModifiers());
		methodInfo.setName(method.getName());
		methodInfo.setReturnType(new TypeWrapper(Type.getType(method.getReturnType())));

		// excecoes
		for ( Class<?> exception : method.getExceptionTypes() )
			methodInfo.addThrownExceptionName(exception.getName());
		
		// parametros
		Parameter[] params = method.getParameters();
		for ( int i = 0; i < params.length; i++ )
		{
			ParameterInfo paramInfo = new ParameterInfo();
			paramInfo.setName(params[i].getName()); 
			paramInfo.setIndex(i);
			paramInfo.setType(new TypeWrapper(Type.getType(params[i].getType())));
			methodInfo.addParameterInfo(paramInfo);
		}
		
		// anotacoes
		// TODO: TO BE DONE..
		
		return ( methodInfo );
	}
	
	/**
	 * Retorna informacoes sobre um campo/propriedade.
	 * 
	 * @param field o campo a ser inspecionado
	 * @return as informacoes sobre o campo informado
	 * @see org.esfinge.liveprog.instrumentation.FieldInfo
	 */
	public FieldInfo inspect(Field field)
	{
		if ( field == null )
			return ( null );
		
		FieldInfo fieldInfo = new FieldInfo();
		fieldInfo.setAccessFlag(field.getModifiers());
		fieldInfo.setName(field.getName());
		fieldInfo.setType(new TypeWrapper(Type.getType(field.getType())));
		
		// anotacoes
		// TODO: TO BE DONE..
		
		return ( fieldInfo );
	}
}
