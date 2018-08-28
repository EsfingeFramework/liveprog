package org.esfinge.liveprog.instrumentation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.esfinge.liveprog.util.Utils;

/**
 * Armazena informacoes sobre uma anotacao.
 */
public class AnnotationInfo
{
	// nome qualificado da anotacao
	private String name;
	
	// informacoes dos atributos da anotacao
	private Set<AttributeInfo> attributesInfo;
	
	
	/**
	 * Construtor padrao.
	 */
	public AnnotationInfo()
	{
		this.attributesInfo = new HashSet<AttributeInfo>();
	}
	
	/**
	 * Atribui o nome qualificado da anotacao.
	 * 
	 * @param name o nome completo da anotacao
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Retorna o nome qualificado da anotacao.
	 * 
	 * @return o nome completo da anotacao
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Retorna o nome da anotacao.
	 * 
	 * @return o nome simples da anotacao
	 */
	public String getSimpleName()
	{
		if ( this.name != null )
			return ( this.name.substring(this.name.lastIndexOf('.') + 1) );

		return ( null );
	}
	
	/**
	 * Adiciona as informacoes dos atributos da anotacao.
	 * 
	 * @param attributesInfo informacoes dos atributos da anotacao
	 */
	public void addAttributeInfo(AttributeInfo... attributesInfo)
	{
		Utils.addToCollection(this.attributesInfo, attributesInfo);
	}
	
	/**
	 * Retorna as informacoes dos atributos da anotacao.
	 * 
	 * @return informacoes dos atributos da anotacao
	 */
	public Set<AttributeInfo> getAttributesInfo()
	{
		return attributesInfo;
	}
	
	@Override
	public String toString()
	{
		return ( String.format("@%s", this.getSimpleName()) );
	}
	
	
	/**
	 * Armazena informacoes sobre um atributo de uma anotacao.
	 */
	public static class AttributeInfo
	{
		// nome do atributo
		private String name;
		
		// valores do atributo
		private List<Object> values;
		
		// tipo do atributo
		private TypeWrapper type;
		
		
		/**
		 * Construtor padrao.
		 */
		public AttributeInfo()
		{
			this.values = new ArrayList<Object>();
		}

		/**
		 * Atribui o nome do atributo.
		 * 
		 * @param name o nome do atributo
		 */
		public void setName(String name)
		{
			this.name = name;
		}
		
		/**
		 * Retorna o nome do atributo.
		 * 
		 * @return o nome do atributo
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * Adiciona os valores do atributo.
		 * 
		 * @param values os valores do atributo
		 */
		public void addValue(Object... values)
		{
			Utils.addToCollection(this.values, values);
		}

		/**
		 * Retorna os valores do atributo.
		 * 
		 * @return os valores do atributo
		 */
		public List<Object> getValues()
		{
			return values;
		}

		/**
		 * Atribui o tipo do atributo.
		 * 
		 * @param type o tipo do atributo
		 * @see org.esfinge.liveprog.instrumentation.TypeWrapper
		 */
		public void setType(TypeWrapper type)
		{
			this.type = type;
		}

		/**
		 * Retorna o tipo do atributo.
		 * 
		 * @return o tipo do atributo
		 * @see org.esfinge.liveprog.instrumentation.TypeWrapper
		 */
		public TypeWrapper getType()
		{
			return type;
		}
		
		/**
		 * Retorna se o atributo eh um array.
		 * 
		 * @return <b>true</b> caso o atributo seja do tipo array, 
		 * <b>false</b> caso contrario 
		 */
		public boolean isArray()
		{
			return ( this.type.isArray() );
		}
	}
}
