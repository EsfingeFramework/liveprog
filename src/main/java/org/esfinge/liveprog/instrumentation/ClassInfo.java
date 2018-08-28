package org.esfinge.liveprog.instrumentation;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.esfinge.liveprog.util.Utils;

/**
 * Armazena informacoes sobre uma classe.
 */
public class ClassInfo
{
	// class file format version 
	private int version;
	
	// flag modificador de acesso da classe
	private int accessFlag;
	
	// nome qualificado da classe
	private String name;
	
	// nome da classe pai
	private String superclassNames;
	
	// nome da classe externa (para classes internas)
	private String outerClassName;
	
	// informacoes da classe externa
	// TODO: REMOVIDO PARA EVITAR PROBLEMAS DE REFERENCIA CIRCULAR
//	private ClassInfo outerClassInfo;
	
	// nomes das classes internas 
	private Set<String> innerClassNames;
	
	// informacoes das classes internas
	private Set<ClassInfo> innerClassesInfo;
	
	// nomes das interfaces implementadas pela classe
	private Set<String> interfaceNames;
	
	// informacoes dos campos da classe
	private Set<FieldInfo> fieldsInfo;
	
	// informacoes dos metodos da classe
	private Set<MethodInfo> methodsInfo;
	
	// informacoes das anotacoes da classe
	private Set<AnnotationInfo> annotationsInfo;
	
	// o arquivo da classe compilada
	private File classFile;
	
	// os bytecodes da classe
	private byte[] bytecode;

	
	/**
	 * Construtor padrao.
	 */
	public ClassInfo()
	{
		this.innerClassNames = new HashSet<String>();
		this.interfaceNames = new HashSet<String>();
		this.fieldsInfo = new HashSet<FieldInfo>();
		this.methodsInfo = new HashSet<MethodInfo>();
		this.annotationsInfo = new HashSet<AnnotationInfo>();		
		this.innerClassesInfo = new HashSet<ClassInfo>();
	}

	/**
	 * Atribui a versao do formato de arquivo de classe (class file format version).
	 * 
	 * @param version class file format version
	 */
	public void setVersion(int version)
	{
		this.version = version;
	}
	
	/**
	 * Retorna a versao do formato de arquivo de classe (class file format version).
	 * 
	 * @return class file format version 
	 */
	public int getVersion()
	{
		return version;
	}
	
	/**
	 * Retorna o flag modificador de acesso da classe.
	 * 
	 * @param accessFlag o flag modificador de acesso da classe 
	 * @see java.lang.reflect.Modifier
	 */

	public void setAccessFlag(int accessFlag)
	{
		this.accessFlag = accessFlag;
	}

	/**
	 * Retorna o flag modificador de acesso da classe.
	 * 
	 * @return o flag modificador de acesso da classe 
	 * @see java.lang.reflect.Modifier
	 */
	public int getAccessFlag()
	{
		return accessFlag;
	}
	
	/**
	 * Retorna o modo de acesso da classe.
	 * 
	 * @return o modo de acesso da classe 
	 * @see org.esfinge.liveprog.instrumentation.AccessModifier
	 */
	public AccessModifier getAccessPermission()
	{
		return ( AccessModifier.decode(this.accessFlag) );
	}

	/**
	 * Atribui o nome qualificado da classe.
	 * 
	 * @param name o nome completo da classe
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Retorna o nome qualificado da classe.
	 * 
	 * @return o nome completo da classe
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retorna o nome da classe.
	 * 
	 * @return o nome simples da classe
	 */
	public String getSimpleName()
	{
		if ( this.name == null )
			return null;
		
		int pos = 0;
		if ( this.isInnerClass() )
			pos = this.name.lastIndexOf('$') + 1;
		else
			pos = this.name.lastIndexOf('.') + 1;
		
		return ( this.name.substring(pos) );
	}

