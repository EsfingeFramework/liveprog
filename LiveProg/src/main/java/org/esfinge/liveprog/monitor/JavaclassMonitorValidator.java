package org.esfinge.liveprog.monitor;

import java.io.File;

import org.esfinge.liveprog.annotation.LiveClass;
import org.esfinge.liveprog.util.ClassInstrumentation;

/**
 * Valida que o arquivo seja uma classe Java anotada como uma classe dinamica,
 * que pode ser atualizada em tempo de execucao.
 * 
 * @see org.esfinge.liveprog.annotation.LiveClass
 */
public class JavaclassMonitorValidator  implements IMonitorFileValidator
{

	@Override
	public boolean isValid(File file)
	{
		try
		{
			//
			ClassInstrumentation classInstr = new ClassInstrumentation(file);
			
			// verifica se a classe esta anotada como uma classe dinamica
			return ( classInstr.getClazz().isAnnotationPresent(LiveClass.class) );
		}
		catch ( Exception e )
		{
			// TODO: debug
			e.printStackTrace();
			
			return ( false );
		}
	}
}
