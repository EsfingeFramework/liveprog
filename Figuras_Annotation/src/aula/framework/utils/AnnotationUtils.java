package aula.framework.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import aula.framework.dao.MapeadorBoolean;
import aula.framework.dao.MapeadorDouble;
import aula.framework.dao.MapeadorFloat;
import aula.framework.dao.MapeadorInteger;
import aula.framework.dao.MapeadorLong;
import aula.framework.dao.MapeadorNulo;
import aula.framework.dao.MapeadorPropriedade;

public class AnnotationUtils
{
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
	
	public static MapeadorPropriedade<?> getMapeadorPropriedade(Class<?> tipo)
	{
		//boleano
		if ( tipo.isAssignableFrom(boolean.class) || tipo.isAssignableFrom(Boolean.class) )					 
			return ( new MapeadorBoolean() );
		
		// inteiro
		if ( tipo.isAssignableFrom(int.class) || tipo.isAssignableFrom(Integer.class) )
			return ( new MapeadorInteger() );
		
		// long
		if ( tipo.isAssignableFrom(long.class) || tipo.isAssignableFrom(Long.class) )
			return ( new MapeadorLong() ); 
		
		// float
		if ( tipo.isAssignableFrom(float.class) || tipo.isAssignableFrom(Float.class) )
			return ( new MapeadorFloat() ); 
		
		// double
		else if ( tipo.isAssignableFrom(double.class) || tipo.isAssignableFrom(Double.class) )
			return ( new MapeadorDouble() );
		
		// String / Object
		return ( new MapeadorNulo() );			
	}
}
