package org.esfinge.liveprog.instrumentation;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * <p>
 * Classe para instrumentação de classes. 
 * </p>
 * <p><i>
 * Class for class instrumentation.
 * </i></p>
 */
class ClassTransformer extends ClassVisitor
{
	// nome original da classe dinamica
	private String className;
	
	// nome da nova versao da classe dinamica
	private String newClassName;
	
	
	/**
	 * <p>
	 * Constrói um novo manipulador de classes.
	 * </p>
	 * <p><i>
	 * Constructs a new transformer for class instrumentation.
	 * </i></p>
	 * 
	 * @param className - nome original da classe
	 * <br><i>the original class name</i>
	 * @param newClassName - nome da nova versão da classe
	 * <br><i>the new name of the class</i> 
	 * @param writer - objeto escritor de classes ASM
	 * <br><i>the ASM classwriter object</i>
	 * @see org.objectweb.asm.ClassWriter
	 */
	ClassTransformer(String className, String newClassName, ClassWriter writer)
	{
		super(Opcodes.ASM6, writer);
		
		this.className = className;
		this.newClassName = newClassName;
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
	{
		super.visit(version, access, this.replaceClassName(name), this.replaceClassName(signature), superName, interfaces);
	}

	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access)
	{
		super.visitInnerClass(this.replaceClassName(name), this.replaceClassName(outerName), innerName, access);
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible)
	{
		AnnotationVisitor av = super.visitAnnotation(this.replaceClassName(desc), visible); 
		
		return ( av == null ? null : new AnnotationTransformer(this.className, this.newClassName, av) ); 
	}
	
	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
	{
		FieldVisitor fv = super.visitField(access, name, this.replaceClassName(desc), this.replaceClassName(signature), value); 

		return ( fv == null ? null : new FieldTransformer(this.className, this.newClassName, fv) );
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
	{
		MethodVisitor mv = super.visitMethod(access, name, this.replaceClassName(desc), this.replaceClassName(signature), exceptions);

		return ( mv == null ? null : new MethodTransformer(this.className, this.newClassName, mv) );
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
