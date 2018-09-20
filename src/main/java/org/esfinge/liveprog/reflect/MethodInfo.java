package org.esfinge.liveprog.reflect;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.esfinge.liveprog.util.Utils;

/**
 * <p>
 * Armazena informa��es sobre um m�todo de uma classe.
 * <p><i>
 * Stores information about a method of a class.
 * </i>
 */
public class MethodInfo
{
	// flag de modificador de acesso do metodo
	private int accessFlag;
	
	// nome do metodo
	private String name;
	
	// tipo de retorno do metodo
	private TypeHandler returnType;
	
	// informacoes dos parametros do metodo
	private Set<ParameterInfo> parametersInfo;
	
	// informacoes das anotacoes do metodo
	private Set<AnnotationInfo> annotationsInfo;
	
	// nomes das excecoes lancadas pelo metodo
	private Set<String> thrownExceptionNames;
	
	
	/**
	 * <p>
	 * Construtor padr�o.
	 * <p><i>
	 * Default constructor.
	 * </i>
	 */
	public MethodInfo()
	{
		this.parametersInfo = new TreeSet<ParameterInfo>();
		this.annotationsInfo = new HashSet<AnnotationInfo>();
		this.thrownExceptionNames = new HashSet<String>();
	}
	
	/**
	 * <p>
	 * Atribui o nome do m�todo.
	 * <p><i>
	 * Sets the method name.
	 * </i>
	 * 
	 * @param name nome do m�todo
	 * <br><i>the name of the method</i>
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * <p>
	 * Obt�m o nome do m�todo.
	 * <p><i>
	 * Gets the method name.
	 * </i>
	 * 
	 * @return o nome do m�todo
	 * <br><i>the name of the method</i>
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * <p>
	 * Atribui o valor inteiro representando o modificador de acesso do m�todo. 
	 * <p><i>
	 * Sets the integer value representing the method access modifier.
	 * </i>
	 * 
	 * @param accessFlag valor inteiro representando o modificador de acesso do m�todo
	 * <br><i>the integer value representing the access modifier of the method</i> 
	 * @see java.lang.reflect.Modifier
	 */
	public void setAccessFlag(int accessFlag)
	{
		this.accessFlag = accessFlag;
	}
	
	/**
	 * <p>
	 * Obt�m o valor inteiro representando o modificador de acesso do m�todo.
	 * <p><i>
	 * Gets the integer value representing the method access modifier.
	 * </i>
	 * 
	 * @return o valor inteiro representando o modificador de acesso do m�todo
	 * <br><i>the integer value representing the access modifier of the method</i> 
	 * @see java.lang.reflect.Modifier
	 */
	public int getAccessFlag()
	{
		return accessFlag;
	}
	
	/**
	 * <p>
	 * Obt�m o enum representando o modificador de acesso do m�todo.
	 * <p><i>
	 * Gets the enum value representing the method access modifier.
	 * </i>
	 * 
	 * @return o enum representando o modificador de acesso do m�todo
	 * <br><i>the enum value representing the access modifier of the method</i> 
	 * @see org.esfinge.liveprog.reflect.AccessModifier
	 */
	public AccessModifier getAccessPermission()
	{
		return ( AccessModifier.decode(this.accessFlag) );
	}
	
	/**
	 * <p>
	 * Adiciona informa��es das anota��es do m�todo.
	 * <p><i>
	 * Adds the method annotations.
	 * </i>
	 * 
	 * @param annotationsInfo informa��es das anota��es do m�todo
	 * <br><i>the annotations of the method</i>
	 * @see org.esfinge.liveprog.reflect.AnnotationInfo
	 */
	public void addAnnotationInfo(AnnotationInfo... annotationsInfo)
	{
		Utils.addToCollection(this.annotationsInfo, annotationsInfo);
	}

	/**
	 * <p>
	 * Obt�m informa��es das anota��es do m�todo.
	 * <p><i>
	 * Gets the method annotations.
	 * </i>
	 * 
	 * @return as informa��es das anota��es do m�todo
	 * <br><i>the annotations of the method</i>
	 * @see org.esfinge.liveprog.reflect.AnnotationInfo
	 */
	public Set<AnnotationInfo> getAnnotationsInfo()
	{
		return annotationsInfo;
	}
	
	/**
	 * <p>
	 * Atribui o tipo de retorno do m�todo.
	 * <p><i>
	 * Sets the method return type.
	 * </i>
	 * 
	 * @param returnType tipo de retorno do m�todo
	 * <br><i>the return type of the method</i>
	 * @see org.esfinge.liveprog.reflect.TypeHandler
	 */
	public void setReturnType(TypeHandler returnType)
	{
		this.returnType = returnType;
	}
	
	/**
	 * <p>
	 * Obt�m o tipo de retorno do m�todo.
	 * <p><i>
	 * Gets the method return type.
	 * </i>
	 * 
	 * @return o tipo de retorno do m�todo
	 * <br><i>the return type of the method</i>
	 * @see org.esfinge.liveprog.reflect.TypeHandler
	 */
	public TypeHandler getReturnType()
	{
		return returnType;
	}

