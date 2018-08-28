package org.esfinge.liveprog.instrumentation.inspector;

import org.esfinge.liveprog.instrumentation.AnnotationInfo;
import org.esfinge.liveprog.instrumentation.TypeWrapper;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Classe utilitaria que facilita a recuperacao de informacoes sobre um atributo da anotacao do tipo array.
 */
class ArrayAnnotationInspector extends AnnotationVisitor
{
	// informacoes sobre o atributo da anotacao do tipo array
	private AnnotationInfo.AttributeInfo attribute;

	
	/**
	 * Inicializa um novo analisador de atributos anotacoes do tipo array.
	 * 
	 * @param attribute onde as informacoes do atributo da anotacao serao armazenadas
	 * @see org.esfinge.liveprog.instrumentation.AnnotationInfo.AttributeInfo
	 */
	ArrayAnnotationInspector(AnnotationInfo.AttributeInfo attribute)
	{
		super(Opcodes.ASM6);
		this.attribute = attribute;
	}

	@Override
	public void visit(String name, Object value)
	{
		// vindo do visitArray, o nome eh nulo..
		
		if ( value instanceof Type )
		{
			//array de classes
			attribute.setType(new TypeWrapper("[Ljava/lang/Class;")); 
			this.attribute.addValue(((Type) value).getClassName());
		}
		else
		{
			// array de String
			attribute.setType(new TypeWrapper("[Ljava/lang/String;"));
			this.attribute.addValue(value);
		}
	}

	@Override
	public void visitEnum(String name, String desc, String value)
	{
		// vindo do visitArray, o nome eh nulo..
		
		// eh um array de enums
		
		// desc eh o tipo do enum
		
		// array de enums
		TypeWrapper type = new TypeWrapper("[" + desc); 
		attribute.setType(type); 
		
		// adiciona o valor do enum
		this.attribute.addValue(value);
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String name, String desc)
	{
		// vindo do visitArray, o nome eh nulo..
		
		// eh um array de anotacoes (anotacoes aninhadas)
		
		// desc eh o tipo da anotacao
		
		// array de anotacoes
		TypeWrapper type = new TypeWrapper("[" + desc); 
		attribute.setType(type); 
		
		// cria uma nova informacao de anotacao
		AnnotationInfo annot = new AnnotationInfo();
		annot.setName(type.getTypeName());
		
		// adiciona a anotacao aninhada
		this.attribute.addValue(annot);
		
		// coleta as demais informacoes da anotacao
		return ( new AnnotationInspector(annot) );
	}
}
