package org.esfinge.liveprog.instrumentation;

import org.esfinge.liveprog.reflect.AnnotationInfo;
import org.esfinge.liveprog.reflect.ClassInfo;
import org.esfinge.liveprog.reflect.FieldInfo;
import org.esfinge.liveprog.reflect.MethodInfo;
import org.esfinge.liveprog.reflect.TypeHandler;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * <p>
 * Extrai informações sobre uma classe. 
 * </p>
 * <p><i>
 * Collects information about a class.
 * </i></p>
 */
class ClassInspector extends ClassVisitor
{
	// informacoes sobre a classe
	private ClassInfo classInfo;
	

	/**
	 * <p>
	 * Constrói um novo inspetor de classes.
	 * </p>
	 * <p><i>
	 * Constructs a new class inspector.
	 * </i></p>
	 * 
	 * @param classBytecode - bytecodes da classe
	 * <br><i>the bytecodes of the class</i>
	 */
	ClassInspector(byte[] classBytecode)
	{
		super(Opcodes.ASM6);
		
		this.classInfo = new ClassInfo();
		this.classInfo.setBytecode(classBytecode);
		
		// carrega as informacoes da classe
		new ClassReader(classBytecode).accept(this, 0);
	}
	
	/**
	 * <p>
	 * Obtém as informações extraídas da classe.
	 * </p>
	 * <p><i>
	 * Gets the class collected information.
	 * </i></p>
	 * 
	 * @return as informações da classe inspecionada
	 * <br><i>the collected information of the class</i>
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
		this.classInfo.setName(InstrumentationHelper.toQualifiedName(name));
		this.classInfo.setSuperclassName(InstrumentationHelper.toQualifiedName(superName));
		
		// adiciona as interfaces
		for ( String interf : interfaces )
			this.classInfo.addInterfaceName(InstrumentationHelper.toQualifiedName(interf));
	}
	
	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access)
	{
		// ignora classes internas estaticas
//		if ( Modifier.isStatic(access) )
//			return;

		// ignora as classes de Lookup de lambdas
		if ( name.equals("java/lang/invoke/MethodHandles$Lookup") )
			return;
		
		// nome da classe
		String className = this.classInfo.getName();
		
		// nome da classe interna
		String innerClassName = InstrumentationHelper.toQualifiedName(name);
		
		// classe interna referenciando a classe externa imediatamente superior
		if ( className.equals(innerClassName) )
			this.classInfo.setEnclosingClassName(InstrumentationHelper.toQualifiedName(outerName));
		
		else
		{
			// classe externa referenciando a classe interna
			if ( innerClassName.length() > className.length() )
				this.classInfo.addInnerClassName(innerClassName);
			
			// obtem a classe externa topo, que declara essa classe interna
			else if ( outerName != null )
			{
				String declaringClass = InstrumentationHelper.toQualifiedName(outerName);
				
				if ( this.classInfo.getDeclaringClassName() == null )
					this.classInfo.setDeclaringClassName(declaringClass);
				
				else if ( this.classInfo.getDeclaringClassName().length() > declaringClass.length() )
					this.classInfo.setDeclaringClassName(declaringClass);
			}
		}
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
	{
		FieldInfo field = new FieldInfo();
		field.setAccessFlag(access);
		field.setName(name);
		field.setType(new TypeHandler(desc));
		
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
		method.setReturnType(new TypeHandler(Type.getType(desc).getReturnType()));
		
		// adiciona as excecoes
		if ( exceptions != null )
			for ( String e : exceptions )
				method.addThrownExceptionName(InstrumentationHelper.toQualifiedName(e));
		
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
			annot.setName(InstrumentationHelper.toQualifiedName(name).substring(1, name.length()-1));
			
			// adiciona a anotacao
			this.classInfo.addAnnotationInfo(annot);
			
			// coleta as demais informacoes da anotacao
			return ( new AnnotationInspector(annot) );
		}

		//
		return ( null );
	}	
}
