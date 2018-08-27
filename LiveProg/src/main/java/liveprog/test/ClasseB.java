package liveprog.test;

import org.esfinge.liveprog.annotation.InvokeOnReload;
import org.esfinge.liveprog.annotation.LiveClass;

//versao 1
///*
@LiveClass
public class ClasseB
{
	private String myString = "My String";
	
	@InvokeOnReload
	public void test()
	{
		System.out.println("B >> B.test(): " + this.getClass().getSimpleName());
		System.out.println("B >> myString: " + this.myString);
	}
}
//*/


// versao 2
/*
@LiveClass
public class ClasseB
{
	@IgnoreOnReload
	private String myString;
	private InnerClasseB innerB;
	
	public ClasseB()
	{
		this.innerB = new InnerClasseB();
	}
	
	@InvokeOnReload
	public void test()
	{
		System.out.println("B >> B.test(): " + this.getClass().getSimpleName());
		System.out.println("B >> myString: " + this.myString);
		System.out.println("B >> Calling InnerClass: " + this.innerB.callInner());
	}
	
	private class InnerClasseB
	{
		public String callInner()
		{
			return "Hello from InnerB!";
		}
	}
}
//*/
