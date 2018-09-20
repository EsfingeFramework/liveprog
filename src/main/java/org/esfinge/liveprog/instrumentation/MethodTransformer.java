package org.esfinge.liveprog.instrumentation;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

/**
 * <p>
 * Classe para instrumentação de métodos. 
 * <p><i>
 * Class for method instrumentation.
 * </i>
 */
class MethodTransformer extends MethodVisitor
{
	// nome original da classe dinamica
	private String className;
	
	// nome da nova versao da classe dinamica
	private String newClassName;


	/**
	 * <p>
	 * Constrói um novo manipulador de métodos.
	 * <p><i>
	 * Constructs a new transformer for method instrumentation.
	 * </i>
	 * 
	 * @param className nome original da classe
	 * <br><i>the original class name</i>
	 * @param newClassName nome da nova versão da classe
	 * <br><i>the new name of the class</i> 
	 * @param visitor objeto visitor ASM original
	 * <br><i>the original ASM visitor object</i>
	 * @see org.objectweb.asm.MethodVisitor
	 */
	MethodTransformer(String className, String newClassName, MethodVisitor visitor)
	{
		super(Opcodes.ASM6, visitor);
		
		this.className = className;
		this.newClassName = newClassName;
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
	{
		super.visitLocalVariable(name, this.replaceClassName(desc), this.replaceClassName(signature), start, end, index);
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc)
	{
		super.visitFieldInsn(opcode, this.replaceClassName(owner), name, this.replaceClassName(desc));
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
	{
		super.visitMethodInsn(opcode, this.replaceClassName(owner), name, this.replaceClassName(desc), itf);
	}
	
	@Override
	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs)
	{
		super.visitInvokeDynamicInsn(name, this.replaceClassName(desc), bsm, bsmArgs);
	}
	
	@Override
	public void visitTypeInsn(int opcode, String type)
	{
		super.visitTypeInsn(opcode, this.replaceClassName(type));
	}
	
	@Override
	public void visitMultiANewArrayInsn(String desc, int dims)
	{
		super.visitMultiANewArrayInsn(this.replaceClassName(desc), dims);
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible)
	{
		AnnotationVisitor av = super.visitAnnotation(this.replaceClassName(desc), visible); 
		
		return ( av == null ? null : new AnnotationTransformer(this.className, this.newClassName, av) ); 
	}
	
	@Override
	public AnnotationVisitor visitParameterAnnotation(int index, String desc, boolean visible)
	{
		AnnotationVisitor av = super.visitAnnotation(this.replaceClassName(desc), visible); 
		
		return ( av == null ? null : new AnnotationTransformer(this.className, this.newClassName, av) ); 
	}
	
	@Override
	public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible)
	{
		AnnotationVisitor av = super.visitInsnAnnotation(typeRef, typePath, this.replaceClassName(desc), visible);
		
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
