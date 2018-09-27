package org.esfinge.liveprog.instrumentation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.esfinge.liveprog.annotation.LiveClass;
import org.esfinge.liveprog.exception.IncompatibleLiveClassException;
import org.esfinge.liveprog.reflect.AnnotationInfo;
import org.esfinge.liveprog.reflect.ClassInfo;
import org.esfinge.liveprog.reflect.FieldInfo;
import org.esfinge.liveprog.reflect.MethodInfo;
import org.esfinge.liveprog.reflect.ParameterInfo;
import org.esfinge.liveprog.reflect.TypeHandler;
import org.esfinge.liveprog.util.LiveClassUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * <p>
 * Classe utilitária para serviços de instrumentação.
 * </p>
 * <p><i>
 * Utilitary class for handling instrumentation.
 * </i></p>
 */
public abstract class InstrumentationHelper
{
	/**
	 * <p>
	 * Verifica se a classe informada é compatível com as regras para ser considerada uma classe dinâmica válida:
	 * <br>- a classe tem que possuir a anotação {@link LiveClass}
	 * <br>- a classe deve possuir um construtor público vazio
	 * <br>- a classe não pode ser utilizada como parâmetro em nenhum de seus métodos públicos
	 * <br>- a classe não pode ser utilizada como retorno em nenhum de seus métodos públicos
	 * <br>- suas classes internas não podem ser utilizadas como parâmetro em nenhum de seus métodos públicos
	 * <br>- suas classes internas não podem ser utilizadas como retorno em nenhum de seus métodos públicos
	 * <p><i>
	 * Checks if the specified class complies with the LiveClass's requirements:
	 * <br>- the class must be annotated with {@link LiveClass}
	 * <br>- the class must define a public default constructor
	 * <br>- the class cannot be used as a parameter type in none of its public methods
	 * <br>- the class cannot be used as a return type in none of its public methods
	 * <br>- its inner classes cannot be used as a parameter type in none of its public methods
	 * <br>- its inner classes cannot be used as a return type in none of its public methods
	 * </i>
	 *   
	 * @param liveClass classe candidata a ser validada como uma classe dinâmica válida 
	 * <br><i>the candidate class to be validated as a valid LiveClass</i>
	 * @throws IncompatibleLiveClassException caso a classe não cumpra alguma das regras estabelecidas
	 * <br><i>if the candidate class does not comply with the specified rules</i>
	 */
	public static void checkValidLiveClass(Class<?> liveClass) throws IncompatibleLiveClassException
	{
		// verifica se nao eh null
		if ( liveClass == null )
			throw new IncompatibleLiveClassException("Class is null!");
		
		// verifica se tem a anotacao LiveClass
		if (! liveClass.isAnnotationPresent(LiveClass.class) )
			throw new IncompatibleLiveClassException("'" + liveClass + "' is not annotated with @LiveClass!");
		
		// verifica se tem um construtor publico vazio
		Constructor<?> defaultConst = LiveClassUtils.getFromCollection(Arrays.asList(liveClass.getConstructors()), c -> c.getParameterCount() == 0 );
		if ( defaultConst == null )
			throw new IncompatibleLiveClassException("'" + liveClass + "' must have a default public constructor!");
		
		
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
				throw new IncompatibleLiveClassException("A LiveClass cannot be used as a return type in none of its public methods!");
			
			// verifica se usa alguma classe interna como retorno do metodo
			if ( innerClassesList.contains(method.getReturnType()) )
				throw new IncompatibleLiveClassException("Inner classes cannot be used as a return type in none of its public methods!");

			// parametros do metodo
			List<Class<?>> paramList = Arrays.asList(method.getParameterTypes());

			// verifica se usa a propria classe como parametro do metodo
			if ( paramList.contains(liveClass) )
				throw new IncompatibleLiveClassException("A LiveClass cannot be used as parameter type in none of its public methods!");
			
			// verifica se usa alguma classe interna como parametro do metodo
			if (! Collections.disjoint(paramList, innerClassesList) )
				throw new IncompatibleLiveClassException("Inner classes cannot be used as parameter type in none of its public methods!");
		}
		
