package org.esfinge.liveprog.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * Marca um método para ser invocado quando a classe dinâmica for recarregada após um rollback.
 * Métodos anotados com {@link InvokeOnRollback} são invocados antes que os métodos anotados com {@link InvokeOnReload}. 
 * <p><i>
 * Annotates a method to be invoked when the LiveClass is reloaded after a rollback.
 * Methods annotated with {@link InvokeOnRollback} are invoked prior to those annotated with {@link InvokeOnReload}.
 * </i>
 * 
 * @see org.esfinge.liveprog.annotation.LiveClass
 * @see org.esfinge.liveprog.annotation.InvokeOnReload
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface InvokeOnRollback
{
}
