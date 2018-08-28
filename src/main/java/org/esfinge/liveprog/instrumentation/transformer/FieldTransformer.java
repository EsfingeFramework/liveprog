package org.esfinge.liveprog.instrumentation.transformer;

import org.esfinge.liveprog.instrumentation.InstrumentationService;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Classe para instrumentacao de campos/propriedades de classes dinamicas.
 */
class FieldTransformer extends FieldVisitor
{
	// nome original da classe dinamica
	private String className;
	
	// nome da nova versao da classe dinamica
	private String newClassName;
	
	
	/**
	 * Inicializa um novo instrumentador de campos.
	 * 
	 * @param className nome original da classe dinamica
	 * @param newClassName o nome da nova versao da classe 
	 * @param visitor o escritor de campos ASM
	 * @see org.esfinge.liveprog.instrumentation.ClassInfo
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