	/**
	 * Retorna o pacote da classe.
	 * 
	 * @return o pacote ao qual a classe pertence
	 */
	public String getPackage()
	{
		if ( this.name != null && (this.name.lastIndexOf('.') > 0))
			return ( this.name.substring(0, this.name.lastIndexOf('.')) );
		
		return ( "" );
	}

	/**
	 * Atribui o nome da superclasse da classe.
	 * 
	 * @param superclassName o nome da classe pai da classe
	 */
	public void setSuperclassName(String superclassName)
	{
		this.superclassNames = superclassName;
	}

	/**
	 * Retorna o nome da superclasse da classe.
	 * 
	 * @return o nome da classe pai da classe
	 */
	public String getSuperclassName()
	{
		return superclassNames;
	}
	
	/**
	 * Atribui o nome da classe externa (para classes internas).
	 * 
	 * @param outerClassName o nome da classe externa, que contem essa classe interna 
	 */
	public void setOuterClassName(String outerClassName)
	{
		this.outerClassName = outerClassName;
	}

	/**
	 * Retorna o nome da classe externa (para classes internas).
	 * 
	 * @return o nome da classe externa, que contem essa classe interna 
	 */
	public String getOuterClassName()
	{
		return outerClassName;
	}
	
//	/**
//	 * Atribui informacoes da classe externa (para classes internas).
//	 * 
//	 * @param outerClassInfo informacoes da classe externa, que contem essa classe interna 
//	 */
	/*
	// TODO: REMOVIDO PARA EVITAR PROBLEMAS DE REFERENCIA CIRCULAR
	public void setOuterClassInfo(ClassInfo outerClassInfo)
	{
		this.outerClassInfo  = outerClassInfo;
	}
	/*
	
//	/**
//	 * Retorna informacoes da classe externa (para classes internas).
//	 * 
//	 * @return informacoes classe externa, que contem essa classe interna 
//	 */
	/*
	// TODO: REMOVIDO PARA EVITAR PROBLEMAS DE REFERENCIA CIRCULAR
	public ClassInfo getOuterClassInfo()
	{
		return outerClassInfo;
	}
	*/
	
	/**
	 * Retorna se essa classe eh uma classe interna.
	 * 
	 * @return <b>true</b> caso seja uma classe interna, 
	 * <b>false</b> caso contrario 
	 */
	public boolean isInnerClass()
	{
		return ( this.outerClassName != null );
	}
	
	/**
	 * Retorna se essa classe contem classes internas.
	 * 
	 * @return <b>true</b> caso tenha classes internas,
	 * <b>false</b> caso contrario
	 */
	public boolean hasInnerClasses()
	{
		return (! this.innerClassNames.isEmpty() );
	}

	/**
	 * Adiciona os nomes das classes internas da classe.
	 * 
	 * @param innerClassNames os nomes das classes internas da classe
	 */
	public void addInnerClassName(String... innerClassNames)
	{
		Utils.addToCollection(this.innerClassNames,  innerClassNames);
	}
	
	/**
	 * Retorna os nomes das classes internas da classe.
	 * 
	 * @return os nomes das classes internas da classe
	 */
	public Set<String> getInnerClassNames()
	{
		return innerClassNames;
	}
	
	/**
	 * Adiciona as informacoes das classes internas da classe.
	 * 
	 * @param innerClassesInfo informacoes das classes internas da classe
	 */
	public void addInnerClassInfo(ClassInfo... innerClassesInfo)
	{
		Utils.addToCollection(this.innerClassesInfo, innerClassesInfo);
	}
	
	/**
	 * Retorna as informacoes das classes internas da classe.
	 * 
	 * @return informacoes das classes internas da classe
	 */
	public Set<ClassInfo> getInnerClassesInfo()
	{
		return ( this.innerClassesInfo );
	}
	
	/**
	 * Adiciona os nomes das interfaces da classe.
	 * 
	 * @param interfaceNames os nomes das interfaces implementadas pela classe
	 */
	public void addInterfaceName(String... interfaceNames)
	{
		Utils.addToCollection(this.interfaceNames, interfaceNames);
	}

