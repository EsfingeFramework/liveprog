package org.esfinge.liveprog.instrumentation;

import java.util.HashSet;
import java.util.Set;

import org.esfinge.liveprog.util.Utils;


/**
 * Armazena informacoes sobre um campo/propriedade de uma classe.
 */
public class FieldInfo
{
	// flag de modificador de acesso do campo
	private int accessFlag;
	
	// nome do campo
	private String name;
	
	// tipo do campo
	private TypeWrapper type;
	
	// informacoes das anotacoes do campo
	private Set<AnnotationInfo> annotationsInfo;
	

	/**
	 * Construtor padrao.
	 */
	public FieldInfo()
	{
		this.annotationsInfo = new HashSet<AnnotationInfo>();
	}
	
	/**
	 * Atribui o nome do campo.
	 * 
	 * @param name o nome do campo
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Retorna o nome do campo.
	 * 
	 * @return o nome do campo
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Atribui o tipo do campo.
	 * 
	 * @param type o tipo do campo
	 * @see org.esfinge.liveprog.instrumentation.TypeWrapper
	 */
	public void setType(TypeWrapper type)
	{
		this.type = type;
	}
	
	/**
	 * Retorna o tipo do campo.
	 * 
	 * @return o tipo do campo
	 * @see org.esfinge.liveprog.instrumentation.TypeWrapper
	 */
	public TypeWrapper getType()
	{
		return type;
	}

	/**
	 * Atribui o flag modificador de acesso do campo.
	 * @param accessFlag o flag modificador de acesso do campo
	 * @see java.lang.reflect.Modifier 
	 */
	public void setAccessFlag(int accessFlag)
	{
		this.accessFlag = accessFlag;
	}
	
	/**
	 * Retorna o flag modificador de acesso do campo.
	 * 
	 * @return o flag modificador de acesso do campo
	 * @see java.lang.reflect.Modifier
	 */
	public int getAccessFlag()
	{
		return accessFlag;
	}
	
	/**
	 * Retorna o modo de acesso do campo.
	 * 
	 * @return o modo de acesso do campo 
	 * @see org.esfinge.liveprog.instrumentation.AccessModifier
	 */
	public AccessModifier getAccessPermission()
	{
		return ( AccessModifier.decode(this.accessFlag) );
	}
	
	/**
	 * Adiciona as informacoes das anotacoes do campo.
	 * 
	 * @param annotationsInfo informacoes das anotacoes do campo
	 * @see org.esfinge.liveprog.instrumentation.AnnotationInfo
	 */
	public void addAnnotationInfo(AnnotationInfo... annotationsInfo)
	{
		Utils.addToCollection(this.annotationsInfo, annotationsInfo);
	}

	/**
	 * Retorna as informacoes das anotacoes do campo.
	 * 
	 * @return informacoes das anotacoes do campo
	 * @see org.esfinge.liveprog.instrumentation.AnnotationInfo
	 */
	public Set<AnnotationInfo> getAnnotationsInfo()
	{
		return annotationsInfo;
	}

	/**
	 * Retorna se o campo eh um array.
	 * 
	 * @return <b>true</b> caso o campo seja do tipo array, 
	 * <b>false</b> caso contrario 
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
