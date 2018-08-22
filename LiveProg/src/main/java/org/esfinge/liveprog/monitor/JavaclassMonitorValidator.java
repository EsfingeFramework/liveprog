package org.esfinge.liveprog.monitor;

import java.io.File;

import org.esfinge.liveprog.annotation.LiveClass;
import org.esfinge.liveprog.instrumentation.ClassInfo;
import org.esfinge.liveprog.instrumentation.InstrumentationService;

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
			ClassInfo classInfo = InstrumentationService.inspect(file);
			
			// 
			Class<?> clazz;
			
			// verifica se eh uma classe interna
			if ( classInfo.isInnerClass() )
				// obtem a classe raiz (principal)
				clazz = Class.forName(classInfo.getOuterClassName());
			else
				clazz = classInfo.getClazz();

			return ( clazz.isAnnotationPresent(LiveClass.class) );
//			return ( Utils.getFromCollection(classInfo.getAnnotationsInfo(), annotInfo -> annotInfo.getName().equals(LiveClass.class.getTypeName())) != null );
		}
		catch ( Exception e )
		{
			// TODO: debug..
			System.out.println("FILE VALIDATOR >> Falha em validar classe do arquivo: " + file.getAbsolutePath());
			e.printStackTrace();
			
			return ( false );
		}
	}
}
