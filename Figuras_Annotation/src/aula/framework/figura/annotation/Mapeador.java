package aula.framework.figura.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import aula.framework.dao.MapeadorPropriedade;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Mapeador
{
	Class<? extends MapeadorPropriedade<?>> value();
}
