package org.esfinge.liveprog.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.esfinge.liveprog.ILiveClassStateLoader;
import org.esfinge.liveprog.annotation.IgnoreOnReload;

/**
 * Classe com metodos utilitarios usados no framework.
 */
public class Utils
{
	
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
	
	@SuppressWarnings("unchecked")
	public static <T> void addToCollection(Collection<T> collection, T... elements)
	{
		if ( elements != null )
			for ( T element : elements )
				collection.add(element);
	}
	
	public static <T> T getFromCollection(Collection<T> collection, Predicate<T> filter)
	{
		return ( collection.stream().filter(filter).findFirst().orElse(null) );
	}
	
	public static <T> List<T> filterFromCollection(Collection<T> collection, Predicate<T> filter)
	{
		return ( collection.stream().filter(filter).collect(Collectors.toList()) );
	}
	
	public static boolean copyProperties(Object oldObj, Object newObj)
	{
		try
		{
			// verifica se a nova classe implementa a interface IStateLoader
			if ( newObj instanceof ILiveClassStateLoader )
			{
				// TODO: debug
				System.out.format("UTILS >> Criando mapa de propriedades para carregamento via IStateLoader!\n\n");

				// cria o mapa de propriedades do objeto antigo
				Map<String,Object> mapState = new HashMap<String,Object>();
				
				for ( Field oldObjField : getFields(oldObj.getClass()) )
				{
					// armazena a propriedade no objeto antigo
					oldObjField.setAccessible(true);
					mapState.put(oldObjField.getName(), oldObjField.get(oldObj));
				}
				
				// carrega o estado no novo objeto
				((ILiveClassStateLoader) newObj).load(mapState);
			}
			else
			{
				// obtem as propriedades do novo objeto
				for ( Field newObjField : getFields(newObj.getClass()) )
				{
					// verifica se o campo esta marcado com @IgnoreOnReload
					if ( newObjField.isAnnotationPresent(IgnoreOnReload.class) )
					{
						// TODO: debug
						System.out.println("UTILS >> Propriedade ignorada: " + newObjField.getName());
						
						continue;
					}
					
					// obtem a propriedade no objeto antigo
					Field oldObjField = getField(oldObj.getClass(), newObjField.getName());						
				
					if ( oldObjField != null )
					{
						//
						newObjField.setAccessible(true);
						oldObjField.setAccessible(true);
						
						// copia o valor antigo para o novo objeto
						newObjField.set(newObj, oldObjField.get(oldObj));
	
						// TODO: debug
						System.out.format("UTILS >> Propriedade copiada: [%s , %s]\n", newObjField.getName(), newObjField.get(newObj));
					}
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
