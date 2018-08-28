package org.esfinge.liveprog.instrumentation;

import java.util.HashSet;
import java.util.Set;

import org.esfinge.liveprog.util.Utils;

/**
 * Armazena informacoes sobre um parametro de um metodo de uma classe.
 */
public class ParameterInfo implements Comparable<ParameterInfo>
{
	// nome do parametro
	private String name;
	
	// tipo do parametro
	private TypeWrapper type;
	
	// posicao do parametro no metodo
	private int index;
	
	// informacoes das anotacoes do parametro
	private Set<AnnotationInfo> annotationsInfo;
	
	
	/**
	 * Construtor padrao.
	 */
	public ParameterInfo()
	{
		this.annotationsInfo = new HashSet<AnnotationInfo>();
	}
	
	/**
	 * Atribui o nome do parametro.
	 * 
	 * @param name o nome do parametro
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Retorna o nome do parametro.
	 * 
	 * @return o nome do parametro
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Atribui o tipo do parametro.
	 * 
	 * @param type o tipo do parametro
	 * @see org.esfinge.liveprog.instrumentation.TypeWrapper
	 */
	public void setType(TypeWrapper type)
	{
		this.type = type;
	}
	
	/**
	 * Retorna o tipo do parametro.
	 * 
	 * @return o tipo do parametro
	 * @see org.esfinge.liveprog.instrumentation.TypeWrapper
	 */
	public TypeWrapper getType()
	{
		return type;
	}

	/**
	 * Atribui o indice do parametro no metodo.
	 * 
	 * @param index o indice do parametro no metodo, comecando por 0
	 */
	public void setIndex(int index)
	{
		this.index = index;
	}

	/**
	 * Retorna o indice do parametro no metodo.
	 * 
	 * @return o indice do parametro no metodo, sendo 0 a primeira posicao
	 */
	public int getIndex()
	{
		return index;
	}

	/**
	 * Adiciona as informacoes das anotacoes do parametro.
	 * 
	 * @param annotationsInfo informacoes das anotacoes do parametro
	 * @see org.esfinge.liveprog.instrumentation.AnnotationInfo
	 */
	public void addAnnotationInfo(AnnotationInfo... annotationsInfo)
	{
		Utils.addToCollection(this.annotationsInfo, annotationsInfo);
	}

	/**
	 * Retorna as informacoes das anotacoes do parametro.
	 * 
	 * @return informacoes das anotacoes do parametro
	 * @see org.esfinge.liveprog.instrumentation.AnnotationInfo
	 */
	public Set<AnnotationInfo> getAnnotationsInfo()
	{
		return annotationsInfo;
	}
	
	/**
	 * Retorna se o parametro eh um array.
	 * 
	 * @return <b>true</b> caso o parametro seja do tipo array, 
	 * <b>false</b> caso contrario 
	 */
	public boolean isArray()
	{
		return ( this.type.isArray() );
	}
	
	@Override
	public int compareTo(ParameterInfo p)
	{
		return ( Integer.compare(this.index, p.index) );
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotationsInfo == null) ? 0 : annotationsInfo.hashCode());
		result = prime * result + index;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		ParameterInfo other = (ParameterInfo) obj;
		if (annotationsInfo == null)
		{
			if (other.annotationsInfo != null)
				return false;
		} else if (!annotationsInfo.equals(other.annotationsInfo))
			return false;
		if (index != other.index)
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null)
		{
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return ( String.format("%s %s", this.type, this.name) );
	}
}
