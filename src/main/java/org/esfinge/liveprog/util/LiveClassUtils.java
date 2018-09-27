package org.esfinge.liveprog.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

import org.esfinge.liveprog.util.ILiveClassLogger.Level;

/**
 * <p>
 * Classe com métodos utilitários usado no framework.
 * </p>
 * <p><i>
 * Utilitary methods used in the framework.
 * </i></p>
 */
public class LiveClassUtils
{
	// logger utilizado no framework
	private static ILiveClassLogger LOGGER = new DefaultLogger("liveprog.log");
	
	
	/**
	 * <p>
	 * Obtém todos os campos/propriedades da classe informada.
	 * </p>
	 * <p><i>
	 * Gets all fields of the specified class.
	 * </i></p>
	 * 
	 * @param clazz classe cujos campos serão retornados
	 * <br><i>the class to retrieve its fields</i>
	 * @return todos os campos/propriedades da classe informada, inclusive os herdados das suas superclasses
	 * <br><i>all fields of the specified class, including the inherited ones</i>
	 */
	public static List<Field> getFields(Class<?> clazz)
	{
		List<Field> fields = new ArrayList<Field>();
		
		do
		{
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
			clazz = clazz.getSuperclass();			
		} while ( clazz != null );
		
		return ( fields );
	}

	/**
	 * <p>
	 * Obtém o campo/propriedade da classe com o nome especificado.
	 * </p>
	 * <p><i>
	 * Gets the specified field of a class.
	 * </i></p>
	 * 
	 * @param clazz classe cujo campo será retornado
	 * <br><i>the class to retrieve the desired field</i>
	 * @param name nome do campo desejado
	 * <br><i>the name of the desired field</i>
	 * @return o campo/propriedade da classe com o nome especificado, ou <i>null</i> se não for encontrado
	 * <br><i>the field of the class with the specified name, or null if not found</i>
	 */
	public static Field getField(Class<?> clazz, String name)
	{
		try
		{
			return ( getFields(clazz).stream()
						.filter(f -> f.getName().equals(name))
						.findFirst().get() );
		}
		catch (Exception e)
		{
			return ( null );
		}
	}

	/**
	 * <p>
	 * Obtém os campos/propriedades da classe que contenham a anotação especificada.
	 * </p>
	 * <p><i>
	 * Gets the annotated fields of a class.
	 * </i></p>
	 * 
	 * @param clazz classe cujos campos anotados serão retornados
	 * <br><i>the class to retrieve the annotated fields</i>
	 * @param annotationClazz classe da anotação
	 * <br><i>the annotation class</i>
	 * @return todos os campos/propriedades da classe que contenham a anotação informada, 
	 * inclusive os herdados das suas superclasses
	 * <br><i>all fields of the classe that contain the specified annotation, including the inherited ones</i>
	 */
	public static List<Field> getAnnotatedFields(Class<?> clazz, Class<? extends Annotation> annotationClazz)
	{
		try
		{
			return ( getFields(clazz).stream()
						.filter(f -> f.isAnnotationPresent(annotationClazz))
						.collect(Collectors.toList()));
		}
		catch (Exception e)
		{
			return ( Collections.emptyList() );
		}
	}
	
	/**
	 * <p>
	 * Obtém todos os métodos da classe informada e suas superclasses, excluídos os da classe Object.
	 * </p>
	 * <p><i>
	 * Gets all methods of the specified class e its superclasses, excluding the Object class methods.
	 * </i></p>
	 * 
	 * @param clazz classe cujos métodos serão retornados
	 * <br><i>the class to retrieve its methods</i>
	 * @return todos os métodos (públicos, privados, protegidos e de pacote) da classe informada, 
	 * inclusive os herdados das suas superclasses, excluídos os da classe Object
	 * <br><i>all methods (public, private, protected and package) of the specified class, 
	 * including the inherited ones, excluding the Object class methods</i>
	 */
	public static List<Method> getMethods(Class<?> clazz)
	{
		return ( getMethods(clazz, false) );
	}
	