	/**
	 * Retorna os nomes das interfaces da classe.
	 * 
	 * @return os nomes das interfaces implementadas pela classe
	 */
	public Set<String> getInterfaceNames()
	{
		return interfaceNames;
	}

	/**
	 * Adiciona as informacoes dos campos/propriedades da classe.
	 * 
	 * @param fieldsInfo informacoes dos campos da classe
	 * @see org.esfinge.liveprog.instrumentation.FieldInfo
	 */
	public void addFieldInfo(FieldInfo... fieldsInfo)
	{
		Utils.addToCollection(this.fieldsInfo, fieldsInfo);
	}

	/**
	 * Retorna as informacoes dos campos/propriedades da classe.
	 * 
	 * @return informacoes dos campos da classe
	 * @see org.esfinge.liveprog.instrumentation.FieldInfo
	 */
	public Set<FieldInfo> getFieldsInfo()
	{
		return fieldsInfo;
	}

	/**
	 * Adiciona as informacoes dos metodos da classe.
	 * 
	 * @param methodsInfo informacoes dos metodos da classe
	 * @see org.esfinge.liveprog.instrumentation.MethodInfo
	 */
	public void addMethodInfo(MethodInfo... methodsInfo)
	{
		Utils.addToCollection(this.methodsInfo, methodsInfo);
	}

	/**
	 * Retorna as informacoes dos metodos da classe.
	 * 
	 * @return informacoes dos metodos da classe
	 * @see org.esfinge.liveprog.instrumentation.MethodInfo
	 */
	public Set<MethodInfo> getMethodsInfo()
	{
		return methodsInfo;
	}

	/**
	 * Adiciona as informacoes das anotacoes da classe.
	 * 
	 * @param annotationsInfo informacoes das anotacoes da classe
	 * @see org.esfinge.liveprog.instrumentation.AnnotationInfo
	 */
	public void addAnnotationInfo(AnnotationInfo... annotationsInfo)
	{
		Utils.addToCollection(this.annotationsInfo, annotationsInfo);
	}

	/**
	 * Retorna as informacoes das anotacoes da classe.
	 * 
	 * @return informacoes das anotacoes da classe
	 * @see org.esfinge.liveprog.instrumentation.AnnotationInfo
	 */
	public Set<AnnotationInfo> getAnnotationsInfo()
	{
		return annotationsInfo;
	}

	/**
	 * Atribui o arquivo compilado da classe.
	 * 
	 * @param classFile o arquivo da classe compilada (.class)
	 */
	public void setClassFile(File classFile)
	{
		this.classFile = classFile;
	}

	/**
	 * Retorna o arquivo da classe.
	 * 
	 * @return o arquivo da classe compilada (.class),
	 * ou <b>null</b> caso o arquivo nao tenha sido especificado
	 */
	public File getClassFile()
	{
		return classFile;
	}

	/**
	 * Atribui os bytecodes da classe.
	 * 
	 * @param classBytecode os bytecodes da classe
	 */
	public void setBytecode(byte[] classBytecode)
	{
		this.bytecode = classBytecode;
	}

	/**
	 * Retorna os bytecodes da classe.
	 * 
	 * @return os bytecodes da classe
	 */
	public byte[] getBytecode()
	{
		return bytecode;
	}
	
	/**
	 * Retorna o objeto Class da classe.
	 * @return o objeto Class da classe, ou <b>null</b> se a classe nao foi carregada
	 * @see Class.forName
	 */
	public Class<?> getClazz()
	{
		try
		{
			return ( Class.forName(this.name) );
		} 
		catch (ClassNotFoundException e)
		{
			// TODO: debug
			e.printStackTrace();
			
			return ( null );
		}
	}

	@Override
	public String toString()
	{
		return ( String.format("%s class %s", this.getAccessPermission(), this.getName()) );
	}
}
