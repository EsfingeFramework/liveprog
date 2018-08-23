package liveprog.test;

import java.util.Map;

import org.esfinge.liveprog.ILiveClassStateLoader;
import org.esfinge.liveprog.annotation.*;

// versao 1
///*
@LiveClass
public class ClasseA
{
	private int campo1 = 10;
	private int campo2 = 20;
	private ClasseB b;
	
	public void test()
	{
		System.out.println("A >> A.test(): " + this.getClass().getSimpleName());
		System.out.println("A >> campo1: " + this.campo1);
		System.out.println("A >> campo2: " + this.campo2);
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
//*/

// versao 2
/*
@LiveClass
public class ClasseA implements IStateLoader
{
	private int campoA;
	private int campoB;
	private ClasseB b;
	
	@InvokeOnReload
	public void test()
	{
		System.out.println("A >> A.test(): " + this.getClass().getSimpleName());
		System.out.println("A >> campoA: " + this.campoA);
		System.out.println("A >> campoB: " + this.campoB);
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
		
		System.out.println("A >> campoA - campoB: " + (this.campoA - this.campoB));
	}

	@Override
	public void load(Map<String, Object> mapState)
	{
		Object valor = mapState.get("campo1");
		if ( valor instanceof Integer )
			this.campoA = ((Integer) valor).intValue() * 10;
		
		valor = mapState.get("campo2");
		if ( valor instanceof Integer )
			this.campoB = ((Integer) valor).intValue() * 10;
		
		valor = mapState.get("b");
		if ( b instanceof ClasseB )
			this.b = (ClasseB) valor;
	}
}
*/