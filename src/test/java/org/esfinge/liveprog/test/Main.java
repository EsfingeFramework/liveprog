package org.esfinge.liveprog.test;

import org.esfinge.liveprog.LiveClassFactory;
import org.esfinge.liveprog.LiveClassFactoryBuilder;
import org.esfinge.liveprog.db.DefaultDBVersionManager;
import org.esfinge.liveprog.db.DefaultLiveClassDB;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		// gerenciador de BD
		DefaultLiveClassDB.setDatabaseFilePath("db/liveclass.db");
		
		// gerenciador de versoes
		DefaultDBVersionManager vManager = new DefaultDBVersionManager(DefaultLiveClassDB.getInstance());
		vManager.setVisible(true);
		
		// cria uma nova fabrica de objetos dinamicos
		LiveClassFactory factory = new LiveClassFactoryBuilder()
											.monitoringDirectory("target/classes/liveprog/test")
											.excludingSubdirs()
											.usingDatabaseManager(DefaultLiveClassDB.getInstance())
											.usingVersionManager(vManager)
											.build();
		
		ClasseA liveA_prod = factory.createLiveObject(ClasseA.class);
		ClasseA liveA_test = factory.createLiveObjectInTestMode(ClasseA.class);
		ClasseB liveB = factory.createLiveObject(ClasseB.class);
		
		liveA_prod.setB(liveB);
		liveA_test.setB(liveB);
		
		liveA_prod.test();
		liveA_test.test();
	}
}
