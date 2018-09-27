package org.esfinge.liveprog.reflect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.esfinge.liveprog.util.LiveClassUtils;

/**
 * <p>
 * Armazena informações sobre uma anotação.
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
	 * Construtor padrão.
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
	 * Atribui o nome qualificado da anotação.
	 * <p><i>
	 * Sets the annotation fully qualified name.
	 * </i>
	 * 
	 * @param name nome qualificado da anotação
	 * <br><i>the fully qualified name of the annotation</i>
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * <p>
	 * Obtém o nome qualificado da anotação.
	 * <p><i>
	 * Gets the annotation fully qualified name.
	 * </i>
	 * 
	 * @return o nome qualificado da anotação
	 * <br><i>the fully qualified name of the annotation</i>
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * <p>
	 * Obtém o nome simples da anotação.
	 * <p><i>
	 * Gets the annotation simple name.
	 * </i>
	 * 
	 * @return o nome simples da anotação
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
	 * Adiciona informações dos atributos da anotação.
	 * <p><i>
	 * Adds the annotation attributes.
	 * </i>
	 * 
	 * @param attributesInfo informações dos atributos da anotação
	 * <br><i>the attributes of the annotation</i>
	 */
	public void addAttributeInfo(AttributeInfo... attributesInfo)
	{
		LiveClassUtils.addToCollection(this.attributesInfo, attributesInfo);
	}
	
	/**
	 * <p>
	 * Obtém informações dos atributos da anotação.
	 * <p><i>
	 * Gets the annotation attributes.
	 * </i>
	 * 
	 * @return as informações dos atributos da anotação
	 * <br><i>the attributes of the annotation</i>
	 */
	public Set<AttributeInfo> getAttributesInfo()
	{
		return attributesInfo;
	}
	
	/**
	 * <p>
	 * Adiciona informações das anotações da classe.
	 * <p><i>
	 * Adds the class annotations.
	 * </i>
	 * 
	 * @param annotationsInfo informações das anotações da classe
	 * <br><i>the annotations of the class</i>
	 * @see org.esfinge.liveprog.reflect.AnnotationInfo
	 */
	public void addAnnotationInfo(AnnotationInfo... annotationsInfo)
	{
		LiveClassUtils.addToCollection(this.annotationsInfo, annotationsInfo);
	}

	/**
	 * <p>
	 * Obtém informações das anotações da anotação.
	 * <p><i>
	 * Gets the annotation annotations.
	 * </i>
	 * 
	 * @return as informações das anotações da anotação
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
	 * Armazena informações sobre um atributo de uma anotação.
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
		 * Construtor padrão.
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
		 * Obtém o nome do atributo.
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
		 * Obtém os valores do atributo.
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
		 * Obtém o tipo do atributo.
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
		 * Verifica se o atributo é um array.
		 * <p><i>
		 * Checks if this attribute is an array.
		 * </i>
		 * 
		 * @return <i>true</i> caso o atributo seja um array, <i>false</i> caso contrário
		 * <br><i>true if this attribute is an array, false otherwise</i>
		 */
		public boolean isArray()
		{
			return ( this.type.isArray() );
		}
	}
}