	/**
	 * <p>
	 * Obtém todos os métodos da classe informada e suas superclasses.
	 * </p>
	 * <p><i>
	 * Gets all methods of the specified class e its superclasses.
	 * </i></p>
	 * 
	 * @param clazz classe cujos métodos serão retornados
	 * <br><i>the class to retrieve its methods</i>
	 * @param includeObjectMethods <i>true</i> para incluir os métodos da classe Object, <i>false</i> caso contrário
	 * <br><i>true to also include the Object class methods, false otherwise</i>
	 * @return todos os métodos (públicos, privados, protegidos e de pacote) da classe informada, 
	 * inclusive os herdados das suas superclasses, incluídos os da classe Object
	 * <br><i>all methods (public, private, protected and package) of the specified class, 
	 * including the inherited ones and those from the Object class</i>
	 */
	public static List<Method> getMethods(Class<?> clazz, boolean includeObjectMethods)
	{
		List<Method> methods = new ArrayList<Method>();		
		
		while ( clazz != null )
		{
			methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
			clazz = clazz.getSuperclass();
			
			if ( !includeObjectMethods && clazz.equals(Object.class) )
				break;
		}
		
		return ( methods );
	}
	
	
	/**
	 * <p>
	 * Obtém o método da classe com o nome e parâmetros especificados.
	 * </p>
	 * <p><i>
	 * Gets the specified method of a class.
	 * </i></p>
	 * 
	 * @param clazz classe cujo método será retornado
	 * <br><i>the class to retrieve the desired method</i>
	 * @param name nome do método desejado
	 * <br><i>the name of the desired method</i>
	 * @param parameterTypes tipos dos parâmetros do método desejado
	 * <br><i>the desired method parameter types</i>
	 * @return o método da classe com o nome especificado, ou <i>null</i> se não for encontrado
	 * <br><i>the method of the class with the specified name, or null if not found</i>
	 */
	public static Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes)
	{
		Method method = null;
		
		while ( clazz != null )
		{
			try
			{
				method = clazz.getDeclaredMethod(name, parameterTypes);
				break;
			}
			catch (NoSuchMethodException | SecurityException e)
			{
				clazz = clazz.getSuperclass();
			}
		}
		
		return ( method );
	}

	/**
	 * <p>
	 * Obtém os métodos da classe que contenham a anotação especificada.
	 * </p>
	 * <p><i>
	 * Gets the annotated methods of a class.
	 * </i></p>
	 * 
	 * @param clazz classe cujos métodos anotados serão retornados
	 * <br><i>the class to retrieve the annotated methods</i>
	 * @param annotationClazz classe da anotação
	 * <br><i>the annotation class</i>
	 * @return todos os métodos da classe que contenham a anotação informada, 
	 * inclusive os herdados das suas superclasses
	 * <br><i>all methods of the classe that contain the specified annotation, including the inherited ones</i>
	 */
	public static List<Method> getAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> annotationClazz)
	{
		try
		{
			return ( getMethods(clazz, true).stream()
						.filter(m -> m.isAnnotationPresent(annotationClazz))
						.collect(Collectors.toList()));
		}
		catch (Exception e)
		{
			return ( Collections.emptyList() );
		}
	}	
	
	/**
	 * <p>
	 * Adiciona os elementos especificados à uma coleção.
	 * </p>
	 * <p><i>
	 * Adds the specified elements to a collection.
	 * </i></p>
	 * 
	 * @param collection coleção onde os elementos serão adicionados
	 * <br><i>the collection object where the specified elements will be added</i>
	 * @param elements elementos a serem adicionados à coleção
	 * <br><i>the elements to be added to the collection</i>
	 */
	@SuppressWarnings("unchecked")
	public static <T> void addToCollection(Collection<T> collection, T... elements)
	{
		if ( elements != null )
			for ( T element : elements )
				collection.add(element);
	}
	
	/**
	 * <p>
	 * Obtém um elemento da coleção usando o filtro de seleção informado.
	 * </p>
	 * <p><i>
	 * Gets an element from the collection by using the specified selection filter.
	 * </i></p>
	 * 
	 * @param collection coleção contendo o elemento a ser retornado
	 * <br><i>the collection object containing the element to be selected</i>
	 * @param filter filtro para selecionar o elemento na coleção
	 * <br><i>the filter predicate to select the collection element</i>
	 * @return o elemento da coleção usando o filtro de seleção informado, ou o primeiro elemento
	 * caso o resultado do filtro encontre mais de um elemento na coleção, ou <i>null</i> caso nenhum elemento
	 * seja encontrado com o filtro informado
	 * <br><i>the element selected by the specified filter, or the first one if the specified filter
	 * returns more than one element, or null if the specified filter returns no elements from the collection</i>
	 */
	public static <T> T getFromCollection(Collection<T> collection, Predicate<T> filter)
	{
		return ( collection.stream().filter(filter).findFirst().orElse(null) );
	}
	
	/**
	 * <p>
	 * Obtém os elementos da coleção que correspondam ao filtro de seleção informado.
	 * </p>
	 * <p><i>
	 * Gets the elements of the collection that match the specified selection filter.
	 * </i></p>
	 * 
	 * @param collection coleção contendo os elementos a serem retornados
	 * <br><i>the collection object containing the elements to be selected</i>
	 * @param filter filtro para selecionar os elementos na coleção
	 * <br><i>the filter predicate to select the collection elements</i>
	 * @return os elementos da coleção que correspondam ao filtro de seleção informado
	 * <br><i>the elements of the collection that match the specified selection filter</i>
	 */
	public static <T> List<T> filterFromCollection(Collection<T> collection, Predicate<T> filter)
	{
		return ( collection.stream().filter(filter).collect(Collectors.toList()) );
	}
	
	/**
	 * <p>
	 * Especifica um logger customizado a ser utilizado pelo framework, substituindo o logger interno padrão.
	 * </p>
	 * <p><i>
	 * Sets a custom logger object to be used by the framework, replacing the internal default logger.
	 * </i></p>
	 * 
	 * @param logger logger customizado a ser utilizado pelo framework
	 * <br><i>the custom logger object to be used by the framework</i>
	 * @see org.esfinge.liveprog.util.ILiveClassLogger
	 */
	public static void setLogger(ILiveClassLogger logger)
	{
		if ( logger != null )
			LOGGER = logger;
	}
	
	/**
	 * <p>
	 * Especifica o arquivo onde as mensagens de log serão gravadas.
	 * <br>
	 * Por padrão, as mensagens de log são gravadas no arquivo <i>'liveprog.log'</i>.
	 * <br>
	 * Este método substitui qualquer logger customizado especificado anteriormente pelo logger interno padrão.
	 * </p>
	 * <p><i>
	 * Sets the log file where log records will be written to.
	 * <br>
	 * By default, log records are written to 'liveprog.log' file.
	 * <br>
	 * This method replaces any custom logger previously specified by an instance of the internal default logger.
	 * </i></p>
	 * 
	 * @param logFilePath nome e caminho para o arquivo de log 
	 * <br><i>filename and path of the log file</i>
	 */
	public static void setLoggerFile(String logFilePath)
	{
		LOGGER = new DefaultLogger(logFilePath);
	}
	
	/**
	 * <p>
	 * Suprime as mensagens de log para o console.
	 * Não tem validade se um logger customizado foi especificado.
	 * </p>
	 * <p><i>
	 * Suppresses the logging output to the console. 
	 * Does nothing if a custom logger was set.
	 * </i></p>
	 */
	public static void suppressConsoleLogs()
	{
		if ( LOGGER instanceof DefaultLogger )
			((DefaultLogger) LOGGER).suppressConsoleLogs();
	}
	
	/**
	 * <p>
	 * Habilita as mensagens de log para o console.
	 * Não tem validade se um logger customizado foi especificado.
	 * </p>
	 * <p><i>
	 * Enables the logging output to the console.
	 * Does nothing if a custom logger was set.
	 * </i></p>
	 */
	public static void enableConsoleLogs()
	{
		if ( LOGGER instanceof DefaultLogger )
			((DefaultLogger) LOGGER).enableConsoleLogs();
	}
	
	/**
	 * <i>Copiado de {@link ILiveClassLogger#logException(Throwable)}
	 * <br>
	 * Copied from {@link ILiveClassLogger#logException(Throwable)}
	 * </i>
	 * <p>
	 * Loga a exceção como uma mensagem de erro.
	 * </p>
	 * <p><i>
	 * Logs the throwable object as an error message.
	 * </i></p>
	 * 
	 * @param exception exceção a ser logada
	 * <br><i>the exception object to be logged</i>
	 */
	public static void logException(Throwable exception)
	{
		LOGGER.logException(exception);
	}
	
	/**
	 * <i>Copiado de {@link ILiveClassLogger#logError(String)}
	 * <br>
	 * Copied from {@link ILiveClassLogger#logError(String)}
	 * </i>
	 * <p>
	 * Loga a mensagem de erro.
	 * </p>
	 * <p><i>
	 * Logs the error message.
	 * </i></p>
	 * 
	 * @param message mensagem a ser logada
	 * <br><i>the message to be logged</i>
	 */
	public static void logError(String message)
	{
		LOGGER.logError(message);
	}
	
	/**
	 * <i>Copiado de {@link ILiveClassLogger#logWarning(String)}
	 * <br>
	 * Copied from {@link ILiveClassLogger#logWarning(String)}
	 * </i>
	 * <p>
	 * Loga a mensagem de aviso.
	 * </p>
	 * <p><i>
	 * Logs the warning message.
	 * </i></p>
	 * 
	 * @param message mensagem a ser logada
	 * <br><i>the message to be logged</i>
	 */
	public static void logWarning(String message)
	{
		LOGGER.logWarning(message);
	}

	/**
	 * <i>Copiado de {@link ILiveClassLogger#logInfo(String)}
	 * <br>
	 * Copied from {@link ILiveClassLogger#logInfo(String)}
	 * </i>
	 * <p>
	 * Loga a mensagem de informação.
	 * </p>
	 * <p><i>
	 * Logs the information message.
	 * </i></p>
	 * 
	 * @param message mensagem a ser logada
	 * <br><i>the message to be logged</i>
	 */
	public static void logInfo(String message)
	{
		LOGGER.logInfo(message);
	}

	/**
	 * <i>Copiado de {@link ILiveClassLogger#logDebug(String)}
	 * <br>
	 * Copied from {@link ILiveClassLogger#logDebug(String)}
	 * </i>
	 * <p>
	 * Loga a mensagem de debug.
	 * </p>
	 * <p><i>
	 * Logs the debug message.
	 * </i></p>
	 * 
	 * @param message mensagem a ser logada
	 * <br><i>the message to be logged</i>
	 */
	public static void logDebug(String message)
	{
		LOGGER.logDebug(message);
	}
	
	/**
	 * <i>Copiado de {@link ILiveClassLogger#setLevel(Level)}
	 * <br>
	 * Copied from {@link ILiveClassLogger#setLevel(Level)}
	 * </i>
	 * <p>
	 * Especifica o nível das mensagens a serem logadas
	 * </p>
	 * <p><i>
	 * Specifies the log level of the messages to be logged.
	 * </i></p>
	 * 
	 * @param logLevel nível das mensagens a serem logadas
	 * <br><i>the log level of the messages to be logged</i>
	 */
	public static void setLevel(Level logLevel)
	{
		LOGGER.setLevel(logLevel);
	}
	
	
	/**
	 * <p>
	 * Implementação interna do logger padrão.
	 * </p>
	 * <p><i>
	 * Internal implementation of the default logger.
	 * </i></p>
	 * 
	 * @see org.esfinge.liveprog.util.ILiveClassLogger
	 */
	private static class DefaultLogger implements ILiveClassLogger
	{
		// logger padrao JUL
		private Logger logger;
		
		// handler que logas as mensagens no console
		private Handler consoleHandler;

		
		/**
		 * <p>
		 * Constrói um novo logger que publica as mensagens de log no console, 
		 * configurando INFO como o nível padrão de log.
		 * </p>
		 * <p><i>
		 * Constructs a new logger that publishes log records to console, 
		 * setting INFO as the default log level.
		 * </i></p>
		 */
		DefaultLogger()
		{
			// obtem um logger anonimo
			this.logger = Logger.getAnonymousLogger();
			this.logger.setUseParentHandlers(false);
		
			// especifica o level minimo do handler do console
			this.consoleHandler = new ConsoleHandler();
			this.consoleHandler.setLevel(java.util.logging.Level.FINE);
			this.logger.addHandler(this.consoleHandler);
			
			this.setLevel(Level.INFO);
		}
		
		/**
		 * <p>
		 * Constrói um novo logger que publica as mensagens de log no arquivo especificado, 
		 * configurando INFO como o nível padrão de log.
		 * </p>
		 * <p><i>
		 * Constructs a new logger that publishes log records to the specified file,
		 * setting INFO as the default log level.
		 * </i></p>
		 * 
		 * @param logFilePath nome e caminho para o arquivo de log 
		 * <br><i>filename and path of the log file</i>
		 */
		DefaultLogger(String logFilePath)
		{
			this();
			
			try
			{
				FileHandler fileHandler = new FileHandler(logFilePath, true);
				fileHandler.setFormatter(new SimpleFormatter());
				this.logger.addHandler(fileHandler);
			}
			catch (SecurityException | IOException e)
			{
				// log: erro
				this.logError("Erro ao criar arquivo de log!");
				this.logException(e);
			}
		}
		
		/**
		 * <p>
		 * Suprime as mensagens de log para o console.
		 * </p>
		 * <p><i>
		 * Suppresses the logging output to the console.
		 * </i></p>
		 */
		public void suppressConsoleLogs()
		{
			this.logger.removeHandler(this.consoleHandler);
		}

		/**
		 * <p>
		 * Habilita as mensagens de log para o console.
		 * </p>
		 * <p><i>
		 * Enables the logging output to the console.
		 * </i></p>
		 */
		public void enableConsoleLogs()
		{
			this.logger.addHandler(this.consoleHandler);
		}
		
		@Override
		public void logException(Throwable exception)
		{
			// obtem o stack trace da excecao
			StringWriter writer = new StringWriter();
			exception.printStackTrace(new PrintWriter(writer));
			
			this.log(java.util.logging.Level.SEVERE, this.getCaller(Thread.currentThread().getStackTrace()), writer.toString());
		}

		@Override
		public void logError(String message)
		{
			this.log(java.util.logging.Level.SEVERE, this.getCaller(Thread.currentThread().getStackTrace()), message);
		}

		@Override
		public void logWarning(String message)
		{
			this.log(java.util.logging.Level.WARNING, this.getCaller(Thread.currentThread().getStackTrace()), message);
		}

		@Override
		public void logInfo(String message)
		{
			this.log(java.util.logging.Level.INFO, this.getCaller(Thread.currentThread().getStackTrace()), message);
		}

		@Override
		public void logDebug(String message)
		{
			this.log(java.util.logging.Level.FINE, this.getCaller(Thread.currentThread().getStackTrace()), message);
		}
		
		@Override
		public void setLevel(Level logLevel)
		{
			switch ( logLevel ) 
			{
				case ERROR:
					this.logger.setLevel(java.util.logging.Level.SEVERE);
					break;
					
				case WARNING:
					this.logger.setLevel(java.util.logging.Level.WARNING);
					break;
	
				case INFO:
					this.logger.setLevel(java.util.logging.Level.INFO);
					break;
	
				case DEBUG:
					this.logger.setLevel(java.util.logging.Level.FINE);
					break;
			}
		}
		
		/**
		 * <p>
		 * Loga a mensagem.
		 * </p>
		 * <p><i>
		 * Logs the message.
		 * </i></p>
		 * 
		 * @param level nível da mensagem a ser logada
		 * <br><i>the level of the message to be logged</i>
		 * @param caller informações do objeto que gerou a mensagem de log
		 * <br><i>information about the object that generated the log message</i>
		 * @param message mensagem a ser logada
		 * <br><i>the message to be logged</i>
		 */
		private void log(java.util.logging.Level level, LogCaller caller, String message)
		{
			this.logger.logp(level, caller.getClassName(), caller.getMethodName(), 
					String.format("[%d] %s\n", caller.getLineNumber(), message));
		}
		
		/**
		 * <p>
		 * Obtém informações do objeto que gerou a mensagem de log.
		 * </p>
		 * <p><i>
		 * Gets information about the object that generated the log message.
		 * </i></p>
		 * 
		 * @param trace pilha de chamadas do log [<i>Thread.currentThread().getStackTrace()</i>]
		 * <br><i>the log stack trace [Thread.currentThread().getStackTrace()]</i>
		 * @return as informações do objeto que invocou o log
		 * <br><i>information about the logger caller</i>
		 */
		private LogCaller getCaller(StackTraceElement[] trace)
		{
			StackTraceElement caller = null;
			
			// verifica o tamanho do stack trace
			if ( trace != null )
			{
				switch ( trace.length )
				{
					case 0:
						
					// Thread.currentThread()
					case 1:

					// DefaultLogger
					case 2:
						break;
						
					// Utils.log* ou a classe que chamou diretamente DefaultLogger
					case 3:
						caller = trace[2];
						break;
						
					// classe que chamou Utils.log*
					default:
						caller = trace[3];
				}
			}
			
			return ( new LogCaller(caller) );
		}
		
		
		/**
		 * <p>
		 * Informações do objeto que gerou a mensagem de log.
		 * </p>
		 * <p><i>
		 * Information about the object that generated the log message.
		 * </i></p>
		 */
		private class LogCaller
		{
			// elemento do stack trace que gerou o log
			private StackTraceElement caller;
			
			
			/**
			 * <p>
			 * Constrói um novo LogCaller.
			 * </p>
			 * <p><i>
			 * Constructs a new LogCaller.
			 * </i></p>
			 * 
			 * @param caller elemento do stack trace que gerou a mensagem de log
			 * <br><i>the stack trace element that generated the log message</i>
			 */
			LogCaller(StackTraceElement caller)
			{
				this.caller = caller;
			}
			
			/**
			 * <p>
			 * Obtém o nome da classe que gerou a mensagem de log.
			 * </p>
			 * <p><i>
			 * Gets the class name that generated the log message.
			 * </i></p>
			 * 
			 * @return o nome da classe que gerou a mensagem de log
			 * <br><i>the name of the class that generated the log message</i>
			 */
			String getClassName() 
			{
				if ( this.caller != null )
					return ( caller.getClassName() );
				
				return "";
			}
			
			/**
			 * <p>
			 * Obtém o nome do método onde a mensagem de log foi gerada.
			 * </p>
			 * <p><i>
			 * Gets the method name where the log message was generated.
			 * </i></p>
			 * 
			 * @return o nome do método onde a mensagem de log foi gerada
			 * <br><i>the name of the method where the log message was generated</i>
			 */
			String getMethodName()
			{
				if ( this.caller != null )
					return ( caller.getMethodName() );
				
				return "";
			}
			
			/**
			 * <p>
			 * Obtém a linha do código fonte onde a mensagem de log foi gerada.
			 * </p>
			 * <p><i>
			 * Gets the source-code line number where the log message was generated.
			 * </i></p>
			 * 
			 * @return a linha do código fonte onde a mensagem de log foi gerada
			 * <br><i>the line number on the source-code where the log message was generated</i>
			 */
			int getLineNumber()
			{
				if ( this.caller != null )
					return ( caller.getLineNumber() );
				
				return -1;
			}
		}
	}
}
