package org.esfinge.liveprog.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * Marca um método para ser invocado quando a classe dinâmica for atualizada em tempo de execução.
 * <p><i>
 * Annotates a method to be invoked when the LiveClass is updated at runtime.
 * </i>
 * 
 * @see org.esfinge.liveprog.annotation.LiveClass
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface InvokeOnReload
{
}
