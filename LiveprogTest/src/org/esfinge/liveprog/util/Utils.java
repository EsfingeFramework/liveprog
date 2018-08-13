package org.esfinge.liveprog.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.esfinge.liveprog.annotation.IgnoreOnReload;

/**
 * Classe com metodos utilitarios usados no framework.
 */
public class Utils
{
	/**
	 * Traduz o caminho de um arquivo de classe para o nome completo da classe.
	 * 
	 * (Exemplo: org/esfinge/liveprog/ClasseA.class -> org.esfinge.liveprog.ClasseA)
	 * 
	 * @param classFilePath o caminho do arquivo da classe
	 * @return o nome qualificado da classe
	 */
	public static String getFullQualifiedClassName(String classFilePath)
	{
		// verifica se eh uma classe Java compilada
		if (! classFilePath.endsWith(".class") )
			return ( null );
		
		// transforma o caminho do arquivo -> nome completo da classe
		return ( classFilePath.replace(".class", "").replace(File.separator,".") );
	}
	
	/**
	 * Traduz o nome completo da classe para o caminho do arquivo de classe.
	 * 
	 * (Exemplo: org.esfinge.liveprog.ClasseA -> org/esfinge/liveprog/ClasseA.class)
	 * 
	 * @param fullQualifiedClassName o nome qualificado da classe
	 * @return o caminho do arquivo da classe
	 */
	public static String getClassFilePath(String fullQualifiedClassName)
	{
		return ( fullQualifiedClassName.replace(".", File.separator) + ".class" );
	}
	
	/**
	 * Retorna o conteudo (bytecoes) do arquivo da classe.
	 * 
	 * @param baseDir o diretorio base da classe 
	 * @param fullQualifiedClassName o nome qualificado da classe
	 * @return o conteudo do arquivo da classe (bytecodes)
	 */
	public static byte[] readClassFile(String baseDir, String fullQualifiedClassName) 
	{
		try
		{
			File file = new File(baseDir, getClassFilePath(fullQualifiedClassName));
			if (! file.exists() )
				return ( null );
					
	        FileInputStream input = new FileInputStream(file);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			
	        byte[] buffer = new byte[8192];
	        int read;

	        while ( (read=input.read(buffer, 0, 8192)) > -1 )
	            output.write(buffer, 0, read);
	        
	        input.close();
	        return ( output.toByteArray() );
		}
		catch ( Exception e )
		{
			// TODO: debug
			e.printStackTrace();
			return ( null );
		}
	}
	
	/**
	 * Retorna os metodos da classe que possuem a anotacao informada pelo parametro. 
	 * 
	 * @param clazz a classe com os metodos anotados
	 * @param annotationClazz a anotacao a ser verificada nos metodos
	 * @return uma lista com os metodos anotados da classe, ou uma lista vazia caso 
	 * nao exista na classe nenhum metodo anotado com a anotacao informada
	 */
	public static List<Method> getAnnotadedMethods(Class<?> clazz, Class<? extends Annotation> annotationClazz)
	{
		List<Method> methods = new ArrayList<Method>();
		
		do
		{
			methods.addAll(Arrays.asList(clazz.getDeclaredMethods())
					.stream()
					.filter(f -> f.isAnnotationPresent(annotationClazz))
					.collect(Collectors.toList()));

			clazz = clazz.getSuperclass();
		} while ( clazz != null );
		
		return ( methods );
	}
	
	public static List<Field> getFields(Class<?> clazz)
	{
		List<Field> fields = new ArrayList<Field>();
		
		do
		{
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
			clazz = clazz.getSuperclass();			
		} while ( clazz != null );
		
		return ( fields );
	}
	
	public static List<Field> getAnnotadedFields(Class<?> clazz, Class<? extends Annotation> annotationClazz)
	{
		List<Field> fields = new ArrayList<Field>();
		
		do
		{
			fields.addAll(Arrays.asList(clazz.getDeclaredFields())
					.stream()
					.filter(f -> f.isAnnotationPresent(annotationClazz))
					.collect(Collectors.toList()));

			clazz = clazz.getSuperclass();
		} while ( clazz != null );
		
		return ( fields );
	}
	
	public static Field getField(Class<?> clazz, String name)
	{
		try
		{
			return ( getFields(clazz).stream()
						.filter(f -> f.getName().equals(name))
						.findFirst().get() );
		}
		catch (Exception e)
		{
			return ( null );
		}
	}
	
	public static boolean copyProperties(Object oldObj, Object newObj)
	{
		try
		{
			// obtem as propriedades do novo objeto
			for ( Field newObjField : getFields(newObj.getClass()) )
			{
				// verifica se o campo esta marcado como @IgnoreOnReload
				if ( newObjField.isAnnotationPresent(IgnoreOnReload.class) )
				{
					// TODO: debug
					System.out.println("UTILS >> Propriedade ignorada: " + newObjField.getName());
					
					continue;
				}
				
				// obtem a propriedade do objeto antigo
				Field oldObjField = getField(oldObj.getClass(), newObjField.getName());
				
				if ( oldObjField != null )
				{
					//
					newObjField.setAccessible(true);
					oldObjField.setAccessible(true);
					
					// copia o valor antigo para o novo objeto
					newObjField.set(newObj, oldObjField.get(oldObj));

					// TODO: debug
					System.out.println("UTILS >> Propriedade copiada: " + newObjField.getName());
				}
			}
			
			return ( true );
		}
		catch ( Exception e )
		{
			// TODO: debug
			e.printStackTrace();
			
			return ( false );
		}
	}
}
