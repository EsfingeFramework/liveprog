package org.esfinge.liveprog;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.esfinge.liveprog.util.ClassInstrumentation;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;

/**
 * Carregador de classes dinamicas.
 * 
 * @see org.esfinge.LiveClass
 */
public class LiveClassLoader
{
	// mapa das versoes atuais de cada classe dinamica carregada
	private Map<String,Integer> mapClassVersion;
	
	
	/**
	 * Cria um novo carregador de classes dinamicas.
	 * 
	 * @param baseDir o diretorio base das classes 
	 */
	LiveClassLoader()
	{
		this.mapClassVersion = new HashMap<String,Integer>();
	}
	
	/**
	 * Carrega dinamicamente a nova versao da classe.
	 * 
	 * @param classFile o arquivo da classe dinamica atualizada
	 * @return a nova versao da classe dinamica
	 */
	public Class<?> loadUpdatedClass(File classFile)
	{
		try
		{
			// 
			ClassInstrumentation classInstr = new ClassInstrumentation(classFile);
			
			// TODO: debug
			System.out.println("LOADER >> " + classInstr.getSimpleClassName());
			Class<?> origClass = Class.forName(classInstr.getClassName());
			
			// nova versao da classe
			int classVersion = mapClassVersion.getOrDefault(classInstr.getClassName(), 0) + 1;

			// carrega a nova versao da classe
			Class<?> newClass = this.makeLiveClass(classInstr.getBytecode(), classInstr.getClassName(), classVersion);
			
			// verifica se a interface publica da nova versao  eh compativel com a versao original da classe
//			this.checkPublicInterfaceIntegrity(origClass, newClass);

			
			// atualiza a versao
			this.mapClassVersion.put(classInstr.getClassName(), classVersion);
			
			//
			return ( newClass );
		}
		catch ( Exception e )
		{
			// TODO: debug
			e.printStackTrace();
			
			return ( null );
		}
	}
	
	private void checkPublicInterfaceIntegrity(Class<?> oldClass, Class<?> newClass)
	{
		try
		{
			// verifica se a nova classe possui os metodos publicos existentes na classe antiga
			for ( Method m : oldClass.getMethods() )
			{
				// ignora os metodos da classe Object
				if ( m.getDeclaringClass() == Object.class )
					continue;
				
				// tenta obter o mesmo metodo na nova classe
				Method m1 = newClass.getMethod(m.getName(), m.getParameterTypes());
				
				// verifica se o tipo de retorno eh compativel
				if (! m.getReturnType().isAssignableFrom(m1.getReturnType()) )
					throw new NoSuchMethodException(
							String.format("Incompatible return types on method '%s': [%s][%s]", 
									m.getName(), m.getReturnType(), m1.getReturnType()));
				
				//TODO: debug
				System.out.format("Method OK:\n>> %s\n>> %s\n\n", m, m1);
			}
			
		}
		catch ( NoSuchMethodException e )
		{
			// TODO: debug
			e.printStackTrace();
		}
	}
	
	/**
	 * Cria a nova versao da classe, instrumentalizada dinamicamente via ASM.
	 * 
	 * @param classData os bytes do arquivo .class da classe atualizada
	 * @param className o nome da classe dinamica
	 * @param classVersion a nova versao da classe dinamica 
	 * @return
	 */
	private Class<?> makeLiveClass(byte[] classData, String className, int classVersion)
	{
		// nome da classe_versao
		String newName = String.format("%s_v%d", className, classVersion);
		
		// ASM 
		// modifica o cabecalho da classe, para que ela tenha o novo nome
		ClassWriter w = new ClassWriter(0);
		ClassReader r = new ClassReader(classData);
		ClassRemapper cr = new ClassRemapper(w, new SimpleRemapper(className.replace('.', '/'), newName.replace('.', '/')));
		r.accept(cr, 0);
		
		// obtem os bytes da nova classe instrumentada
		byte[] newData = w.toByteArray();
		
		// carrega dinamicamente a nova classe
		return ( new DynamicLoader().defineClass(newName, newData) );
	}
	
	
	/**
	 * Carregador dinamico de classes.
	 */
	private static class DynamicLoader extends ClassLoader
	{
		public Class<?> defineClass(String name, byte[] b)
		{
			// carrega a classe
			Class<?> c = defineClass(name, b, 0, b.length);
			
			// verifica se o pacote da classe esta definido
			String packageName = null;
			int pos = name.lastIndexOf('.');
			if ( pos != -1 )
			{
				packageName = name.substring(0, pos);
			
				Package pkg = getPackage(packageName);
				
				if ( pkg == null )
					definePackage(packageName, null, null, null, null, null, null, null);
			}
			
			// 
			return c;
		}
	}
}
