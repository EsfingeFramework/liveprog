package org.esfinge.liveprog.instrumentation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.esfinge.liveprog.annotation.LiveClass;
import org.esfinge.liveprog.exception.IncompatibleLiveClassException;
import org.esfinge.liveprog.instrumentation.inspector.InspectorHelper;
import org.esfinge.liveprog.instrumentation.transformer.TransformerHelper;
import org.esfinge.liveprog.util.Utils;
import org.objectweb.asm.Type;

/**
 * Classe utilitaria para servicos relacionados a instrumentacao.
 */
public abstract class InstrumentationService
{
	// responsavel pelos metodos de inspecao
	private static InspectorHelper _inspectorHelper = new InspectorHelper();
	
	// responsavel pelos metodos de transformacao
	private static TransformerHelper _transformerHelper = new TransformerHelper();
	
	
	/**
	 * Verifica se a classe eh compativel para ser uma classe dinamica.
	 * 
	 * Regras:
	 * 
	 * 1- a classe tem que ser marcada com a anotacao LiveClass
	 * 2- a classe deve possuir um construtor publico vazio
	 * 3- a classe nao pode ser utilizada como parametro em nenhum de seus metodos publicos
	 * 4- a classe nao pode ser utilizada como retorno em nenhum de seus metodos publicos 
	 * 5- classes internas nao podem ser utilizadas como parametro em nenhum de seus metodos publicos
	 * 6- classes internas nao podem ser utilizadas como parametro em nenhum de seus metodos publicos    
	 *   
	 * @param liveClass a classe a ser verificada como uma classe dinamica valida 
	 * @throws IncompatibleLiveClassException caso a classe nao respeite alguma das regras estabelecidas
	 */
	public static void checkValidLiveClass(Class<?> liveClass) throws IncompatibleLiveClassException
	{
		// verifica se nao eh null
		if ( liveClass == null )
			throw new IncompatibleLiveClassException("Class is null!");
		
		// verifica se tem a anotacao LiveClass
		if (! liveClass.isAnnotationPresent(LiveClass.class) )
			throw new IncompatibleLiveClassException(liveClass + " not marked with @LiveClass annotation!");
		
		// verifica se tem um construtor publico vazio
		Constructor<?> defaultConst = Utils.getFromCollection(Arrays.asList(liveClass.getConstructors()), c -> c.getParameterCount() == 0 );
		if ( defaultConst == null )
			throw new IncompatibleLiveClassException("Live classes must have a default public constructor!");
		
		
		// classes internas da classe
		List<Class<?>> innerClassesList = Arrays.asList(liveClass.getDeclaredClasses());

		
		// verifica os metodos publicos da classe
		for ( Method method : liveClass.getDeclaredMethods() )
		{
			// ignora os metodos nao publicos
			if (! Modifier.isPublic(method.getModifiers()) )
				continue;
			
			// verifica se usa a propria classe como retorno do metodo
			if ( method.getReturnType().equals(liveClass) )
				throw new IncompatibleLiveClassException("Live classes cannot be used as return type on their public methods!");
			
			// verifica se usa alguma classe interna como retorno do metodo
			if ( innerClassesList.contains(method.getReturnType()) )
				throw new IncompatibleLiveClassException("Inner classes cannot be used as return type on LiveClass's public methods!");

			// parametros do metodo
			List<Class<?>> paramList = Arrays.asList(method.getParameterTypes());

			// verifica se usa a propria classe como parametro do metodo
			if ( paramList.contains(liveClass) )
				throw new IncompatibleLiveClassException("Live classes cannot be used as parameter type on their public methods!");
			
			// verifica se usa alguma classe interna como parametro do metodo
			if (! Collections.disjoint(paramList, innerClassesList) )
				throw new IncompatibleLiveClassException("Inner classes cannot be used as parameter type on LiveClass's public methods!");
		}
		
		// TODO: verificar os campos publicos???
	}
	
	/**
	 * @see org.esfinge.liveprog.instrumentation.inspector.InspectorHelper#inspect(File)
	 */
	public static ClassInfo inspect(File classFile) throws IOException
	{
		return ( _inspectorHelper.inspect(classFile) );
	}
	
	/**
	 * @see org.esfinge.liveprog.instrumentation.inspector.InspectorHelper#inspect(byte[])
	 */
	public static ClassInfo inspect(byte[] classBytecode)
	{
		return ( _inspectorHelper.inspect(classBytecode) );
	}
	
