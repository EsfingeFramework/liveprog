package org.esfinge.liveprog.instrumentation;

import org.esfinge.liveprog.reflect.AnnotationInfo;
import org.esfinge.liveprog.reflect.MethodInfo;
import org.esfinge.liveprog.reflect.ParameterInfo;
import org.esfinge.liveprog.reflect.TypeHandler;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * <p>
 * Extrai informações sobre um método. 
 * <p><i>
 * Collects information about a method.
 * </i>
 */
class MethodInspector extends MethodVisitor
{
	// informacoes sobre o metodo
	private MethodInfo methodInfo;
	
	// label de referencia para identificar os parametros dos metodos
	private Label paramsLabelRef;

	
	/**
	 * <p>
	 * Constrói um novo inspetor de métodos.
	 * <p><i>
	 * Constructs a new method inspector.
	 * </i>
	 * 
	 * @param methodInfo objeto onde as informações extraídas serão armazenadas
	 * <br><i>the object to store the collected information</i>
	 * @see org.esfinge.liveprog.reflect.MethodInfo
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
			annot.setName(InstrumentationHelper.toQualifiedName(name).substring(1, name.length()-1));
			
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
			annot.setName(InstrumentationHelper.toQualifiedName(name).substring(1, name.length()-1));

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
				param.setType(new TypeHandler(desc));
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
