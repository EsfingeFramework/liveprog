package org.esfinge.liveprog.reflect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.esfinge.liveprog.util.LiveClassUtils;

/**
 * <p>
 * Armazena informa��es sobre uma anota��o.
 * <p><i>
 * Stores information about an annotation.
 * </i>
 */
public class AnnotationInfo
{
	// nome qualificado da anotacao
	private String name;
	
	// informacoes dos atributos da anotacao
	private Set<AttributeInfo> attributesInfo;
	
	// informacoes das anotacoes da anotacao
	private Set<AnnotationInfo> annotationsInfo;
	
	
	/**
	 * <p>
	 * Construtor padr�o.
	 * <p><i>
	 * Default constructor.
	 * </i>
	 */
	public AnnotationInfo()
	{
		this.attributesInfo = new HashSet<AttributeInfo>();
		this.annotationsInfo = new HashSet<AnnotationInfo>();
	}
	
	/**
	 * <p>
	 * Atribui o nome qualificado da anota��o.
	 * <p><i>
	 * Sets the annotation fully qualified name.
	 * </i>
	 * 
	 * @param name nome qualificado da anota��o
	 * <br><i>the fully qualified name of the annotation</i>
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * <p>
	 * Obt�m o nome qualificado da anota��o.
	 * <p><i>
	 * Gets the annotation fully qualified name.
	 * </i>
	 * 
	 * @return o nome qualificado da anota��o
	 * <br><i>the fully qualified name of the annotation</i>
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * <p>
	 * Obt�m o nome simples da anota��o.
	 * <p><i>
	 * Gets the annotation simple name.
	 * </i>
	 * 
	 * @return o nome simples da anota��o
	 * <br><i>the simple name of the annotation</i>
	 */
	public String getSimpleName()
	{
		if ( this.name != null )
			return ( this.name.substring(this.name.lastIndexOf('.') + 1) );

		return ( null );
	}
	
	/**
	 * <p>
	 * Adiciona informa��es dos atributos da anota��o.
	 * <p><i>
	 * Adds the annotation attributes.
	 * </i>
	 * 
	 * @param attributesInfo informa��es dos atributos da anota��o
	 * <br><i>the attributes of the annotation</i>
	 */
	public void addAttributeInfo(AttributeInfo... attributesInfo)
	{
		LiveClassUtils.addToCollection(this.attributesInfo, attributesInfo);
	}
	
	/**
	 * <p>
	 * Obt�m informa��es dos atributos da anota��o.
	 * <p><i>
	 * Gets the annotation attributes.
	 * </i>
	 * 
	 * @return as informa��es dos atributos da anota��o
	 * <br><i>the attributes of the annotation</i>
	 */
	public Set<AttributeInfo> getAttributesInfo()
	{
		return attributesInfo;
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
		LiveClassUtils.addToCollection(this.annotationsInfo, annotationsInfo);
	}

	/**
	 * <p>
	 * Obt�m informa��es das anota��es da anota��o.
	 * <p><i>
	 * Gets the annotation annotations.
	 * </i>
	 * 
	 * @return as informa��es das anota��es da anota��o
	 * <br><i>the annotations of the annotation</i>
	 */
	public Set<AnnotationInfo> getAnnotationsInfo()
	{
		return annotationsInfo;
	}
	
	@Override
	public String toString()
	{
		return ( String.format("@%s", this.getSimpleName()) );
	}
	
	
	/**
	 * <p>
	 * Armazena informa��es sobre um atributo de uma anota��o.
	 * <p><i>
	 * Stores information about an attribute of an annotation.
	 * </i>
	 */
	public static class AttributeInfo
	{
		// nome do atributo
		private String name;
		
		// valores do atributo
		private List<Object> values;
		
		// tipo do atributo
		private TypeHandler type;
		
		
		/**
		 * <p>
		 * Construtor padr�o.
		 * <p><i>
		 * Default constructor.
		 * </i>
		 */
		public AttributeInfo()
		{
			this.values = new ArrayList<Object>();
		}

		/**
		 * <p>
		 * Atribui o nome do atributo.
		 * <p><i>
		 * Sets the attribute name.
		 * </i>
		 * 
		 * @param name nome do atributo
		 * <br><i>the name of the attribute</i>
		 */
		public void setName(String name)
		{
			this.name = name;
		}
		
		/**
		 * <p>
		 * Obt�m o nome do atributo.
		 * <p><i>
		 * Gets the attribute name.
		 * </i>
		 * 
		 * @return o nome do atributo
		 * <br><i>the name of the attribute</i>
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * <p>
		 * Adiciona os valores do atributo.
		 * <p><i>
		 * Adds the attribute values.
		 * </i>
		 * 
		 * @param values valores do atributo
		 * <br><i>the values of the attribute</i>
		 */
		public void addValue(Object... values)
		{
			LiveClassUtils.addToCollection(this.values, values);
		}

		/**
		 * <p>
		 * Obt�m os valores do atributo.
		 * <p><i>
		 * Gets the attribute values.
		 * </i>
		 * 
		 * @return os valores do atributo
		 * <br><i>the values of the attribute</i>
		 */
		public List<Object> getValues()
		{
			return values;
		}

		/**
		 * <p>
		 * Atribui o tipo do atributo.
		 * <p><i>
		 * Sets the attribute type.
		 * </i>
		 * 
		 * @param type tipo do atributo
		 * <br><i>the type of the attribute</i>
		 * @see org.esfinge.liveprog.reflect.TypeHandler
		 */
		public void setType(TypeHandler type)
		{
			this.type = type;
		}

		/**
		 * <p>
		 * Obt�m o tipo do atributo.
		 * <p><i>
		 * Gets the attribute type.
		 * </i>
		 * 
		 * @return o tipo do atributo
		 * <br><i>the type of the attribute</i>
		 * @see org.esfinge.liveprog.reflect.TypeHandler
		 */
		public TypeHandler getType()
		{
			return type;
		}
		
		/**
		 * <p>
		 * Verifica se o atributo � um array.
		 * <p><i>
		 * Checks if this attribute is an array.
		 * </i>
		 * 
		 * @return <i>true</i> caso o atributo seja um array, <i>false</i> caso contr�rio
		 * <br><i>true if this attribute is an array, false otherwise</i>
		 */
		public boolean isArray()
		{
			return ( this.type.isArray() );
		}
	}
}
