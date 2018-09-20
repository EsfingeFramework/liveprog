package org.esfinge.liveprog.instrumentation;

import java.lang.reflect.Array;

import org.esfinge.liveprog.reflect.AnnotationInfo;
import org.esfinge.liveprog.reflect.TypeHandler;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * <p>
 * Extrai informações sobre uma anotação. 
 * </p>
 * <p><i>
 * Collects information about an annotation.
 * </i></p>
 */
class AnnotationInspector extends AnnotationVisitor
{
	// informacoes sobre a anotacao
	private AnnotationInfo annotationInfo;

	
	/**
	 * <p>
	 * Constrói um novo inspetor de anotações.
	 * </p>
	 * <p><i>
	 * Constructs a new annotation inspector.
	 * </i></p>
	 * 
	 * @param annotationInfo - objeto onde as informações extraídas serão armazenadas
	 * <br><i>the object to store the collected information</i>
	 * @see org.esfinge.liveprog.reflect.AnnotationInfo
	 */
	AnnotationInspector(AnnotationInfo annotationInfo)
	{
		super(Opcodes.ASM6);
		this.annotationInfo = annotationInfo;
	}

	@Override
	public void visit(String name, Object value)
	{
		AnnotationInfo.AttributeInfo attribute = new AnnotationInfo.AttributeInfo();
		attribute.setName(name);
		TypeHandler type;

		// classe ou array (de classe / anotacoes)
		if ( value instanceof Type )
		{
			// eh uma classe
			type = new TypeHandler((Type) value);
			attribute.addValue(type.getDisplayName());
			attribute.setType(type);
		}
		else
		{
			// String, primitivos ou array de primitivos
			type = new TypeHandler(Type.getObjectType(value.getClass().getName()));
			attribute.setType(type);

			if ( type.isArray() )
				for ( int i = 0; i < Array.getLength(value); i++ )
					attribute.addValue(Array.get(value, i));
			else
				attribute.addValue(value);
		}
		
		// adiciona o atributo
		this.annotationInfo.addAttributeInfo(attribute);
	}

	@Override
	public AnnotationVisitor visitArray(String name)
	{
		AnnotationInfo.AttributeInfo attribute = new AnnotationInfo.AttributeInfo();
		attribute.setName(name);
		
		// adiciona o atributo
		this.annotationInfo.addAttributeInfo(attribute);
		
		// coleta os valores do array
		return ( new ArrayAnnotationInspector(attribute) );
	}
	
	@Override
	public void visitEnum(String name, String desc, String value)
	{
		AnnotationInfo.AttributeInfo attribute = new AnnotationInfo.AttributeInfo();
		attribute.setName(name);
		attribute.setType(new TypeHandler(desc));
		attribute.addValue(value);
		
		// adiciona o atributo
		this.annotationInfo.addAttributeInfo(attribute);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String name, String desc)
	{
		AnnotationInfo.AttributeInfo attribute = new AnnotationInfo.AttributeInfo();
		attribute.setName(name);
		TypeHandler type = new TypeHandler(desc); 
		attribute.setType(type); 
		
		// cria uma nova informacao de anotacao
		AnnotationInfo annot = new AnnotationInfo();
		annot.setName(type.getDisplayName());
		
		// adiciona a anotacao no atributo
		attribute.addValue(annot);
		
		// adiciona o atributo
		this.annotationInfo.addAttributeInfo(attribute);		
		
		// coleta as demais informacoes da anotacao
		return ( new AnnotationInspector(annot) );
	}
}
