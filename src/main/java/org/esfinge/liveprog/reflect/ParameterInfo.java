package org.esfinge.liveprog.reflect;

import java.util.HashSet;
import java.util.Set;

import org.esfinge.liveprog.util.Utils;

/**
 * <p>
 * Armazena informações sobre um parâmetro de um método.
 * </p>
 * <p><i>
 * Stores information about a parameter of a method.
 * </i></p>
 */
public class ParameterInfo implements Comparable<ParameterInfo>
{
	// nome do parametro
	private String name;
	
	// tipo do parametro
	private TypeHandler type;
	
	// posicao do parametro no metodo
	private int index;
	
	// informacoes das anotacoes do parametro
	private Set<AnnotationInfo> annotationsInfo;
	
	
	/**
	 * <p>
	 * Construtor padrão.
	 * </p>
	 * <p><i>
	 * Default constructor.
	 * </i></p>
	 */
	public ParameterInfo()
	{
		this.annotationsInfo = new HashSet<AnnotationInfo>();
	}
	
	/**
	 * <p>
	 * Atribui o nome do parâmetro.
	 * </p>
	 * <p><i>
	 * Sets the parameter name.
	 * </i></p>
	 * 
	 * @param name - nome do parâmetro
	 * <br><i>the name of the parameter</i>
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * <p>
	 * Obtém o nome do parâmetro.
	 * </p>
	 * <p><i>
	 * Gets the parameter name.
	 * </i></p>
	 * 
	 * @return o nome do parâmetro
	 * <br><i>the name of the parameter</i>
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * <p>
	 * Atribui o tipo do parâmetro.
	 * </p>
	 * <p><i>
	 * Sets the parameter type.
	 * </i></p>
	 * 
	 * @param type - tipo do parâmetro
	 * <br><i>the type of the parameter</i>
	 * @see org.esfinge.liveprog.reflect.TypeHandler
	 */
	public void setType(TypeHandler type)
	{
		this.type = type;
	}
	
	/**
	 * <p>
	 * Obtém o tipo do parâmetro.
	 * </p>
	 * <p><i>
	 * Gets the parameter type.
	 * </i></p>
	 * 
	 * @return o tipo do parâmetro
	 * <br><i>the type of the parameter</i>
	 * @see org.esfinge.liveprog.reflect.TypeHandler
	 */
	public TypeHandler getType()
	{
		return type;
	}

	/**
	 * <p>
	 * Atribui a posição do parâmetro no método.
	 * </p>
	 * <p><i>
	 * Sets the method parameter position.
	 * </i></p>
	 * 
	 * @param index - posição do parâmetro no método
	 * <br><i>the method parameter position</i>
	 */
	public void setIndex(int index)
	{
		this.index = index;
	}

	/**
	 * <p>
	 * Obtém a posição do parâmetro no método.
	 * </p>
	 * <p><i>
	 * Gets the method parameter position.
	 * </i></p>
	 * 
	 * @return a posição do parâmetro no método
	 * <br><i>the method parameter position</i>
	 */
	public int getIndex()
	{
		return index;
	}

	/**
	 * <p>
	 * Adiciona informações das anotações do parâmetro.
	 * </p>
	 * <p><i>
	 * Adds the parameter annotations.
	 * </i></p>
	 * 
	 * @param annotationsInfo - informações das anotações do parâmetro
	 * <br><i>the annotations of the parameter</i>
	 * @see org.esfinge.liveprog.reflect.AnnotationInfo
	 */
	public void addAnnotationInfo(AnnotationInfo... annotationsInfo)
	{
		Utils.addToCollection(this.annotationsInfo, annotationsInfo);
	}

	/**
	 * <p>
	 * Obtém informações das anotações do parâmetro.
	 * </p>
	 * <p><i>
	 * Gets the parameter annotations.
	 * </i></p>
	 * 
	 * @return as informações das anotações do parâmetro
	 * <br><i>the annotations of the parameter</i>
	 * @see org.esfinge.liveprog.reflect.AnnotationInfo
	 */
	public Set<AnnotationInfo> getAnnotationsInfo()
	{
		return annotationsInfo;
	}
	
	/**
	 * <p>
	 * Verifica se o parâmetro é um array.
	 * </p>
	 * <p><i>
	 * Checks if this parameter is an array.
	 * </i></p>
	 * 
	 * @return <i>true</i> caso o parâmetro seja um array, <i>false</i> caso contrário
	 * <br><i>true if this parameter is an array, false otherwise 
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
