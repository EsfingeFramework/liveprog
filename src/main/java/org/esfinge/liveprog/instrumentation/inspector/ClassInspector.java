package org.esfinge.liveprog.instrumentation.inspector;

import java.io.File;

import org.esfinge.liveprog.instrumentation.AnnotationInfo;
import org.esfinge.liveprog.instrumentation.ClassInfo;
import org.esfinge.liveprog.instrumentation.FieldInfo;
import org.esfinge.liveprog.instrumentation.InstrumentationService;
import org.esfinge.liveprog.instrumentation.MethodInfo;
import org.esfinge.liveprog.instrumentation.TypeWrapper;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Classe utilitaria que facilita a recuperacao de informacoes sobre uma classe.
 */
class ClassInspector extends ClassVisitor
{
	// informacoes sobre a classe
	private ClassInfo classInfo;
	

	/**
	 * Inicializa um novo analisador de classe.
	 * 
	 * Pelo menos um dos parametros deve ser informado.
	 *  
	 * @param classFile o arquivo da classe compilada (.class)
	 * @param classBytecode os bytecodes da classe
	 */
	ClassInspector(File classFile, byte[] classBytecode)
	{
		super(Opcodes.ASM6);
		
		this.classInfo = new ClassInfo();
		this.classInfo.setClassFile(classFile);
		this.classInfo.setBytecode(classBytecode);
		
		// carrega as informacoes da classe
		new ClassReader(classBytecode).accept(this, 0);
	}
	
	/**
	 * Retorna as informacoes da classe analisada.
	 * 
	 * @return as informacoes da classe analisada
	 */
	ClassInfo getClassInfo()
	{
		return ( this.classInfo );
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
	{
		this.classInfo.setVersion(version);		
		this.classInfo.setAccessFlag(access);
		this.classInfo.setName(InstrumentationService.toQualifiedName(name));
		this.classInfo.setSuperclassName(InstrumentationService.toQualifiedName(superName));
		
		// adiciona as interfaces
		for ( String interf : interfaces )
			this.classInfo.addInterfaceName(InstrumentationService.toQualifiedName(interf));
	}
	
	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access)
	{
		// nao processa classes internas estaticas
//		if ( Modifier.isStatic(access) )
//			return;

		
		// nome da classe
		String className = this.classInfo.getName();
		
		// nome da classe interna
		String innerClassName = InstrumentationService.toQualifiedName(name);
		
		// classe interna referenciando a classe externa
		if ( className.equals(innerClassName) )
			this.classInfo.setOuterClassName(innerClassName.substring(0, innerClassName.lastIndexOf('$')));
		
		else
		{
			// classe externa referenciando a classe interna
			if ( innerClassName.length() > className.length() )
				this.classInfo.addInnerClassName(innerClassName);
			
			// classe interna referenciando uma classe externa acima da classe externa pai!
			// ignora, pois essa classe ja esta referenciada na classe externa pai..
		}
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
	{
		FieldInfo field = new FieldInfo();
		field.setAccessFlag(access);
		field.setName(name);
		field.setType(new TypeWrapper(desc));
		
		// adiciona o campo
		this.classInfo.addFieldInfo(field);

		// coleta as demais informacoes do campo
		return ( new FieldInspector(field) );
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
	{
		MethodInfo method = new MethodInfo();
		method.setAccessFlag(access);
		method.setName(name);
		method.setReturnType(new TypeWrapper(Type.getType(desc).getReturnType()));
		
		// adiciona as excecoes
		if ( exceptions != null )
			for ( String e : exceptions )
				method.addThrownExceptionName(InstrumentationService.toQualifiedName(e));
		
		// adiciona o metodo
		this.classInfo.addMethodInfo(method);
		
		// coleta as demais informacoes do metodo 
		return ( new MethodInspector(method) );
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
			this.classInfo.addAnnotationInfo(annot);
			
			// coleta as demais informacoes da anotacao
			return ( new AnnotationInspector(annot) );
		}

		//
		return ( null );
	}	
}