	/**
	 * <p>
	 * Adiciona os nomes das exce��es do m�todo.
	 * <p><i>
	 * Adds the method exception names.
	 * </i>
	 * 
	 * @param exceptionNames nomes das exce��es lan�adas pelo m�todo
	 * <br><i>the names of the exceptions thrown by the method</i>
	 */
	public void addThrownExceptionName(String... exceptionNames)
	{
		Utils.addToCollection(this.thrownExceptionNames, exceptionNames);
	}
	
	/**
	 * <p>
	 * Obt�m os nomes das exce��es do m�todo.
	 * <p><i>
	 * Gets the method exception names.
	 * </i>
	 * 
	 * @return os nomes das exce��es lan�adas pelo m�todo
	 * <br><i>the names of the exceptions thrown by the method</i>
	 */
	public Set<String> getThrownExceptionNames()
	{
		return thrownExceptionNames;
	}

	/**
	 * <p>
	 * Adiciona informa��es dos par�metros do m�todo.
	 * <p><i>
	 * Adds the method parameters.
	 * </i>
	 * 
	 * @param paramsInfo informa��es dos par�metros do m�todo
	 * <br><i>the parameters of the method</i>
	 */
	public void addParameterInfo(ParameterInfo... paramsInfo)
	{
		Utils.addToCollection(this.parametersInfo, paramsInfo);
	}
	
	/**
	 * <p>
	 * Obt�m informa��es dos par�metros do m�todo.
	 * <p><i>
	 * Gets the method parameters.
	 * </i>
	 * 
	 * @return as informa��es dos par�metros do m�todo
	 * <br><i>the parameters of the method</i>
	 */
	public Set<ParameterInfo> getParametersInfo()
	{
		return parametersInfo;
	}
	
	/**
	 * <p>
	 * Obt�m informa��es do par�metro do m�todo da posi��o informada.
	 * <p><i>
	 * Gets the method parameter at the specified position.
	 * </i>
	 * 
	 * @param index posi��o do par�metro desejado
	 * <br><i>the desired method parameter position</i>
	 * @return as informa��es do par�metro da posi��o informada, ou <i>null</i> caso a posi��o seja inv�lida
	 * <br><i>the method parameter at the specified position, or null if the specified index is invalid</i>
	 */
	public ParameterInfo getParameterAtIndex(int index)
	{
		return ( Utils.getFromCollection(this.parametersInfo, (p) -> p.getIndex() == index) );
	}
	
	/**
	 * <p>
	 * Verifica se o m�todo representa um construtor de uma classe.
	 * <p><i>
	 * Checks if this method represents a constructor of a class.
	 * </i>
	 * 
	 * @return <i>true</i> caso o m�todo seja um construtor, <i>false</i> caso contr�rio
	 * <br><i>true if this method represents a constructor, false otherwise</i>
	 */
	public boolean isConstructor()
	{
		return ( this.name.equals("<init>") );
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + accessFlag;
		result = prime * result + ((annotationsInfo == null) ? 0 : annotationsInfo.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parametersInfo == null) ? 0 : parametersInfo.hashCode());
		result = prime * result + ((returnType == null) ? 0 : returnType.hashCode());
		result = prime * result + ((thrownExceptionNames == null) ? 0 : thrownExceptionNames.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodInfo other = (MethodInfo) obj;
		if (accessFlag != other.accessFlag)
			return false;
		if (annotationsInfo == null)
		{
			if (other.annotationsInfo != null)
				return false;
		} else if (!annotationsInfo.equals(other.annotationsInfo))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parametersInfo == null)
		{
			if (other.parametersInfo != null)
				return false;
		} else if (!parametersInfo.equals(other.parametersInfo))
			return false;
		if (returnType == null)
		{
			if (other.returnType != null)
				return false;
		} else if (!returnType.equals(other.returnType))
			return false;
		if (thrownExceptionNames == null)
		{
			if (other.thrownExceptionNames != null)
				return false;
		} else if (!thrownExceptionNames.equals(other.thrownExceptionNames))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		StringBuilder tmp;
		
		sb.append(this.getAccessPermission() + " ");
		sb.append(this.returnType + " ");
		sb.append(this.name + "(");
		
		if (! this.parametersInfo.isEmpty())
		{
			tmp = new StringBuilder(Arrays.toString(this.parametersInfo.toArray()));
			sb.append(tmp.substring(1,tmp.length()-1));
		}
		
		sb.append(")");
		
		if (! this.thrownExceptionNames.isEmpty())
		{
			tmp = new StringBuilder(Arrays.toString(this.thrownExceptionNames.toArray()));
			sb.append("throws " + tmp.substring(1,tmp.length()-1));
		}
		
		return ( sb.toString() );
	}
}
