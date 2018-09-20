package org.esfinge.liveprog.instrumentation;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

/**
 * <p>
 * Classe para instrumentação de campos. 
 * </p>
 * <p><i>
 * Class for field instrumentation.
 * </i></p>
 */
class FieldTransformer extends FieldVisitor
{
	// nome original da classe dinamica
	private String className;
	
	// nome da nova versao da classe dinamica
	private String newClassName;
	
	
	/**
	 * <p>
	 * Constrói um novo manipulador de campos.
	 * </p>
	 * <p><i>
	 * Constructs a new transformer for field instrumentation.
	 * </i></p>
	 * 
	 * @param className - nome original da classe
	 * <br><i>the original class name</i>
	 * @param newClassName - nome da nova versão da classe
	 * <br><i>the new name of the class</i> 
	 * @param visitor - objeto visitor ASM original
	 * <br><i>the original ASM visitor object</i>
	 * @see org.objectweb.asm.FieldVisitor
	 */
	FieldTransformer(String className, String newClassName, FieldVisitor visitor)
	{
		super(Opcodes.ASM6, visitor);
		
		this.className = className;
		this.newClassName = newClassName;
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible)
	{
		AnnotationVisitor av = super.visitAnnotation(this.replaceClassName(desc), visible); 
		
		return ( av == null ? null : new AnnotationTransformer(this.className, this.newClassName, av) ); 
	}

	/**
	 * <p>
	 * Substitui o nome original pelo nome da nova versão da classe.
	 * </p>
	 * <p><i>
	 * Replaces the original class name by the new name of the class.
	 * </i></p>
	 * 
	 * @param arg - string a ser verificada
	 * <br><i>the string to be verified</i>
	 * @return a string com os nomes da classe substituídos
	 * <br><i>the string with the class names replaced</i>
	 */
	private String replaceClassName(String arg)
	{
		if ( arg != null )
			return ( arg.replace(InstrumentationHelper.toInternalName(this.className), InstrumentationHelper.toInternalName(this.newClassName)) );
		
		return ( null );
	}
}