	/**
	 * @see org.esfinge.liveprog.instrumentation.inspector.InspectorHelper#inspectToString(File)
	 */
	public static String inspectToString(File classFile) throws IOException
	{
		return ( _inspectorHelper.inspectToString(classFile) );
	}

	/**
	 * @see org.esfinge.liveprog.instrumentation.inspector.InspectorHelper#inspectToString(byte[])
	 */
	public static String inspectToString(byte[] classBytecode)
	{
		return ( _inspectorHelper.inspectToString(classBytecode) );
	}
	
	/**
	 * Retorna informacoes sobre uma classe.
	 * 
	 * @param clazz a classe a ser inspecionada 
	 * @return as informacoes sobre a classe informada
	 * @see org.esfinge.liveprog.instrumentation.ClassInfo
	 */
	public static ClassInfo inspect(Class<?> clazz)
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
	public static MethodInfo inspect(Method method)
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
	public static FieldInfo inspect(Field field)
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
	
	/**
	 * Instrumenta a classe para transforma-la na nova versao.
	 *  
	 * @param classInfo as informacoes da classe, carregadas do arquivo .class da nova versao da classe
	 * @param className o nome original da classe 
	 * @param newClassName o nome da nova versao da classe 
	 * @return as informacoes da classe instrumentada para a nova versao
	 * @see org.esfinge.liveprog.instrumentation.ClassInfo
	 * @see org.esfinge.liveprog.instrumentation.transformer.TransformerHelper#transform(ClassInfo, String, String)
	 */
	public static ClassInfo transform(ClassInfo classInfo, String className, String newClassName)
	{
		// transforma na nova versao da classe
		ClassInfo newClassInfo = inspect(_transformerHelper.transform(classInfo, className, newClassName));
		
		// transforma as classes internas
		for (ClassInfo innerClassInfo : classInfo.getInnerClassesInfo() )
			newClassInfo.addInnerClassInfo(transform(innerClassInfo, className, newClassName));
		
		//
		return ( newClassInfo );
	}
	
	/**
	 * Retorna o arquivo compilado (.class) da classe informada.
	 * 
	 * @param clazz a classe cujo arquivo .class sera retornado
	 * @return o arquivo .class da classe, ou <b>null</b>  caso o arquivo 
	 * nao puder ser encontrado no classpath da aplicacao 
	 */
	public static File getClassFile(Class<?> clazz)
	{
		try		
		{
			// tenta recuperar o arquivo no sistema de arquivos
			URL resourceUrl = clazz.getResource("/" + clazz.getCanonicalName().replace(".", "/") + ".class");
			
			return ( Paths.get(resourceUrl.toURI()).toFile() );
		}
		catch ( Exception exc )
		{
			// provavelmente o arquivo esta dentro de um jar/zip, retorna nulo..
			return ( null );
		}
	}
	
	/**
	 * Retorna os bytecodes da classe informada.
	 * 
	 * @param clazz a classe cujo bytecode sera retornado
	 * @return os bytecodes da classe, ou <b>null</b>  caso o recurso 
	 * nao puder ser encontrado no classpath da aplicacao 
	 */
	public static byte[] getClassBytecode(Class<?> clazz)
	{
		try
		{
			URL resourceUrl = clazz.getResource("/" + clazz.getCanonicalName().replace(".", "/") + ".class");
			InputStream classStream = resourceUrl.openStream();
			return ( IOUtils.toByteArray(classStream) );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			
			return ( null );
		}
	}
	
	/**
	 * Traduz o caminho de um arquivo de classe para o nome completo da classe.
	 * 
	 * (Exemplo: org/esfinge/liveprog/ClasseA.class -> org.esfinge.liveprog.ClasseA)
	 * 
	 * @param classFilePath o caminho do arquivo da classe
	 * @return o nome qualificado da classe
	 */
	public static String toQualifiedName(String internalName)
	{
		if ( internalName != null )
			return ( internalName.replace('/', '.') );
		
		return ( null );
	}
	
	/**
	 * Traduz o nome completo da classe para o caminho do arquivo de classe.
	 * 
	 * (Exemplo: org.esfinge.liveprog.ClasseA -> org/esfinge/liveprog/ClasseA.class)
	 * 
	 * @param fullQualifiedClassName o nome qualificado da classe
	 * @return o caminho do arquivo da classe
	 */
	public static String toInternalName(String qualifiedName)
	{
		if ( qualifiedName != null )
			return ( qualifiedName.replace('.', '/') );
		
		return ( null );
	}
}
