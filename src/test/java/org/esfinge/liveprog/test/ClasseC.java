package org.esfinge.liveprog.test;

import org.esfinge.liveprog.annotation.InvokeOnReload;
import org.esfinge.liveprog.annotation.LiveClass;

@LiveClass
public class ClasseC
{
	// teste o uso da anotacao para verificar se 
	// o valor antigo vai ser ignorado na nova versao da classe
//	@IgnoreOnReload
	private int campo1;
	private int campo2;
	
	
	public ClasseC()
	{
		this.campo1 = 10;
		this.campo2 = 20;
	}
	
	public void setCampo1(int campo1)
	{
		this.campo1 = campo1;
	}

	public void setCampo2(int campo2)
	{
		this.campo2 = campo2;
	}

	public void somar()
	{
		System.out.println("Campo1: " + this.campo1);
		System.out.println("Campo2: " + this.campo2);
		System.out.println("SOMA: " + (this.campo1 + this.campo2));
	}
	
	@InvokeOnReload
	public void subtrair()
	{
		System.out.println("Campo1: " + this.campo1);
		System.out.println("Campo2: " + this.campo2);
		System.out.println("DIFERENCA: " + (this.campo1 - this.campo2));
	}
	
//	@InvokeOnReload
	public Number onReload()
	
	
	{
		System.out.println("@InvokeOnReload chamou um metodo que nao existia na antiga classe!");
		
		return ( new Integer(1) );
	}
}
