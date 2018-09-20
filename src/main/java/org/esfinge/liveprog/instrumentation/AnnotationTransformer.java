package org.esfinge.liveprog.instrumentation;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * <p>
 * Classe para instrumentação de anotações. 
 * <p><i>
 * Class for annotation instrumentation.
 * </i>
 */
class AnnotationTransformer extends AnnotationVisitor
{
	// nome original da classe dinamica
	private String className;
	
	// nome da nova versao da classe dinamica
	private String newClassName;
	
	
	/**
	 * <p>
	 * Constrói um novo manipulador de anotações.
	 * <p><i>
	 * Constructs a new transformer for annotation instrumentation.
	 * </i>
	 * 
	 * @param className nome original da classe
	 * <br><i>the original class name</i>
	 * @param newClassName nome da nova versão da classe
	 * <br><i>the new name of the class</i> 
	 * @param visitor objeto visitor ASM original
	 * <br><i>the original ASM visitor object</i>
	 * @see org.objectweb.asm.AnnotationVisitor
	 */
	AnnotationTransformer(String className, String newClassName, AnnotationVisitor visitor)
	{
		super(Opcodes.ASM6, visitor);
		
		this.className = className;
		this.newClassName = newClassName;
	}
	
	@Override
	public void visit(String name, Object value)
	{
		Object newValue = value;
		
		// verifica se eh uma classe
		if ( value instanceof Type )
			newValue = Type.getType(this.replaceClassName(((Type) value).getDescriptor()));

		super.visit(name, newValue);
	}

	@Override
	public AnnotationVisitor visitArray(String name)
	{
		AnnotationVisitor av = super.visitArray(name); 
		
		return ( av == null ? null : new AnnotationTransformer(this.className, this.newClassName, av) ); 
	}
	
	@Override
	public void visitEnum(String name, String desc, String value)
	{
		super.visitEnum(name, this.replaceClassName(desc), value);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String name, String desc)
	{
		AnnotationVisitor av = super.visitAnnotation(name, this.replaceClassName(desc)); 
		
		return ( av == null ? null : new AnnotationTransformer(this.className, this.newClassName, av) ); 
	}

	/**
	 * <p>
	 * Substitui o nome original pelo nome da nova versão da classe.
	 * <p><i>
	 * Replaces the original class name by the new name of the class.
	 * </i>
	 * 
	 * @param arg string a ser verificada
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
