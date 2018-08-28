package org.esfinge.liveprog.instrumentation.inspector;

import org.esfinge.liveprog.instrumentation.AnnotationInfo;
import org.esfinge.liveprog.instrumentation.InstrumentationService;
import org.esfinge.liveprog.instrumentation.MethodInfo;
import org.esfinge.liveprog.instrumentation.ParameterInfo;
import org.esfinge.liveprog.instrumentation.TypeWrapper;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Classe utilitaria que facilita a recuperacao de informacoes sobre um metodo de uma classe.
 */
class MethodInspector extends MethodVisitor
{
	// informacoes sobre o metodo
	private MethodInfo methodInfo;
	
	// label de referencia para identificar os parametros dos metodos
	private Label paramsLabelRef;

	
	/**
	 * Inicializa um novo analisador de metodos.
	 * 
	 * @param methodInfo onde as informacoes do metodo serao armazenadas
	 * @see org.esfinge.liveprog.instrumentation.MethodInfo
	 */
	MethodInspector(MethodInfo methodInfo)
	{
		super(Opcodes.ASM6);
		this.methodInfo = methodInfo;
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
			this.methodInfo.addAnnotationInfo(annot);
			
			// coleta as demais informacoes da anotacao
			return ( new AnnotationInspector(annot) );
		}

		//
		return ( null );
	}
	
	@Override
	public AnnotationVisitor visitParameterAnnotation(int index, String name, boolean visible)
	{
		if ( visible )
		{
			AnnotationInfo annot = new AnnotationInfo();
			annot.setName(InstrumentationService.toQualifiedName(name).substring(1, name.length()-1));

			// como esse metodo eh chamado antes de visitar os parametros,
			// cria o parametro para armazenar a anotacao
			ParameterInfo param = new ParameterInfo();
			param.setIndex(index);

			// adiciona a anotacao no parametro
			param.addAnnotationInfo(annot);
			
			// adiciona o parametro 
			this.methodInfo.addParameterInfo(param);
			
			// coleta as demais informacoes da anotacao
			return ( new AnnotationInspector(annot) );
		}

		//
		return ( null );
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
	{
		// verifica o que eh parametro do metodo e o que eh variavel interna do metodo
		
		// index = 0 --> THIS
		if ( index == 0 )
		{
			// 1. o start label do objeto THIS vai servir de referencia 
			//    para encontrar os parametros do metodo
			// 2. os parametros do metodo possuem o mesmo start label do objeto THIS,
			//    por isso guardamos o seu start label para a comparacao
			this.paramsLabelRef = start;
		}
		else
		{
			// os parametros do metodo possuem o mesmo start label do objeto THIS
			if ( start.equals(this.paramsLabelRef) )
			{
				// verifica se ja foi criado o parametro (em visitParameterAnnotation)
				ParameterInfo param = this.methodInfo.getParameterAtIndex(index - 1);			
				
				if ( param == null )
					param = new ParameterInfo();
				
				param.setName(name);
				param.setType(new TypeWrapper(desc));
				param.setIndex(index - 1);
				
				// adiciona o parametro
				this.methodInfo.addParameterInfo(param);
			}
			
			// eh uma variavel interna do metodo
		}
	}

	@Override
	public void visitEnd()
	{
		super.visitEnd();
		
		//
		this.paramsLabelRef = null;
	}
}
