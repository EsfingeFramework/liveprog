package liveprog.test;

import org.esfinge.liveprog.annotation.LiveClass;

@LiveClass
public class ClasseB
{
	/**
	 * Imprime o nome da classe.
	 * Como ela eh uma classe dinamica, vai mostrar qual eh a versao atual.
	 */
	public void test()
	{
		System.out.println("B >> " + this.getClass().getSimpleName());
	}
	
	/**
	 * Escreva qualquer coisa no metodo e salve para atualizar a classe
	 * e a magica do framework funcionar! ;-)
	 */
	private void modifyMe()
	{
		// c
	}
}
