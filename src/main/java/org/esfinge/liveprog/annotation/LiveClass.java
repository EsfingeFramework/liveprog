package org.esfinge.liveprog.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * Marca uma classe como sendo uma classe dinâmica, permitindo que possa ser atualizada em tempo de execução.
 * </p>
 * <p><i>
 * Annotates a class as a live class, enabling it to be updated at runtime.
 * </i></p>
 * 
 * @see org.esfinge.liveprog.annotation.IgnoreOnReload
 * @see org.esfinge.liveprog.annotation.InvokeOnReload
 * @see org.esfinge.liveprog.annotation.InvokeOnRollback
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface LiveClass
{
}
