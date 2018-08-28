package org.esfinge.liveprog.instrumentation;

import org.objectweb.asm.Type;

/**
 * Wrapper para facilitar obter as informacoes de tipo.
 * 
 * @see org.objectweb.asm.Type
 */
public class TypeWrapper
{
	// objeto original Type
	private Type type;
	
	// o tipo basico (se for array)
	private String arrayType;
	

	/**
	 * Cria um novo wrapper para tipos.
	 * 
	 * @param descriptor o descritor do tipo
	 * @see org.objectweb.asm.Type
	 */
	public TypeWrapper(String descriptor)
	{
		this(Type.getType(descriptor) );
	}

	/**
	 * Cria um novo wrapper para tipos.
	 * 
	 * @param type o objeto original Type
	 * @see org.objectweb.asm.Type
	 */
	public TypeWrapper(Type type)
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
	 * Retorna o nome do tipo.
	 * 
	 * Corresponde ao metodo Class.getName(), conforme especificado na Java Language Specification.
	 * 
	 * @return o nome do tipo
	 */
	public String getName()
	{
		if ( (this.type.getSort() == Type.ARRAY) || (this.type.getSort() == Type.OBJECT) )
			return ( InstrumentationService.toQualifiedName(this.type.getInternalName()) );
		
		return ( this.getTypeName() );
	}
	
	/**
	 * Retorna um nome informativo do tipo.
	 * 
	 * Por exemplo, se for um array, retorna: tipo[].
	 * 
	 * @return o nome informativo do tipo
	 */
	public String getTypeName()
	{
		return ( this.type.getClassName() );
	}
	
	/**
	 * Retorna o nome do tipo base do array.
	 * 
	 * @return o nome do tipo base do array
	 */
	public String getArrayType()
	{
		return ( this.arrayType );
	}
	
	/**
	 * Retorna o descritor do tipo.
	 * 
	 * @return o descritor do tipo
	 */
	public String getDescriptor()
	{
		return ( this.type.getDescriptor() );
	}

	/**
	 * Retorna o objeto original do tipo Type.
	 * 
	 * @return o objeto do tipo Type
	 * @see org.objectweb.asm.Type
	 */
	public Type getType()
	{
		return ( this.type );
	}
	
	/**
	 * Retorna o objeto Class correspondente ao tipo.
	 * 
	 * @return o objeto Class do tipo
	 * @throws ClassNotFoundException caso nao encontre a classe correspondente ao tipo
	 */
	public Class<?> getTypeClass() throws ClassNotFoundException 
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
		
		return ( Class.forName(this.getName()) );
	}
	
	/**
	 * Retorna se o tipo eh um array.
	 * 
	 * @return <b>true</b> caso seja do tipo array, 
	 * <b>false</b> caso contrario 
	 */

	public boolean isArray()
	{
		return ( this.type.getSort() == Type.ARRAY );
	}
	
	/**
	 * Retorna a dimensao do array.
	 * 
	 * @return a dimensao do array, ou 0 se nao for do tipo array
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
		TypeWrapper other = (TypeWrapper) obj;
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
		return ( this.getTypeName() );
	}
}
