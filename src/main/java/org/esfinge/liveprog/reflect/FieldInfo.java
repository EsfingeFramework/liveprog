package org.esfinge.liveprog.reflect;

import java.util.HashSet;
import java.util.Set;

import org.esfinge.liveprog.util.Utils;

/**
 * <p>
 * Armazena informações sobre um campo/propriedade de uma classe.
 * <p><i>
 * Stores information about a field/property of a class.
 * </i>
 */
public class FieldInfo
{
	// flag de modificador de acesso do campo
	private int accessFlag;
	
	// nome do campo
	private String name;
	
	// tipo do campo
	private TypeHandler type;
	
	// informacoes das anotacoes do campo
	private Set<AnnotationInfo> annotationsInfo;
	

	/**
	 * <p>
	 * Construtor padrão.
	 * <p><i>
	 * Default constructor.
	 * </i>
	 */
	public FieldInfo()
	{
		this.annotationsInfo = new HashSet<AnnotationInfo>();
	}
	
	/**
	 * <p>
	 * Atribui o nome do campo.
	 * <p><i>
	 * Sets the field name.
	 * </i>
	 * 
	 * @param name nome do campo/propriedade
	 * <br><i>the name of the field</i>
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * <p>
	 * Obtém o nome do campo.
	 * <p><i>
	 * Gets the field name.
	 * </i>
	 * 
	 * @return o nome do campo/propriedade
	 * <br><i>the name of the field</i>
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * <p>
	 * Atribui o tipo do campo.
	 * <p><i>
	 * Sets the field type.
	 * </i>
	 * 
	 * @param type tipo do campo/propriedade
	 * <br><i>the type of the field</i>
	 * @see org.esfinge.liveprog.reflect.TypeHandler
	 */
	public void setType(TypeHandler type)
	{
		this.type = type;
	}
	
	/**
	 * <p>
	 * Obtém o tipo do campo.
	 * <p><i>
	 * Gets the field type.
	 * </i>
	 * 
	 * @return o tipo do campo/propriedade
	 * <br><i>the type of the field</i>
	 * @see org.esfinge.liveprog.reflect.TypeHandler
	 */
	public TypeHandler getType()
	{
		return type;
	}

	/**
	 * <p>
	 * Atribui o valor inteiro representando o modificador de acesso do campo. 
	 * <p><i>
	 * Sets the integer value representing the field access modifier.
	 * </i>
	 * 
	 * @param accessFlag valor inteiro representando o modificador de acesso do campo/propriedade
	 * <br><i>the integer value representing the access modifier of the field</i> 
	 * @see java.lang.reflect.Modifier
	 */
	public void setAccessFlag(int accessFlag)
	{
		this.accessFlag = accessFlag;
	}
	
	/**
	 * <p>
	 * Obtém o valor inteiro representando o modificador de acesso do campo.
	 * <p><i>
	 * Gets the integer value representing the field access modifier.
	 * </i>
	 * 
	 * @return o valor inteiro representando o modificador de acesso do campo/propriedade
	 * <br><i>the integer value representing the access modifier of the field</i> 
	 * @see java.lang.reflect.Modifier
	 */
	public int getAccessFlag()
	{
		return accessFlag;
	}
	
	/**
	 * <p>
	 * Obtém o enum representando o modificador de acesso do campo.
	 * <p><i>
	 * Gets the enum value representing the field access modifier.
	 * </i>
	 * 
	 * @return o enum representando o modificador de acesso do campo/propriedade
	 * <br><i>the enum value representing the access modifier of the field</i> 
	 * @see org.esfinge.liveprog.reflect.AccessModifier
	 */
	public AccessModifier getAccessPermission()
	{
		return ( AccessModifier.decode(this.accessFlag) );
	}
	
	/**
	 * <p>
	 * Adiciona informações das anotações do campo.
	 * <p><i>
	 * Adds the field annotations.
	 * </i>
	 * 
	 * @param annotationsInfo informações das anotações do campo/propriedade
	 * <br><i>the annotations of the field</i>
	 * @see org.esfinge.liveprog.reflect.AnnotationInfo
	 */
	public void addAnnotationInfo(AnnotationInfo... annotationsInfo)
	{
		Utils.addToCollection(this.annotationsInfo, annotationsInfo);
	}

	/**
	 * <p>
	 * Obtém informações das anotações do campo.
	 * <p><i>
	 * Gets the field annotations.
	 * </i>
	 * 
	 * @return as informações das anotações do campo/propriedade
	 * <br><i>the annotations of the field</i>
	 * @see org.esfinge.liveprog.reflect.AnnotationInfo
	 */
	public Set<AnnotationInfo> getAnnotationsInfo()
	{
		return annotationsInfo;
	}

	/**
	 * <p>
	 * Verifica se o campo é um array.
	 * <p><i>
	 * Checks if this field is an array.
	 * </i>
	 * 
	 * @return <i>true</i> caso o campo/propriedade seja um array, <i>false</i> caso contrário
	 * <br><i>true if this field is an array, false otherwise</i>
	 */
	public boolean isArray()
	{
		return ( this.type.isArray() );
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + accessFlag;
		result = prime * result + ((annotationsInfo == null) ? 0 : annotationsInfo.hashCode());
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
		FieldInfo other = (FieldInfo) obj;
		if (accessFlag != other.accessFlag)
			return false;
		if (annotationsInfo == null)
		{
			if (other.annotationsInfo != null)
				return false;
		}
		else if (!annotationsInfo.equals(other.annotationsInfo))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (type == null)
		{
			if (other.type != null)
				return false;
		}
		else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return ( String.format("%s %s %s", this.getAccessPermission(), this.type, this.name) );
	}
}
