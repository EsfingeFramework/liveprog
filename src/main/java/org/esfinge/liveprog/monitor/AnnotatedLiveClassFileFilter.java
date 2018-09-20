package org.esfinge.liveprog.monitor;

import java.io.File;

import org.esfinge.liveprog.annotation.LiveClass;
import org.esfinge.liveprog.instrumentation.InstrumentationHelper;
import org.esfinge.liveprog.reflect.ClassInfo;
import org.esfinge.liveprog.util.Utils;

/**
 * <p>
 * Filtro que verifica se a classe contém a anotação {@link LiveClass}.
 * <p><i>
 * Filters classes annotated with {@link LiveClass}.
 * </i>
 * 
 * @see org.esfinge.liveprog.annotation.LiveClass
 * @see org.esfinge.liveprog.monitor.ILiveClassFileFilter
 */
public class AnnotatedLiveClassFileFilter  implements ILiveClassFileFilter
{
	@Override
	public boolean acceptFile(File file)
	{
		try
		{
			//
			ClassInfo classInfo = InstrumentationHelper.inspect(file);
			
			// 
			Class<?> clazz;
			
			// verifica se eh uma classe interna
			if ( classInfo.isInnerClass() )
				// obtem a classe raiz (principal)
				clazz = Class.forName(classInfo.getDeclaringClassName());
			else
				clazz = classInfo.getClazz();

			return ( clazz.isAnnotationPresent(LiveClass.class) );
//			return ( Utils.getFromCollection(classInfo.getAnnotationsInfo(), annotInfo -> annotInfo.getName().equals(LiveClass.class.getTypeName())) != null );
		}
		catch ( Exception e )
		{
			Utils.logDebug("Arquivo '" + file.getAbsolutePath() + "' nao contem uma classe valida");
			Utils.logException(e);
			
			return ( false );
		}
	}
}
