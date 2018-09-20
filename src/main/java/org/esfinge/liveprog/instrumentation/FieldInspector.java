package org.esfinge.liveprog.instrumentation;

import org.esfinge.liveprog.reflect.AnnotationInfo;
import org.esfinge.liveprog.reflect.FieldInfo;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

/**
 * <p>
 * Extrai informações sobre um campo. 
 * </p>
 * <p><i>
 * Collects information about a field.
 * </i></p>
 */
class FieldInspector extends FieldVisitor
{
	// informacoes sobre o campo
	private FieldInfo fieldInfo;
	

	/**
	 * <p>
	 * Constrói um novo inspetor de campos.
	 * </p>
	 * <p><i>
	 * Constructs a new field inspector.
	 * </i></p>
	 * 
	 * @param fieldInfo - objeto onde as informações extraídas serão armazenadas
	 * <br><i>the object to store the collected information</i>
	 * @see org.esfinge.liveprog.reflect.FieldInfo
	 */
	FieldInspector(FieldInfo fieldInfo)
	{
		super(Opcodes.ASM6);
		this.fieldInfo = fieldInfo;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String name, boolean visible)
	{
		// armazena somente as anotacoes visiveis em tempo de execucao (runtime)
		if ( visible )
		{
			AnnotationInfo annot = new AnnotationInfo();
			annot.setName(InstrumentationHelper.toQualifiedName(name).substring(1, name.length()-1));
			
			// adiciona a anotacao
			this.fieldInfo.addAnnotationInfo(annot);
			
			// coleta as demais informacoes da anotacao
			return ( new AnnotationInspector(annot) );
		}

		//
		return ( null );
	}
}
