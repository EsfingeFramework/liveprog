package org.esfinge.liveprog.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indica uma propriedade da classe dinamica cujo valor nao sera copiado da antiga versao 
 * quando a nova versao for carregada dinamicamente.
 * 
 * Essa anotacao deve ser utilizada na NOVA (versao da) classe, que sera atualizada em tempo de execucao.
 * 
 * @see org.esfinge.liveprog.annotation.LiveClass
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface IgnoreOnReload
{

}
