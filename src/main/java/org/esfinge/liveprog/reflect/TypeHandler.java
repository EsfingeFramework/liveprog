package org.esfinge.liveprog.reflect;

import org.esfinge.liveprog.instrumentation.InstrumentationHelper;
import org.esfinge.liveprog.util.Utils;
import org.objectweb.asm.Type;

/**
 * <p>
 * Classe auxiliar para manipular informações de tipo.
 * <p><i>
 * Utilitary class for handling type information.
 * </i>
 * 
 * @see org.objectweb.asm.Type
 */
public class TypeHandler
{
	// objeto Type original
	private Type type;
	
	// o tipo do elemento (se for array)
	private String arrayType;
	

	/**
	 * <p>
	 * Constrói um novo manipulador para tipos.
	 * <p><i>
	 * Constructs a new type handler.
	 * </i>
	 * 
	 * @param descriptor descritor do tipo
	 * <br><i>the type descriptor</i>
	 */
	public TypeHandler(String descriptor)
	{
		this(Type.getType(descriptor) );
	}

	/**
	 * <p>
	 * Constrói um novo manipulador para tipos.
	 * <p><i>
	 * Constructs a new type handler.
	 * </i>
	 * 
	 * @param type objeto original do tipo Type
	 * <br><i>the original Type object</i>
	 * @see org.objectweb.asm.Type
	 */
	public TypeHandler(Type type)
	{
		this.type = type;
		this.arrayType = this.type.getClassName();
		
		// verifica se eh array
		if ( this.isArray() )
		{
			// obtem o nome do tipo do array
			this.arrayType = this.arrayType.substring(0, this.arrayType.length() - (2 * this.getArrayDimension()));
		} 
	}
	
	/**
	 * <p>
	 * Obtém o nome do tipo.
	 * Equivale ao método <i>Class.getName()</i>, conforme especificado na <i>Java Language Specification</i>.
	 * <p><i>
	 * Gets the type name.
	 * It corresponds to Class.getName(), as specified in the Java Language Specification. 
	 * </i>
	 * 
	 * @return o nome do tipo
	 * <br><i>the name of the type</i>
	 */
	public String getName()
	{
		if ( (this.type.getSort() == Type.ARRAY) || (this.type.getSort() == Type.OBJECT) )
			return ( InstrumentationHelper.toQualifiedName(this.type.getInternalName()) );
		
		return ( this.getDisplayName() );
	}
	
	/**
	 * <p>
	 * Obtém o nome informativo do tipo.
	 * Se for um array, por exemplo, o retorno será <i>tipo[]</i>.
	 * <p><i>
	 * Gets the type display name.
	 * If this is an array, for example, it will return type[].
	 * </i>
	 * 
	 * @return o nome informativo do tipo
	 */
	public String getDisplayName()
	{
		return ( this.type.getClassName() );
	}
	
	/**
	 * <p>
	 * Obtém o tipo do elemento base do array.
	 * <p><i>
	 * Gets the array element type.
	 * </i>
	 * 
	 * @return o tipo do elemento base do array
	 * <br><i>the element type of the array</i>
	 */
	public String getArrayType()
	{
		return ( this.arrayType );
	}
	
	/**
	 * <p>
	 * Obtém o descritor do tipo.
	 * <p><i>
	 * Gets the type descriptor.
	 * </i>
	 * 
	 * @return o descritor do tipo
	 * <br><i>the type descriptor</i>
	 */
	public String getDescriptor()
	{
		return ( this.type.getDescriptor() );
	}

	/**
	 * <p>
	 * Obtém o objeto Type original.
	 * <p><i>
	 * Gets the original Type object.
	 * </i>
	 * 
	 * @return o objeto Type original
	 * <br><i>the original Type object</i>
	 */
	public Type getType()
	{
		return ( this.type );
	}
	
	/**
	 * <p>
	 * Obtém a classe do tipo.
	 * <p><i>
	 * Gets the type class.
	 * </i>
	 * 
	 * @return a classe do tipo
	 * <br><i>the type class</i>
	 */
	public Class<?> getTypeClass() 
	{
		// verifica o tipo
		switch ( this.type.getSort() )
		{
			case Type.BOOLEAN:
				return ( boolean.class );
	
			case Type.BYTE:
					return ( byte.class );
				
			case Type.CHAR:
				return ( char.class );
				
			case Type.DOUBLE:
				return ( double.class );
				
			case Type.FLOAT:
				return ( float.class );
				
			case Type.INT:
				return ( int.class );
				
			case Type.LONG:
				return ( long.class );
				
			case Type.SHORT:
				return ( short.class );
				
			case Type.VOID:
				return ( void.class );
		}
		
		try
		{
			return ( Class.forName(this.getName()) );
		}
		catch (ClassNotFoundException e)
		{
			// log: erro
			Utils.logError("Tipo desconhecido: '" + this.getDisplayName() + "'");
			Utils.logException(e);
			
			return ( null );
		}
	}
	
	/**
	 * <p>
	 * Verifica se o tipo é um array.
	 * <p><i>
	 * Checks if this type is an array.
	 * </i>
	 * 
	 * @return <i>true</i> caso o tipo seja um array, <i>false</i> caso contrário
	 * <br><i>true if this type is an array, false otherwise</i>
	 */
	public boolean isArray()
	{
		return ( this.type.getSort() == Type.ARRAY );
	}
	
	/**
	 * <p>
	 * Obtém a dimensão do array.
	 * <p><i>
	 * Gets the array dimensions.
	 * </i>
	 * 
	 * @return a dimensão do array, ou 0 se não for um array
	 * <br><i>the array dimensions, or 0 if it is not an array</i>
	 */
	public int getArrayDimension()
	{
		if ( this.isArray() )
			return ( this.type.getDimensions() );
		
		return ( 0 );
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arrayType == null) ? 0 : arrayType.hashCode());
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
		TypeHandler other = (TypeHandler) obj;
		if (arrayType == null)
		{
			if (other.arrayType != null)
				return false;
		} else if (!arrayType.equals(other.arrayType))
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
		return ( this.getDisplayName() );
	}
}
