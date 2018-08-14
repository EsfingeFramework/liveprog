package aula.framework;

import java.lang.reflect.Field;

import aula.framework.figura.Circulo;
import aula.framework.figura.Retangulo;
import aula.framework.figura.annotation.Mapeador;
import aula.framework.utils.AnnotationUtils;

public class Main
{
	public static void main(String[] args)
	{
		for ( Field f : AnnotationUtils.getAnnotadedFields(Retangulo.class, Mapeador.class) )
			System.out.println(f);
		
		System.out.println(AnnotationUtils.getField(Circulo.class, "posX"));
	}
}
