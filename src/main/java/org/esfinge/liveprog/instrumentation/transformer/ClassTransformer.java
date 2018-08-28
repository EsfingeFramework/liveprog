package org.esfinge.liveprog.instrumentation.transformer;

import org.esfinge.liveprog.instrumentation.InstrumentationService;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Classe para instrumentacao de classes dinamicas.
 */
class ClassTransformer extends ClassVisitor
{
	// nome original da classe dinamica
	private String className;
	
	// nome da nova versao da classe dinamica
	private String newClassName;
	
	
	/**
	 * Inicializa um novo instrumentador de classe.
	 * 
	 * @param className nome original da classe dinamica
	 * @param newClassName o nome da nova versao da classe 
	 * @param writer o escritor de classes ASM
	 * @see org.esfinge.liveprog.instrumentation.ClassInfo
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
	 * Procura pelo nome da classe e substitui pelo nome da nova versao.
	 * 
	 * @param arg a string a ser verificada
	 * @return a string com o nome da classe substituida pela da nova versao,
	 * ou a propria string de entrada caso o nome da classe nao esteja presente
	 */
	private String replaceClassName(String arg)
	{
		if ( arg != null )
			return ( arg.replace(InstrumentationService.toInternalName(this.className), InstrumentationService.toInternalName(this.newClassName)) );
		
		return ( null );
	}
}
