package org.esfinge.liveprog;

import org.esfinge.liveprog.LiveClassFactory;
import org.esfinge.liveprog.LiveClassLoader;
import org.esfinge.liveprog.monitor.FileSystemMonitor;
import org.esfinge.liveprog.util.ClassInstrumentation;
import org.junit.*;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class LiveClassFactoryTest {    
	FileSystemMonitor monitorMock = Mockito.mock(FileSystemMonitor.class);
	LiveClassLoader loaderMock = Mockito.mock(LiveClassLoader.class);
	
	@Test
	public void testLiveClassFactory() {    
		try {
			LiveClassFactory factory = new LiveClassFactory(monitorMock);
			Mockito.verify(monitorMock).setObserver(factory);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCreateObject() {
		try {
			Mockito
				.when(loaderMock.loadUpdatedClass(Mockito.any()))
				.then(new Answer<Class<?>>() {
				    @Override
				    public Class<?> answer(InvocationOnMock invocation) throws Throwable {
				      return ClassB.class;
				    }
				});
			
			ClassInstrumentation instrumentationMock = Mockito.mock(ClassInstrumentation.class);
			Mockito
			.when(instrumentationMock.getClassName())
			.then(new Answer<String>() {
			    @Override
			    public String answer(InvocationOnMock invocation) throws Throwable {
			      return ClassA.class.getName();
			    }
			});
			
			LiveClassFactory factory = new LiveClassFactory(monitorMock,loaderMock);
			ClassA a = factory.createObject(ClassA.class);
			
			factory.classFileUpdated(instrumentationMock);
			
			Mockito
				.verify(loaderMock)
				.loadUpdatedClass(Mockito.any());
			
			Assert.assertEquals("ClassB", a.getName());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
