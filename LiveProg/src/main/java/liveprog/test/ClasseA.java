package liveprog.test;

import org.esfinge.liveprog.annotation.InvokeOnReload;
import org.esfinge.liveprog.annotation.LiveClass;

@LiveClass
public class ClasseA
{
	private ClasseB b;
	
	/**
	 * Imprime o nome da classe.
	 * Como ela eh uma classe dinamica, vai mostrar qual eh a versao atual.
	 */
	@InvokeOnReload
	public void test()
	{
		System.out.println("A >> " + this.getClass().getSimpleName());
		this.callB();
	}
	
	public void setB(ClasseB b)
	{
		this.b = b;
	}
	
	/**
	 * Imprime o nome da classe B.
	 * Como ela eh uma classe dinamica, vai mostrar qual eh a versao atual da B.
	 * 
	 * Serve para verificar se o valor da propriedade b (que tb eh uma classe dinamica) 
	 * foi copiado corretamente quando uma nova versao da classe A eh criada.
	 * 
	 * para a nova classe
	 */
	public void callB()
	{
		System.out.println("AB >> " + this.b);
		
		if ( this.b != null )
			this.b.test();
	}
	
	/**
	 * Escreva qualquer coisa no metodo e salve para atualizar a classe
	 * e a magica do framework funcionar! ;-)
	 */
	private void modifyMe()
	{
		//a
	}
}
