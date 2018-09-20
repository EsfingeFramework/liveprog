package org.esfinge.liveprog.reflect;

import java.util.HashSet;
import java.util.Set;

import org.esfinge.liveprog.util.Utils;

/**
 * <p>
 * Armazena informa��es sobre uma classe.
 * <p><i>
 * Stores information about a class.
 * </i>
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
	
	// nome da classe externa imediatamente acima (para classes internas)
	private String enclosingClassName;
	
	// nome da classe externa topo onde essa classe foi declarada (para classes internas)
	private String declaringClassName;
	
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
	
	// os bytecodes da classe
	private byte[] bytecode;

	
	/**
	 * <p>
	 * Construtor padr�o.
	 * <p><i>
	 * Default constructor.
	 * </i>
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
	 * <p>
	 * Atribui a vers�o do formato de arquivo de classe.
	 * <p><i>
	 * Sets the class file format version.
	 * </i>
	 * 
	 * @param version vers�o do formato de arquivo de classe
	 * <br><i>the class file format version</i>
	 */
	public void setVersion(int version)
	{
		this.version = version;
	}
	
	/**
	 * <p>
	 * Obt�m a vers�o do formato do arquivo de classe.
	 * <p><i>
	 * Gets the class file format version.
	 * </i>
	 * 
	 * @return a vers�o do formato do arquivo de classe
	 * <br><i>the class file format version</i> 
	 */
	public int getVersion()
	{
		return version;
	}
	
	/**
	 * <p>
	 * Atribui o valor inteiro representando o modificador de acesso da classe. 
	 * <p><i>
	 * Sets the integer value representing the class access modifier.
	 * </i>
	 * 
	 * @param accessFlag valor inteiro representando o modificador de acesso da classe
	 * <br><i>the integer value representing the access modifier of the class</i> 
	 * @see java.lang.reflect.Modifier
	 */
	public void setAccessFlag(int accessFlag)
	{
		this.accessFlag = accessFlag;
	}

	/**
	 * <p>
	 * Obt�m o valor inteiro representando o modificador de acesso da classe.
	 * <p><i>
	 * Gets the integer value representing the class access modifier.
	 * </i>
	 * 
	 * @return o valor inteiro representando o modificador de acesso da classe
	 * <br><i>the integer value representing the access modifier of the class</i> 
	 * @see java.lang.reflect.Modifier
	 */
	public int getAccessFlag()
	{
		return accessFlag;
	}
	
	/**
	 * <p>
	 * Obt�m o enum representando o modificador de acesso da classe.
	 * <p><i>
	 * Gets the enum value representing the class access modifier.
	 * </i>
	 * 
	 * @return o enum representando o modificador de acesso da classe
	 * <br><i>the enum value representing the access modifier of the class</i> 
	 * @see org.esfinge.liveprog.reflect.AccessModifier
	 */
	public AccessModifier getAccessPermission()
	{
		return ( AccessModifier.decode(this.accessFlag) );
	}

	/**
	 * <p>
	 * Atribui o nome qualificado da classe.
	 * <p><i>
	 * Sets the class fully qualified name.
	 * </i>
	 * 
	 * @param name nome qualificado da classe
	 * <br><i>the fully qualified name of the class</i>
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * <p>
	 * Obt�m o nome qualificado da classe.
	 * <p><i>
	 * Gets the class fully qualified name.
	 * </i>
	 * 
	 * @return o nome qualificado da classe
	 * <br><i>the fully qualified name of the class</i>
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * <p>
	 * Obt�m o nome simples da classe.
	 * <p><i>
	 * Gets the class simple name.
	 * </i>
	 * 
	 * @return o nome simples da classe
	 * <br><i>the simple name of the class</i>
	 */
	public String getSimpleName()
	{
		if ( this.name == null )
			return null;
		
		int pos = 0;
		if ( this.isInnerClass() )
			pos = this.enclosingClassName.length() + 1;
		else
			pos = this.name.lastIndexOf('.') + 1;
		
		return ( this.name.substring(pos) );
	}

	/**
	 * <p>
	 * Obt�m o nome do pacote da classe.
	 * <p><i>
	 * Gets the class package name.
	 * </i>
	 * 
	 * @return o nome do pacote da classe
	 * <br><i>the package name of the class</i>
	 */
	public String getPackage()
	{
		if ( this.name != null && (this.name.lastIndexOf('.') > 0))
			return ( this.name.substring(0, this.name.lastIndexOf('.')) );
		
		return ( "" );
	}

	/**
	 * <p>
	 * Atribui o nome da superclasse.
	 * <p><i>
	 * Sets the superclass name.
	 * </i>
	 * 
	 * @param superclassName nome da classe pai da classe
	 * <br><i>the name of the parent class of the class</i>
	 */
	public void setSuperclassName(String superclassName)
	{
		this.superclassNames = superclassName;
	}

	/**
	 * <p>
	 * Obt�m o nome da superclasse.
	 * <p><i>
	 * Gets the superclass name.
	 * </i>
	 * 
	 * @return o nome da classe pai da classe
	 * <br><i>the name of the parent class of the class</i>
	 */
	public String getSuperclassName()
	{
		return superclassNames;
	}
	
	/**
	 * <p>
	 * Atribui o nome da classe externa imediatamente acima desta classe interna.
	 * <p><i>
	 * Sets the name of the immediately enclosing class of this inner class.
	 * </i>
	 * 
	 * @param enclosingClassName nome da classe externa imediatamente acima que cont�m essa classe interna
	 * <br><i>the name of the immediately enclosing class of this inner class</i> 
	 */
	public void setEnclosingClassName(String enclosingClassName)
	{
		this.enclosingClassName = enclosingClassName;
	}

	/**
	 * <p>
	 * Obt�m o nome da classe externa imediatamente acima desta classe interna.
	 * Se n�o for uma classe interna, retorna <i>null</i>
	 * <p><i>
	 * Gets the name of the immediately enclosing class of this inner class.
	 * Returns null if this is not an inner class.
	 * </i>
	 * 
	 * @return o nome da classe externa imediatamente acima que cont�m essa classe interna, ou <i>null</i> caso n�o seja uma classe interna
	 * <br><i>the name of the immediately enclosing class of this inner class, or null if this is not an inner class</i> 
	 */
	public String getEnclosingClassName()
	{
		return enclosingClassName;
	}
	
	/**
	 * <p>
	 * Atribui o nome da classe externa topo onde esta classe interna foi declarada.
	 * <p><i>
	 * Sets the name of the top class in which this inner class was declared.
	 * </i>
	 * 
	 * @param declaringClassName o nome da classe externa topo onde esta classe interna foi declarada
	 * <br><i>the name of the top class in which this inner class was declared</i> 
	 */
	public void setDeclaringClassName(String declaringClassName)
	{
		this.declaringClassName = declaringClassName;
	}

	/**
	 * <p>
	 * Obt�m o nome da classe externa topo onde esta classe interna foi declarada.
	 * Se n�o for uma classe interna, retorna <i>null</i>
	 * <p><i>
	 * Gets the name of the top class in which this inner class was declared.
	 * Returns null if this is not an inner class.
	 * </i>
	 * 
	 * @return o nome da classe externa topo onde esta classe interna foi declarada, ou <i>null</i> caso n�o seja uma classe interna
	 * <br><i>the name of the top class in which this inner class was declared, or null if this is not an inner class</i> 
	 */
	public String getDeclaringClassName()
	{
		return declaringClassName;
	}
	
	/**
	 * <p>
	 * Verifica se � uma classe interna.
	 * <p><i>
	 * Checks if it is an inner class.
	 * </i>
	 * 
	 * @return <i>true</i> caso seja uma classe interna, <i>false</i> caso contr�rio
	 * <br><i>true if it is an inner class, false otherwise</i> 
	 */
	public boolean isInnerClass()
	{
		return ( this.enclosingClassName != null );
	}
	
	/**
	 * <p>
	 * Verifica se cont�m classes internas.
	 * <p><i>
	 * Checks if contains inner classes.
	 * </i>
	 * 
	 * @return <i>true</i> caso a classe tenha classes internas, <i>false</i> caso contr�rio
	 * <br><i>true if it contains inner classes, false otherwise</i> 
	 */
	public boolean containsInnerClasses()
	{
		return (! this.innerClassNames.isEmpty() );
	}

	/**
	 * <p>
	 * Adiciona os nomes das classes internas.
	 * <p><i>
	 * Adds the inner class names.
	 * </i>
	 * 
	 * @param innerClassNames nomes das classes internas da classe
	 * <br><i>the names of the inner classes of the class</i>
	 */
	public void addInnerClassName(String... innerClassNames)
	{
		Utils.addToCollection(this.innerClassNames,  innerClassNames);
	}
	
	/**
	 * <p>
	 * Obt�m os nomes das classes internas.
	 * <p><i>
	 * Gets the inner class names.
	 * </i>
	 * 
	 * @return os nomes das classes internas da classe
	 * <br><i>the names of the inner classes of the class</i>
	 */
	public Set<String> getInnerClassNames()
	{
		return innerClassNames;
	}
	
	/**
	 * <p>
	 * Adiciona informa��es das classes internas.
	 * <p><i>
	 * Adds the inner classes.
	 * </i>
	 * 
	 * @param innerClassesInfo informa��es das classes internas da classe
	 * <br><i>the inner classes of the class</i>
	 */
	public void addInnerClassInfo(ClassInfo... innerClassesInfo)
	{
		Utils.addToCollection(this.innerClassesInfo, innerClassesInfo);
	}
	
	/**
	 * <p>
	 * Obt�m informa��es das classes internas.
	 * <p><i>
	 * Gets the inner classes.
	 * </i>
	 * 
	 * @return as informa��es das classes internas da classe
	 * <br><i>the inner classes of the class</i>
	 */
	public Set<ClassInfo> getInnerClassesInfo()
	{
		return ( this.innerClassesInfo );
	}
	
	/**
	 * <p>
	 * Adiciona os nomes das interfaces da classe.
	 * <p><i>
	 * Adds the class interface names.
	 * </i>
	 * 
	 * @param interfaceNames nomes das interfaces implementadas pela classe
	 * <br><i>the names of the interfaces implemented by the class</i>
	 */
	public void addInterfaceName(String... interfaceNames)
	{
		Utils.addToCollection(this.interfaceNames, interfaceNames);
	}

	/**
	 * <p>
	 * Obt�m os nomes das interfaces da classe.
	 * <p><i>
	 * Gets the class interface names.
	 * </i>
	 * 
	 * @return os nomes das interfaces implementadas pela classe
	 * <br><i>the names of the interfaces implemented by the class</i>
	 */
	public Set<String> getInterfaceNames()
	{
		return interfaceNames;
	}

	/**
	 * <p>
	 * Adiciona informa��es dos campos da classe.
	 * <p><i>
	 * Adds the class fields.
	 * </i>
	 * 
	 * @param fieldsInfo informa��es dos campos da classe
	 * <br><i>the fields of the class</i>
	 * @see org.esfinge.liveprog.reflect.FieldInfo
	 */
	public void addFieldInfo(FieldInfo... fieldsInfo)
	{
		Utils.addToCollection(this.fieldsInfo, fieldsInfo);
	}

	/**
	 * <p>
	 * Obt�m informa��es dos campos da classe.
	 * <p><i>
	 * Gets the class fields.
	 * </i>
	 * 
	 * @return as informa��es dos campos da classe
	 * <br><i>the fields of the class</i>
	 * @see org.esfinge.liveprog.reflect.FieldInfo
	 */
	public Set<FieldInfo> getFieldsInfo()
	{
		return fieldsInfo;
	}

	/**
	 * <p>
	 * Adiciona informa��es dos m�todos da classe.
	 * <p><i>
	 * Adds the class methods.
	 * </i>
	 * 
	 * @param methodsInfo informa��es dos m�todos da classe
	 * <br><i>the methods of the class</i>
	 * @see org.esfinge.liveprog.reflect.MethodInfo
	 */
	public void addMethodInfo(MethodInfo... methodsInfo)
	{
		Utils.addToCollection(this.methodsInfo, methodsInfo);
	}

	/**
	 * <p>
	 * Obt�m informa��es dos m�todos das classes.
	 * <p><i>
	 * Gets the class methods.
	 * </i>
	 * 
	 * @return as informa��es dos m�todos da classe
	 * <br><i>the methods of the class</i>
	 * @see org.esfinge.liveprog.reflect.MethodInfo
	 */
	public Set<MethodInfo> getMethodsInfo()
	{
		return methodsInfo;
	}

	/**
	 * <p>
	 * Adiciona informa��es das anota��es da classe.
	 * <p><i>
	 * Adds the class annotations.
	 * </i>
	 * 
	 * @param annotationsInfo informa��es das anota��es da classe
	 * <br><i>the annotations of the class</i>
	 * @see org.esfinge.liveprog.reflect.AnnotationInfo
	 */
	public void addAnnotationInfo(AnnotationInfo... annotationsInfo)
	{
		Utils.addToCollection(this.annotationsInfo, annotationsInfo);
	}

	/**
	 * <p>
	 * Obt�m informa��es das anota��es da classe.
	 * <p><i>
	 * Gets the class annotations.
	 * </i>
	 * 
	 * @return as informa��es das anota��es da classe
	 * <br><i>the annotations of the class</i>
	 * @see org.esfinge.liveprog.reflect.AnnotationInfo
	 */
	public Set<AnnotationInfo> getAnnotationsInfo()
	{
		return annotationsInfo;
	}

	/**
	 * <p>
	 * Atribui os bytecodes da classe.
	 * <p><i>
	 * Sets the class bytecode.
	 * </i>
	 * 
	 * @param classBytecode bytecodes da classe
	 * <br><i>the bytecodes of the class</i>
	 */
	public void setBytecode(byte[] classBytecode)
	{
		this.bytecode = classBytecode;
	}

	/**
	 * <p>
	 * Obt�m os bytecodes da classe.
	 * <p><i>
	 * Gets the class bytecode.
	 * </i>
	 * 
	 * @return os bytecodes da classe
	 * <br><i>the bytecodes of the class</i>
	 */
	public byte[] getBytecode()
	{
		return bytecode;
	}
	
	/**
	 * <p>
	 * Obt�m o objeto Class da classe.
	 * <p><i>
	 * Gets the class's Class object.
	 * </i>
	 * 
	 * @return o objeto Class da classe, ou <i>null</i> se a classe ainda n�o foi carregada
	 * <br><i>the Class object of the class, or null if the class has not been loaded</i>
	 * @see java.lang.Class#forName(String)
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
