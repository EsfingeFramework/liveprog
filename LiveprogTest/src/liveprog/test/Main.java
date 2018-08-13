package liveprog.test;

import org.esfinge.liveprog.LiveClassFactory;
import org.esfinge.liveprog.monitor.FileSystemMonitor;
import org.esfinge.liveprog.monitor.IMonitor;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		
		// cria uma nova fabrica de objetos dinamicos
		IMonitor monitor = new FileSystemMonitor("bin", true);
		LiveClassFactory factory = new LiveClassFactory(monitor);
		monitor.start();
		
		/*
		ClasseA liveA = factory.createObject(ClasseA.class);
		ClasseB liveB = factory.createObject(ClasseB.class);
		
		liveA.setB(liveB);
		liveA.test();
		liveB.test();
		*/
		
		
		// testa a criacao de objetos independentes da mesma classe dinamica  
		ClasseC c1 = factory.createObject(ClasseC.class);
		ClasseC c2 = factory.createObject(ClasseC.class);
		
		c1.setCampo1(1);
		c1.setCampo2(2);
		
		c2.setCampo1(100);
		c2.setCampo2(200);
		
		c1.somar();
		c2.somar();
		
		// modifique a ClasseC e veja se os resultados sao como esperado!
		
		
		/*
		IMonitorFileValidator validator = new JavaclassMonitorValidator();
		IMonitorFileFilter filter = new FileExtensionMonitorFilter("class");
		
		File f1 = Paths.get("bin/liveprog/test/Main.class").toFile();
		File f2 = Paths.get("bin/liveprog/test/ClasseA.class").toFile();
		File f3 = Paths.get(".project").toFile();
		
		System.out.println("[Filter]");
		System.out.format("%s - %s\n", f1.getName(), filter.acceptFile(f1));
		System.out.format("%s - %s\n", f2.getName(), filter.acceptFile(f2));
		System.out.format("%s - %s\n", f3.getName(), filter.acceptFile(f3));
		System.out.format("%s - %s\n", "null", filter.acceptFile(null));
		
		System.out.println("=====================================================================");
		System.out.println("[Validator]");
		System.out.format("%s - %s\n", f1.getName(), validator.isValid(f1));
		System.out.format("%s - %s\n", f2.getName(), validator.isValid(f2));
		System.out.format("%s - %s\n", f3.getName(), validator.isValid(f3));
		System.out.format("%s - %s\n", "null", validator.isValid(null));
		*/
		
		/*
		File f1 = Paths.get("bin/org/esfinge/liveprog/LiveProxy.class").toFile();
		File f2 = Paths.get("bin/liveprog/test/ClasseA.class").toFile();
		
		ClassInstrumentation cF1 = new ClassInstrumentation(f1);
		ClassInstrumentation cF2 = new ClassInstrumentation(f2);
		
		System.out.println(cF1.getClazz());
		System.out.println(cF1.getSimpleClassName());
		System.out.println(cF1.getClassName());
		System.out.println(cF1.getPackage());
		
		
		System.out.println("\n");
		System.out.println(cF2.getClazz());
		System.out.println(cF2.getSimpleClassName());
		System.out.println(cF2.getClassName());
		System.out.println(cF2.getPackage());
		*/

    }
}