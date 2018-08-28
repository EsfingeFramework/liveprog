package org.esfinge.liveprog.db;

/**
 * Descritor das versoes atuais de producao e testes de uma classe dinamica persistida.
 */
public interface ILiveClassDBVersion
{
	/**
	 * Retorna o nome da classe dinamica.
	 * 
	 * @return o nome da classe dinamica
	 */
	public String getName();
	
	/**
	 * Retorna a atual versao de producao da classe dinamica.
	 * 
	 * @return a versao de producao da classe dinamica
	 */
	public int getCurrentVersion();
	
	/**
	 * Retorna a atual versao de testes da classe dinamica.
	 * 
	 * @return a versao de testes da classe dinamica
	 */
	public int getTestVersion();
}
