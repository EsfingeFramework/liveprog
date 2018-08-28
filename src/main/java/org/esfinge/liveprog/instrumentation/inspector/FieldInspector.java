package org.esfinge.liveprog.instrumentation.inspector;

import org.esfinge.liveprog.instrumentation.AnnotationInfo;
import org.esfinge.liveprog.instrumentation.FieldInfo;
import org.esfinge.liveprog.instrumentation.InstrumentationService;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Classe utilitaria que facilita a recuperacao de informacoes sobre um campo/propriedade de uma classe.
 */
class FieldInspector extends FieldVisitor
{
	// informacoes sobre o campo
	private FieldInfo fieldInfo;
	

	/**
	 * Inicializa um novo analisador de campos/propriedades.
	 * 
	 * @param fieldInfo onde as informacoes do campo serao armazenadas
	 * @see org.esfinge.liveprog.instrumentation.FieldInfo
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
			annot.setName(InstrumentationService.toQualifiedName(name).substring(1, name.length()-1));
			
			// adiciona a anotacao
			this.fieldInfo.addAnnotationInfo(annot);
			
			// coleta as demais informacoes da anotacao
			return ( new AnnotationInspector(annot) );
		}

		//
		return ( null );
	}
}
