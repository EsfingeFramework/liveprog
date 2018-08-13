package org.esfinge.liveprog.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indica uma classe dinamica, que pode ser atualizada em tempo de execucao.
 * 
 * A classe deve implementar um construtor padrao (vazio) para que possa 
 * ser instanciada dinamicamente!
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface LiveClass
{
}
