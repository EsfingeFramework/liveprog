package org.esfinge.liveprog;

import java.util.Map;

/**
 * <p>
 * Quando uma nova versão da classe dinâmica é carregada, por padrão os valores das propriedades do objeto antigo 
 * são copiados para a nova versão se elas tiverem o mesmo nome.
 * <br> 
 * Esta interface, quando implementada pela classe dinâmica, permite customizar a forma de carregamento do estado 
 * do antigo objeto na nova classe, recebendo um mapa com o nome e valor das propriedades da versão sendo substituída.
 * </p>
 * <p><i>
 * When a new version of a LiveClass is loaded, by default a property value is copied from the old object
 * to its new version if their names are the same.
 * <br> 
 * This interface may be implemented by LiveClasses in order to customize the way they load the state of the old object
 * in the new class, receiving a map of the names and values of the properties of the version being replaced.
 * </i></p>
 * 
 * @see org.esfinge.liveprog.annotation.LiveClass
 */
public interface ILiveClassState
{
	/**
	 * <p>
	 * Prepara um mapa das propriedades da versão atual que seja compatível com a versão que será revertida,
	 * para que sejam carregadas corretamente pela classe da versão anterior.
	 * </p>
	 * <p><i>
	 * Prepares a map of the properties of this current version that is compatible with the version that will be brought back,  
	 * in order to be correctly loaded by it.
	 * </i></p>
	 * 
	 * @return mapa dos nomes e valores das propriedades da versão atual compatível com as propriedades da versão que será revertida
	 * <br><i>map of names and values of the properties of this current version compatible with the properties of the version that will be brought back</i>
	 */
	public Map<String,Object> prepareToRollback();
	
	/**
	 * <p>
	 * Recebe um mapa com o nome e valor das propriedades da versão sendo substituída, 
	 * para que sejam carregados nesta nova versão da classe.
	 * </p>
	 * <p><i>
	 * Receives a map of the names and values of the properties of the version being replaced, 
	 * in order to load its state in this new class.
	 * </i></p>
	 * 
	 * @param mapState - mapa dos nomes e valores das propriedades da antiga versão para serem carregados na nova classe
	 * <br><i>map of names and values of the properties of the previous version to be loaded in this new class</i>
	 */
	public void load(Map<String,Object> mapState);
}
