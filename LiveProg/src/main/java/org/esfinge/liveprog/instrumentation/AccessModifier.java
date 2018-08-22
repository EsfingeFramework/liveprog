package org.esfinge.liveprog.instrumentation;

import java.lang.reflect.Modifier;

public enum AccessModifier
{
	PUBLIC,
	PRIVATE,
	PROTECTED,
	DEFAULT;
	
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
