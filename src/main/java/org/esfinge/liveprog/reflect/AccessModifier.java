package org.esfinge.liveprog.reflect;

import java.lang.reflect.Modifier;

/**
 * <p>
 * Enum representando os tipos de modificadores de acesso Java.
 * <p><i>
 * Enum representing the types of Java access modifiers.
 * </i>
 */
public enum AccessModifier
{
	PUBLIC,
	PRIVATE,
	PROTECTED,
	DEFAULT;
	
	/**
	 * <p>
	 * Obtém o enum representando o modificador de acesso informado.
	 * <p><i>
	 * Gets the enum representing the specified access modifier.
	 * </i>
	 * 
	 * @param accessFlag inteiro representando o modificador de acesso
	 * <br><i>integer value representing the access modifier</i>
	 * @return o enum representando o modificador de acesso informado
	 * <br><i>the enum representing the specified access modifier</i>
	 * @see java.lang.reflect.Modifier
	 */
	public static AccessModifier decode(int accessFlag)
	{
		if ( Modifier.isPublic(accessFlag) )
			return AccessModifier.PUBLIC;
		
		if ( Modifier.isPrivate(accessFlag) )
			return AccessModifier.PRIVATE;
		
		if ( Modifier.isProtected(accessFlag) )
			return AccessModifier.PROTECTED;
		
		return AccessModifier.DEFAULT;
	}

	@Override
	public String toString()
	{
		return ( super.toString().toLowerCase() );
	}
}
