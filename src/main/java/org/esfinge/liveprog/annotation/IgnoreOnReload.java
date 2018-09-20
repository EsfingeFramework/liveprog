package org.esfinge.liveprog.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * Marca uma propriedade para ser ignorada quando a classe dinâmica for atualizada em tempo de execução.
 * Com isso o seu valor não será copiado para a nova versão.
 * </p>
 * <p><i>
 * Annotates a property to be ignored when the LiveClass is updated at runtime.
 * As a result its value will not be copied to the new version.
 * </i></p>
 * 
 * @see org.esfinge.liveprog.annotation.LiveClass
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface IgnoreOnReload
{

}