		// TODO: verificar os campos publicos???
	}
	
	/**
	 * <p>
	 * Inspeciona uma classe para obter suas informações.
	 * <p><i>
	 * Inspects the class to get its information.
	 * </i>
	 *  
	 * @param classFile arquivo compilado da classe (.class) a ser inspecionada
	 * <br><i>the (.class) file of the class to be inspected</i>
	 * @return as informações sobre a classe inspecionada
	 * <br><i>the information about the inspected class</i>
	 * @throws IOException em caso de erro na leitura do arquivo da classe
	 * <br><i>if an error occurs when reading the class file</i>
	 * @see org.esfinge.liveprog.reflect.ClassInfo
	 */
	public static ClassInfo inspect(File classFile) throws IOException
	{
		return ( new ClassInspector(Files.readAllBytes(classFile.toPath())).getClassInfo() );
	}
	
	/**
	 * <p>
	 * Inspeciona uma classe para obter suas informações.
	 * <p><i>
	 * Inspects the class to get its information.
	 * </i>
	 *  
	 * @param classBytecode bytecode da classe a ser inspecionada
	 * <br><i>the class bytecode to be inspected</i>
	 * @return as informações sobre a classe inspecionada
	 * <br><i>the information about the inspected class</i>
	 * @see org.esfinge.liveprog.reflect.ClassInfo
	 */
	public static ClassInfo inspect(byte[] classBytecode)
	{
		return ( new ClassInspector(classBytecode).getClassInfo() );
	}
	
	/**
	 * <p>
	 * Inspeciona uma classe para obter suas informações.
	 * <p><i>
	 * Inspects the class to get its information.
	 * </i>
	 *  
	 * @param clazz classe a ser inspecionada
	 * <br><i>the class object to be inspected</i>
	 * @return as informações sobre a classe inspecionada
	 * <br><i>the information about the inspected class</i>
	 * @see org.esfinge.liveprog.reflect.ClassInfo
	 */
	public static ClassInfo inspect(Class<?> clazz)
	{
		if ( clazz == null )
			return null;
		
		// tenta recuperar as informacoes pelo bytecode, que eh mais completo
		// (consegue recuperar as classes internas anonimas)
		byte[] classBytecode = InstrumentationHelper.getClassBytecode(clazz);
		
		if ( classBytecode.length > 0 )
			return ( InstrumentationHelper.inspect(classBytecode) );
		
		// nao deu por bytecode, obtem as informacoes disponiveis no objeto Class
		ClassInfo classInfo = new ClassInfo();
		classInfo.setVersion(-1);
		classInfo.setAccessFlag(clazz.getModifiers());
		classInfo.setName(clazz.getName());
		classInfo.setBytecode(InstrumentationHelper.getClassBytecode(clazz));
		
		// nome da classe pai
		classInfo.setSuperclassName(clazz.getSuperclass() == null ? null : clazz.getSuperclass().getName());
		
		// nomes das interfaces
		for ( Class<?> interf : clazz.getInterfaces() )
			classInfo.addInterfaceName(interf.getName());

		// verifica se eh uma classe interna 
		if ( clazz.getEnclosingClass() != null )
		{
			// o nome da classe externa imediatamente acima que a contem
			classInfo.setEnclosingClassName(clazz.getEnclosingClass().getName());
			
			// o nome da classe externa topo que a declara
			Class<?> dc = clazz.getDeclaringClass();
			
			do
			{
				classInfo.setDeclaringClassName(dc.getName());
			} 
			while ( (dc = dc.getDeclaringClass()) != null );
		}
		
		// classes internas
		// (obs: nao eh possivel recuperar as classes internas anonimas, se houver.. soh por bytecode)
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
		for ( Annotation annot : clazz.getAnnotations() )
			classInfo.addAnnotationInfo(inspect(annot));
		
		return ( classInfo );
	}
	
	/**
	 * <p>
	 * Inspeciona um método para obter suas informações.
	 * <p><i>
	 * Inspects the method to get its information.
	 * </i>
	 *  
	 * @param method método a ser inspecionado
	 * <br><i>the method object to be inspected</i>
	 * @return as informações sobre o método inspecionado
	 * <br><i>the information about the inspected method</i>
	 * @see org.esfinge.liveprog.reflect.MethodInfo
	 */
	public static MethodInfo inspect(Method method)
	{
		if ( method == null )
			return ( null );
		
		MethodInfo methodInfo = new MethodInfo();
		methodInfo.setAccessFlag(method.getModifiers());
		methodInfo.setName(method.getName());
		methodInfo.setReturnType(new TypeHandler(Type.getType(method.getReturnType())));

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
			paramInfo.setType(new TypeHandler(Type.getType(params[i].getType())));
			methodInfo.addParameterInfo(paramInfo);
			
			// anotacoes do parametro
			for ( Annotation annot : params[i].getAnnotations() )
				paramInfo.addAnnotationInfo(inspect(annot));
		}
		
		// anotacoes
		for ( Annotation annot : method.getAnnotations() )
			methodInfo.addAnnotationInfo(inspect(annot));
		
		return ( methodInfo );
	}
	
	/**
	 * <p>
	 * Inspeciona um campo para obter suas informações.
	 * <p><i>
	 * Inspects the field to get its information.
	 * </i>
	 *  
	 * @param field campo/propriedade a ser inspecionado
	 * <br><i>the field object to be inspected</i>
	 * @return as informações sobre o campo inspecionado
	 * <br><i>the information about the inspected field</i>
	 * @see org.esfinge.liveprog.reflect.FieldInfo
	 */
	public static FieldInfo inspect(Field field)
	{
		if ( field == null )
			return ( null );
		
		FieldInfo fieldInfo = new FieldInfo();
		fieldInfo.setAccessFlag(field.getModifiers());
		fieldInfo.setName(field.getName());
		fieldInfo.setType(new TypeHandler(Type.getType(field.getType())));
		
		// anotacoes
		for ( Annotation annot : field.getAnnotations() )
			fieldInfo.addAnnotationInfo(inspect(annot));
		
		return ( fieldInfo );
	}
	
	/**
	 * <p>
	 * Inspeciona a anotação para obter suas informações.
	 * <p><i>
	 * Inspects the annotation to get its information.
	 * </i>
	 *  
	 * @param annotation anotação a ser inspecionada
	 * <br><i>the annotation to be inspected</i>
	 * @return as informações sobre a anotação inspecionada
	 * <br><i>the information about the inspected annotation</i>
	 * @see org.esfinge.liveprog.reflect.AnnotationInfo
	 */
	public static AnnotationInfo inspect(Annotation annotation)
	{
		if ( annotation == null )
			return ( null );
		
		AnnotationInfo annotationInfo = new AnnotationInfo();
		annotationInfo.setName(annotation.annotationType().getName());

		// atributos da anotacao
		for ( Method m : annotation.annotationType().getDeclaredMethods() )
		{
			AnnotationInfo.AttributeInfo attr = new AnnotationInfo.AttributeInfo();
			attr.setName(m.getName());
			attr.setType(new TypeHandler(Type.getType(m.getReturnType())));
			
			try
			{
				// valor do atributo
				Object value = m.invoke(annotation);
				
				// verifica se eh um array
				if ( attr.getType().isArray() )
					for ( int i = 0; i < Array.getLength(value); i++ )
					{
						Object arrayValue = Array.get(value, i);
						
						// verifica se eh uma anotacao
						if ( arrayValue instanceof Annotation )
							attr.addValue(inspect((Annotation) arrayValue));
						else
							attr.addValue(arrayValue);
					}
				
				// verifica se eh uma anotacao
				else if ( value instanceof Annotation )
					attr.addValue(inspect((Annotation) value));
				
				else
					attr.addValue(value);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				// log: erro ao recuperar valor da anotacao
				LiveClassUtils.logError("Erro ao recuperar valor da anotacao: '" + attr.getName() + "'");
				LiveClassUtils.logException(e);
			}

			//
			annotationInfo.addAttributeInfo(attr);
		}
		
		// anotacoes da anotacao
		for ( Annotation ann : annotation.annotationType().getAnnotations() )
		{
			// ignora as anotacoes @Retention e @Target
			if ( ann.annotationType().equals(Retention.class) || ann.annotationType().equals(Target.class) )
				continue;
			
			annotationInfo.addAnnotationInfo(inspect(ann) );
		}

		return ( annotationInfo );
	}
	
	/**
	 * <p>
	 * Obtém uma String com as informações de bytecode de uma classe.
	 * <p><i>
	 * Gets a String describing the bytecode information of a class.
	 * </i>
	 * 
	 * @param classFile arquivo compilado da classe (.class) a ser inspecionada
	 * <br><i>the (.class) file of the class to be inspected</i>
	 * @return uma String com as informações de bytecode da classe informada
	 * <br><i>a String describing the bytecode information of the specified class</i>
	 * @throws IOException em caso de erro na leitura do arquivo
	 * <br><i>if an error occurs when reading the class file</i>
	 */
	public static String inspectToString(File classFile) throws IOException
	{
		return ( inspectToString(Files.readAllBytes(classFile.toPath())) );
	}
	
	/**
	 * <p>
	 * Obtém uma String com as informações de bytecode de uma classe.
	 * <p><i>
	 * Gets a String describing the bytecode information of a class.
	 * </i>
	 * 
	 * @param clazz classe a ser inspecionada
	 * <br><i>the class object to be inspected</i>
	 * @return uma String com as informações de bytecode da classe informada
	 * <br><i>a String describing the bytecode information of the specified class</i>
	 */
	public static String inspectToString(Class<?> clazz)
	{
		return ( inspectToString(getClassBytecode(clazz)) );
	}
	
	/**
	 * <p>
	 * Obtém uma String com as informações de bytecode de uma classe.
	 * <p><i>
	 * Gets a String describing the bytecode information of a class.
	 * </i>
	 * 
	 * @param classInfo informações da classe a ser inspecionada
	 * <br><i>the class information to be inspected</i>
	 * @return uma String com as informações de bytecode da classe informada
	 * <br><i>a String describing the bytecode information of the specified class</i>
	 */
	public static String inspectToString(ClassInfo classInfo)
	{
		return ( inspectToString(classInfo.getBytecode()) );
	}

	/**
	 * <p>
	 * Obtém uma String com as informações de bytecode de uma classe.
	 * <p><i>
	 * Gets a String describing the bytecode information of a class.
	 * </i>
	 * 
	 * @param classBytecode bytecode da classe a ser inspecionada
	 * <br><i>the class bytecode to be inspected</i>
	 * @return uma String com as informações de bytecode da classe informada
	 * <br><i>a String describing the bytecode information of the specified class</i>
	 */
	public static String inspectToString(byte[] classBytecode)
	{
		ClassReader classReader = new ClassReader(classBytecode);
		StringWriter buffer = new StringWriter();
		TraceClassVisitor tracer = new TraceClassVisitor(new PrintWriter(buffer));
		classReader.accept(tracer, 0);
		
		return ( buffer.toString() );
	}	
	
	/**
	 * <p>
	 * Instrumenta a classe para alterar o seu nome e transformá-la numa nova versão.
	 * <p><i>
	 * Transforms a class to a new version, modifying its bytecode and changing its original name.
	 * </i>
	 *  
	 * @param classInfo informações da classe a ser instrumentada
	 * <br><i>the class information to be transformed</i>
	 * @param className nome original da classe
	 * <br><i>the original name of the class</i>
	 * @param newClassName nome da nova versão da classe 
	 * <br><i>the new name of the class on its new version</i>
	 * @return as informações da classe instrumentada em sua nova versão
	 * <br><i>the class information transformed in its new version</i>
	 * @see org.esfinge.liveprog.reflect.ClassInfo
	 */
	public static ClassInfo transform(ClassInfo classInfo, String className, String newClassName)
	{
		// transforma na nova versao da classe
		ClassInfo newClassInfo = inspect(instrumentBytecode(classInfo.getBytecode(), className, newClassName));
		
		// transforma as classes internas
		for (ClassInfo innerClassInfo : classInfo.getInnerClassesInfo() )
			newClassInfo.addInnerClassInfo(transform(innerClassInfo, className, newClassName));
		
		//
		return ( newClassInfo );
	}
	
	/**
	 * <p>
	 * Tenta obter os bytecodes da classe informada.
	 * <p><i>
	 * Try to get the specified class's bytecode.
	 * </i>
	 * 
	 * @param clazz classe cujo bytecode será retornado
	 * <br><i>the class whose bytecode will try to be read</i>
	 * @return os bytecodes da classe, ou um array de bytes vazio caso o arquivo da classe não seja encontrado no classpath da aplicação
	 * <br><i>the class bytecode, or an empty byte array if the class file could not be found in the application classpath</i>
	 */
	public static byte[] getClassBytecode(Class<?> clazz)
	{
		try
		{
			String strURL = "/" + clazz.getName().replace(".", "/") + ".class";
			LiveClassUtils.logDebug("URL -> " + strURL);
			
			URL resourceUrl = clazz.getResource(strURL);
			InputStream classStream = resourceUrl.openStream();
			return ( IOUtils.toByteArray(classStream) );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			
			return ( new byte[0] );
		}
	}
	
	/**
	 * <p>
	 * Traduz um nome interno para um nome qualificado.
	 * <i>Exemplo: org/esfinge/liveprog/LiveClass -&rsaquo; org.esfinge.liveprog.LiveClass</i>
	 * <p><i>
	 * Translates an internal name to a fully qualified name.
	 * Example: org/esfinge/liveprog/LiveClass -&rsaquo; org.esfinge.liveprog.LiveClass
	 * </i>
	 * 
	 * @param internalName o nome interno a ser traduzido
	 * <br><i>the internal name to be translated</i>
	 * @return o nome qualificado traduzido do nome interno informado
	 * <br><i>the fully qualified name translated from the specified internal name</i>
	 */
	public static String toQualifiedName(String internalName)
	{
		if ( internalName != null )
			return ( internalName.replace('/', '.') );
		
		return ( null );
	}
	
	/**
	 * <p>
	 * Traduz um nome qualificado para um nome interno.
	 * <i>Exemplo: org.esfinge.liveprog.LiveClass -&rsaquo; org/esfinge/liveprog/LiveClass</i>
	 * <p><i>
	 * Translates a fully qualified name to an internal name.
	 * Example: org.esfinge.liveprog.LiveClass -&rsaquo; org/esfinge/liveprog/LiveClass
	 * </i>
	 * 
	 * @param qualifiedName o nome qualificado a ser traduzido
	 * <br><i>the fully qualified name to be translated</i>
	 * @return o nome interno traduzido do nome qualificado informado
	 * <br><i>the internal name translated from the specified fully qualified name</i>
	 */
	public static String toInternalName(String qualifiedName)
	{
		if ( qualifiedName != null )
			return ( qualifiedName.replace('.', '/') );
		
		return ( null );
	}
	
	/**
	 * <p>
	 * Instrumenta o bytecode para alterar o nome da classe.
	 * <p><i>
	 * Modifies a class bytecode to change its name.
	 * </i>
	 *  
	 * @param classBytecode bytecode da classe a ser instrumentada
	 * <br><i>the class bytecode to be transformed</i>
	 * @param className nome original da classe
	 * <br><i>the original name of the class</i>
	 * @param newClassName novo nome da classe 
	 * <br><i>the new name of the class</i>
	 * @return o bytecode da classe instrumentado com seu novo nome
	 * <br><i>the class bytecode transformed in its new name</i>
	 */
	private static byte[] instrumentBytecode(byte[] classBytecode, String className, String newClassName)
	{
		ClassWriter classWriter = new ClassWriter(0);
		ClassTransformer transformer = new ClassTransformer(className, newClassName, classWriter);
		
		ClassReader reader = new ClassReader(classBytecode);
		reader.accept(transformer, 0);
		
		return ( classWriter.toByteArray() );
	}	
}
