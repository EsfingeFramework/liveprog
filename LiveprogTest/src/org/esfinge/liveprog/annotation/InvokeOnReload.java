package org.esfinge.liveprog.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indica um metodo a ser invocado quando a nova versao da classe for carregada dinamicamente.
 * 
 * Essa anotacao deve ser utilizada na NOVA (versao da) classe, que sera atualizada em tempo de execucao.
 * 
 * @see org.esfinge.liveprog.annotation.LiveClass
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface InvokeOnReload
{
}
