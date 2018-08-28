package org.esfinge.liveprog.instrumentation.inspector;

import java.lang.reflect.Array;

import org.esfinge.liveprog.instrumentation.AnnotationInfo;
import org.esfinge.liveprog.instrumentation.TypeWrapper;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Classe utilitaria que facilita a recuperacao de informacoes sobre uma anotacao.
 */
class AnnotationInspector extends AnnotationVisitor
{
	// informacoes sobre a anotacao
	private AnnotationInfo annotationInfo;

	
	/**
	 * Inicializa um novo analisador de anotacoes.
	 * 
	 * @param annotationInfo onde as informacoes da anotacao serao armazenadas
	 * @see org.esfinge.liveprog.instrumentation.AnnotationInfo
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
		TypeWrapper type;

		// classe ou array (de classe / anotacoes)
		if ( value instanceof Type )
		{
			// eh uma classe
			type = new TypeWrapper((Type) value);
			attribute.addValue(type.getTypeName());
			attribute.setType(type);
		}
		else
		{
			// String, primitivos ou array de primitivos
			type = new TypeWrapper(Type.getObjectType(value.getClass().getName()));
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
		attribute.setType(new TypeWrapper(desc));
		attribute.addValue(value);
		
		// adiciona o atributo
		this.annotationInfo.addAttributeInfo(attribute);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String name, String desc)
	{
		AnnotationInfo.AttributeInfo attribute = new AnnotationInfo.AttributeInfo();
		attribute.setName(name);
		TypeWrapper type = new TypeWrapper(desc); 
		attribute.setType(type); 
		
		// cria uma nova informacao de anotacao
		AnnotationInfo annot = new AnnotationInfo();
		annot.setName(type.getTypeName());
		
		// adiciona a anotacao no atributo
		attribute.addValue(annot);
		
		// adiciona o atributo
		this.annotationInfo.addAttributeInfo(attribute);		
		
		// coleta as demais informacoes da anotacao
		return ( new AnnotationInspector(annot) );
	}
}
