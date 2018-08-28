package org.esfinge.liveprog.instrumentation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.esfinge.liveprog.util.Utils;

/**
 * Armazena informacoes sobre um metodo de uma classe.
 */
public class MethodInfo
{
	// flag de modificador de acesso do metodo
	private int accessFlag;
	
	// nome do metodo
	private String name;
	
	// tipo de retorno do metodo
	private TypeWrapper returnType;
	
	// informacoes dos parametros do metodo
	private Set<ParameterInfo> parametersInfo;
	
	// informacoes das anotacoes do metodo
	private Set<AnnotationInfo> annotationsInfo;
	
	// nomes das excecoes lancadas pelo metodo
	private Set<String> thrownExceptionNames;
	
	
	/**
	 * Construtor padrao.
	 */
	public MethodInfo()
	{
		this.parametersInfo = new TreeSet<ParameterInfo>();
		this.annotationsInfo = new HashSet<AnnotationInfo>();
		this.thrownExceptionNames = new HashSet<String>();
	}
	
	/**
	 * Atribui o nome do metodo.
	 * 
	 * @param name o nome do metodo
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Retorna o nome do metodo.
	 * 
	 * @return o nome do metodo
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Atribui o flag modificador de acesso do metodo.
	 * @param accessFlag o flag modificador de acesso do metodo
	 * @see java.lang.reflect.Modifier
	 */
	public void setAccessFlag(int accessFlag)
	{
		this.accessFlag = accessFlag;
	}
	
	/**
	 * Retorna o flag modificador de acesso do metodo.
	 * 
	 * @return o flag modificador de acesso do metodo
	 * @see java.lang.reflect.Modifier
	 */
	public int getAccessFlag()
	{
		return accessFlag;
	}
	
	/**
	 * Retorna o modo de acesso do metodo.
	 * 
	 * @return o modo de acesso do metodo 
	 * @see org.esfinge.liveprog.instrumentation.AccessModifier
	 */
	public AccessModifier getAccessPermission()
	{
		return ( AccessModifier.decode(this.accessFlag) );
	}
	
	/**
	 * Adiciona as informacoes das anotacoes do metodo.
	 * 
	 * @param annotationsInfo informacoes das anotacoes do metodo
	 * @see org.esfinge.liveprog.instrumentation.AnnotationInfo
	 */
	public void addAnnotationInfo(AnnotationInfo... annotationsInfo)
	{
		Utils.addToCollection(this.annotationsInfo, annotationsInfo);
	}

	/**
	 * Retorna as informacoes das anotacoes do metodo.
	 * 
	 * @return informacoes das anotacoes do metodo
	 * @see org.esfinge.liveprog.instrumentation.AnnotationInfo
	 */
	public Set<AnnotationInfo> getAnnotationsInfo()
	{
		return annotationsInfo;
	}
	
	/**
	 * Atribui o tipo de retorno do metodo.
	 * 
	 * @param type o tipo de retorno do metodo
	 * @see org.esfinge.liveprog.instrumentation.TypeWrapper
	 */
	public void setReturnType(TypeWrapper returnType)
	{
		this.returnType = returnType;
	}
	
	/**
	 * Retorna o tipo de retorno do metodo.
	 * 
	 * @return o tipo de retorno do metodo
	 * @see org.esfinge.liveprog.instrumentation.TypeWrapper
	 */
	public TypeWrapper getReturnType()
	{
		return returnType;
	}

	/**
	 * Adiciona os nomes das excecoes do metodo.
	 * 
	 * @param exceptionNames os nomes das excecoes lancadas do metodo
	 */
	public void addThrownExceptionName(String... exceptionNames)
	{
		Utils.addToCollection(this.thrownExceptionNames, exceptionNames);
	}
	
	/**
	 * Retorna os nomes das excecoes do metodo.
	 * 
	 * @return os nomes das excecoes lancadas do metodo
	 */
	public Set<String> getThrownExceptionNames()
	{
		return thrownExceptionNames;
	}

	/**
	 * Adiciona as informacoes dos parametros do metodo.
	 * 
	 * @param paramsInfo informacoes dos parametros do metodo
	 */
	public void addParameterInfo(ParameterInfo... paramsInfo)
	{
		Utils.addToCollection(this.parametersInfo, paramsInfo);
	}
	
	/**
	 * Retorna as informacoes dos parametros do metodo.
	 * 
	 * @return informacoes dos parametros do metodo
	 */
	public Set<ParameterInfo> getParametersInfo()
	{
		return parametersInfo;
	}
	
	/**
	 * Retorna informacoes do parametro do metodo posicionado no indice informado.
	 *   
	 * @param index a posicao do parametro no metodo
	 * @return informacoes do parametro posicionado no indice informado,
	 * ou <b>null</b> caso a posicao seja invalida
	 */
	public ParameterInfo getParameterAtIndex(int index)
	{
		return ( Utils.getFromCollection(this.parametersInfo, (p) -> p.getIndex() == index) );
	}
	
	/**
	 * Verifica se o metodo eh um construtor da classe.
	 * 
	 * @return <b>true</b> se o metodo representa um construtor da classe,
	 * <b>false</b> caso contrario
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
