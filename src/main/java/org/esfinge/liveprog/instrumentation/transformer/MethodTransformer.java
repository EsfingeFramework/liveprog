package org.esfinge.liveprog.instrumentation.transformer;

import org.esfinge.liveprog.instrumentation.InstrumentationService;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

/**
 * Classe para instrumentacao de metodos de classes dinamicas.
 */
class MethodTransformer extends MethodVisitor
{
	// nome original da classe dinamica
	private String className;
	
	// nome da nova versao da classe dinamica
	private String newClassName;


	/**
	 * Inicializa um novo instrumentador de metodos.
	 * 
	 * @param className nome original da classe dinamica
	 * @param newClassName o nome da nova versao da classe 
	 * @param visitor o escritor de metodos ASM
	 * @see org.esfinge.liveprog.instrumentation.ClassInfo
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
		/*
		System.out.println("VARIABLES REMAPPER>> Visiting variable: " + name);
		System.out.println("VARIABLES REMAPPER>> Desc: " + desc);
		System.out.println("VARIABLES REMAPPER>> Signature: " + signature);
		System.out.println("VARIABLES REMAPPER>> Label start: " + start);
		System.out.println("VARIABLES REMAPPER>> Label end: " + end);
		System.out.println("VARIABLES REMAPPER>> Index: " + index);
		System.out.println("--------------------------------------------");
		*/
		
		super.visitLocalVariable(name, this.replaceClassName(desc), this.replaceClassName(signature), start, end, index);
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc)
	{
		/*
		System.out.println("FIELD_INSN REMAPPER>> Visiting field insn: " + name);
		System.out.println("FIELD_INSN REMAPPER>> Opcode: " + opcode);
		System.out.println("FIELD_INSN REMAPPER>> Owner: " + owner);
		System.out.println("FIELD_INSN REMAPPER>> Name: " + name);
		System.out.println("FIELD_INSN REMAPPER>> Desc: " + desc);
		System.out.println("--------------------------------------------");
		*/
		
		super.visitFieldInsn(opcode, this.replaceClassName(owner), name, this.replaceClassName(desc));
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf)
	{
		/*
		System.out.println("METHOD_INSN REMAPPER>> Visiting method insn: " + name);
		System.out.println("METHOD_INSN REMAPPER>> Opcode: " + opcode);
		System.out.println("METHOD_INSN REMAPPER>> Owner: " + owner);
		System.out.println("METHOD_INSN REMAPPER>> Name: " + name);
		System.out.println("METHOD_INSN REMAPPER>> Desc: " + desc);
		System.out.println("METHOD_INSN REMAPPER>> Itf: " + itf);
		System.out.println("--------------------------------------------");
		*/
		
		super.visitMethodInsn(opcode, this.replaceClassName(owner), name, this.replaceClassName(desc), itf);
	}
	
	@Override
	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs)
	{
		/*
		System.out.println("INVOKEDYN_INSN REMAPPER>> Visiting invoke dynamic insn: " + name);
		System.out.println("INVOKEDYN_INSN REMAPPER>> Name: " + name);
		System.out.println("INVOKEDYN_INSN REMAPPER>> Desc: " + desc);
		System.out.println("INVOKEDYN_INSN REMAPPER>> BSM: " + bsm);
		System.out.println("INVOKEDYN_INSN REMAPPER>> BsmArgs: " + bsmArgs);
		System.out.println("--------------------------------------------");
		*/
		
		super.visitInvokeDynamicInsn(name, this.replaceClassName(desc), bsm, bsmArgs);
	}
	
	@Override
	public void visitTypeInsn(int opcode, String type)
	{
		/*
		System.out.println("TYPE_INSN REMAPPER>> Visiting type insn: " + type);
		System.out.println("TYPE_INSN REMAPPER>> Opcode: " + opcode);
		System.out.println("TYPE_INSN REMAPPER>> Type: " + type);
		System.out.println("--------------------------------------------");
		*/

		super.visitTypeInsn(opcode, this.replaceClassName(type));
	}
	
	@Override
	public void visitMultiANewArrayInsn(String desc, int dims)
	{
		/*
		System.out.println("MULTIARR_INSN REMAPPER>> Visiting multi new array insn: " + desc);
		System.out.println("MULTIARR_INSN REMAPPER>> Desc: " + desc);
		System.out.println("MULTIARR_INSN REMAPPER>> Dims: " + dims);
		System.out.println("--------------------------------------------");
		*/

		super.visitMultiANewArrayInsn(this.replaceClassName(desc), dims);
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible)
	{
		/*
		System.out.println("ANNOT REMAPPER>> Visiting annotation: " + desc);
		System.out.println("ANNOT REMAPPER>> Desc: " + desc);
		System.out.println("ANNOT REMAPPER>> Visible: " + visible);
		System.out.println("--------------------------------------------");
		 */
		
		AnnotationVisitor av = super.visitAnnotation(this.replaceClassName(desc), visible); 
		
		return ( av == null ? null : new AnnotationTransformer(this.className, this.newClassName, av) ); 
	}
	
	@Override
	public AnnotationVisitor visitParameterAnnotation(int index, String desc, boolean visible)
	{
		/*
		System.out.println("PARAM_ANNOT REMAPPER>> Visiting param annotation: " + desc);
		System.out.println("PARAM_ANNOT REMAPPER>> Index: " + index);
		System.out.println("PARAM_ANNOT REMAPPER>> Desc: " + desc);
		System.out.println("PARAM_ANNOT REMAPPER>> Visible: " + visible);
		System.out.println("--------------------------------------------");
		*/
		
		AnnotationVisitor av = super.visitAnnotation(this.replaceClassName(desc), visible); 
		
		return ( av == null ? null : new AnnotationTransformer(this.className, this.newClassName, av) ); 
	}
	
	@Override
	public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible)
	{
		/*
		System.out.println("ANNOT_INSN REMAPPER>> Visiting annotation insn: " + desc);
		System.out.println("ANNOT_INSN REMAPPER>> TypeRef: " + typeRef);
		System.out.println("ANNOT_INSN REMAPPER>> TypePath: " + typePath);
		System.out.println("ANNOT_INSN REMAPPER>> Desc: " + desc);
		System.out.println("ANNOT_INSN REMAPPER>> Visible: " + visible);
		System.out.println("--------------------------------------------");
		*/
		
		AnnotationVisitor av = super.visitInsnAnnotation(typeRef, typePath, this.replaceClassName(desc), visible);
		
		return ( av == null ? null : new AnnotationTransformer(this.className, this.newClassName, av) ); 
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
