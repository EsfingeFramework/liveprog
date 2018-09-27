package org.esfinge.liveprog.reflect;

import java.util.HashSet;
import java.util.Set;

import org.esfinge.liveprog.util.LiveClassUtils;

/**
 * <p>
 * Armazena informa��es sobre um par�metro de um m�todo.
 * <p><i>
 * Stores information about a parameter of a method.
 * </i>
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
	 * Construtor padr�o.
	 * <p><i>
	 * Default constructor.
	 * </i>
	 */
	public ParameterInfo()
	{
		this.annotationsInfo = new HashSet<AnnotationInfo>();
	}
	
	/**
	 * <p>
	 * Atribui o nome do par�metro.
	 * <p><i>
	 * Sets the parameter name.
	 * </i>
	 * 
	 * @param name nome do par�metro
	 * <br><i>the name of the parameter</i>
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * <p>
	 * Obt�m o nome do par�metro.
	 * <p><i>
	 * Gets the parameter name.
	 * </i>
	 * 
	 * @return o nome do par�metro
	 * <br><i>the name of the parameter</i>
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * <p>
	 * Atribui o tipo do par�metro.
	 * <p><i>
	 * Sets the parameter type.
	 * </i>
	 * 
	 * @param type tipo do par�metro
	 * <br><i>the type of the parameter</i>
	 * @see org.esfinge.liveprog.reflect.TypeHandler
	 */
	public void setType(TypeHandler type)
	{
		this.type = type;
	}
	
	/**
	 * <p>
	 * Obt�m o tipo do par�metro.
	 * <p><i>
	 * Gets the parameter type.
	 * </i>
	 * 
	 * @return o tipo do par�metro
	 * <br><i>the type of the parameter</i>
	 * @see org.esfinge.liveprog.reflect.TypeHandler
	 */
	public TypeHandler getType()
	{
		return type;
	}

	/**
	 * <p>
	 * Atribui a posi��o do par�metro no m�todo.
	 * <p><i>
	 * Sets the method parameter position.
	 * </i>
	 * 
	 * @param index posi��o do par�metro no m�todo
	 * <br><i>the method parameter position</i>
	 */
	public void setIndex(int index)
	{
		this.index = index;
	}

	/**
	 * <p>
	 * Obt�m a posi��o do par�metro no m�todo.
	 * <p><i>
	 * Gets the method parameter position.
	 * </i>
	 * 
	 * @return a posi��o do par�metro no m�todo
	 * <br><i>the method parameter position</i>
	 */
	public int getIndex()
	{
		return index;
	}

	/**
	 * <p>
	 * Adiciona informa��es das anota��es do par�metro.
	 * <p><i>
	 * Adds the parameter annotations.
	 * </i>
	 * 
	 * @param annotationsInfo informa��es das anota��es do par�metro
	 * <br><i>the annotations of the parameter</i>
	 * @see org.esfinge.liveprog.reflect.AnnotationInfo
	 */
	public void addAnnotationInfo(AnnotationInfo... annotationsInfo)
	{
		LiveClassUtils.addToCollection(this.annotationsInfo, annotationsInfo);
	}

	/**
	 * <p>
	 * Obt�m informa��es das anota��es do par�metro.
	 * <p><i>
	 * Gets the parameter annotations.
	 * </i>
	 * 
	 * @return as informa��es das anota��es do par�metro
	 * <br><i>the annotations of the parameter</i>
	 * @see org.esfinge.liveprog.reflect.AnnotationInfo
	 */
	public Set<AnnotationInfo> getAnnotationsInfo()
	{
		return annotationsInfo;
	}
	
	/**
	 * <p>
	 * Verifica se o par�metro � um array.
	 * <p><i>
	 * Checks if this parameter is an array.
	 * </i>
	 * 
	 * @return <i>true</i> caso o par�metro seja um array, <i>false</i> caso contr�rio
	 * <br><i>true if this parameter is an array, false otherwise</i>
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
