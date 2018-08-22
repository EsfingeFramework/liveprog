package liveprog.test;

import org.esfinge.liveprog.LiveClassFactory;
import org.esfinge.liveprog.LiveClassFactoryBuilder;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		// cria uma nova fabrica de objetos dinamicos
		LiveClassFactory factory = new LiveClassFactoryBuilder()
											.inTestMode()
											.monitoringDirectory("liveDir")
											.excludingSubdirs()
											.usingDatabaseFile("db/liveclass.db")
											.build();
		
		ClasseA liveA = factory.createLiveObject(ClasseA.class);
		ClasseB liveB = factory.createLiveObject(ClasseB.class);
		
		liveA.setB(liveB);
		
		liveA.test();
		liveB.test();
	}
}
