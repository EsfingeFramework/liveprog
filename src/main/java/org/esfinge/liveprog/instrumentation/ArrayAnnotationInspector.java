package org.esfinge.liveprog.instrumentation;

import org.esfinge.liveprog.reflect.AnnotationInfo;
import org.esfinge.liveprog.reflect.TypeHandler;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * <p>
 * Extrai informações sobre um atributo do tipo array de uma anotação. 
 * <p><i>
 * Collects information about an array attribute of an annotation.
 * </i>
 */
class ArrayAnnotationInspector extends AnnotationVisitor
{
	// informacoes sobre o atributo da anotacao do tipo array
	private AnnotationInfo.AttributeInfo attribute;

	
	/**
	 * <p>
	 * Constrói um novo inspetor de atributos de anotações do tipo array.
	 * <p><i>
	 * Constructs a new annotation array attribute inspector.
	 * </i>
	 * 
	 * @param attribute objeto onde as informações extraídas serão armazenadas
	 * <br><i>the object to store the collected information</i>
	 * @see org.esfinge.liveprog.reflect.AnnotationInfo.AttributeInfo
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
			attribute.setType(new TypeHandler("[Ljava/lang/Class;")); 
			this.attribute.addValue(((Type) value).getClassName());
		}
		else
		{
			// array de String
			attribute.setType(new TypeHandler("[Ljava/lang/String;"));
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
		TypeHandler type = new TypeHandler("[" + desc); 
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
		TypeHandler type = new TypeHandler("[" + desc); 
		attribute.setType(type); 
		
		// cria uma nova informacao de anotacao
		AnnotationInfo annot = new AnnotationInfo();
		annot.setName(type.getDisplayName());
		
		// adiciona a anotacao aninhada
		this.attribute.addValue(annot);
		
		// coleta as demais informacoes da anotacao
		return ( new AnnotationInspector(annot) );
	}
}
