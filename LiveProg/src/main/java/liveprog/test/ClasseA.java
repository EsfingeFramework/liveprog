package liveprog.test;

import org.esfinge.liveprog.annotation.InvokeOnReload;
import org.esfinge.liveprog.annotation.LiveClass;

// versao 1
///*
@LiveClass
public class ClasseA
{
	private int campo1 = 10;
	private int campo2 = 20;
	private ClasseB b;
	
	@InvokeOnReload
	public void test()
	{
		System.out.println("A >> A.test(): " + this.getClass().getSimpleName());
		System.out.println("A >> campo1: " + this.campo1);
		System.out.println("A >> campo2: " + this.campo2);
		
		// versao 2
//		System.out.println("A >> version 2 date: " + new Date());
		
		// versao 3
//		System.out.println("A >> New version 3 released: " + new Date());
		this.callB();
	}
	
	public void setB(ClasseB b)
	{
		this.b = b;
	}
	
	public void callB()
	{
		System.out.println("A >> A.callB()");
		System.out.println("A >> chamando B.test(): " + this.b.getClass().getSimpleName());
		
		if ( this.b != null )
			this.b.test();
	}
}