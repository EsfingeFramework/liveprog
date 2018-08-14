package org.esfinge.liveprog;

import java.util.Map;

/**
 * Recebe um mapa com as propriedades e valores do objeto a ser substituido pela nova versao, 
 * para que os valores sejam carregados na nova classe quando esta for atualizada em tempo de execucao.
 * 
 * @see org.esfinge.liveprog.annotation.LiveClass
 */
public interface IStateLoader
{
	/**
	 * Recebe o mapa com as propriedades e valores do objeto sendo substituido, 
	 * para que sejam carregados na nova versao da classe.
	 * @param mapState o mapa das propriedades e seus respectivos valores do objeto sendo substituido
	 */
	public void load(Map<String,Object> mapState);
}
