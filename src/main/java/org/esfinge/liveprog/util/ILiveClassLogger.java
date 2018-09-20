package org.esfinge.liveprog.util;

/**
 * <p>
 * Interface para customizar o logger utilizado pelo framework.
 * <p><i>
 * Interface to be implemented by classes to customize the logger object to be used by the framework.
 * </i> 
 */
public interface ILiveClassLogger
{
	/**
	 * <p>
	 * Níveis de log disponíveis.
	 * <p><i>
	 * Logging levels available.
	 * </i>
	 */
	public enum Level { ERROR, WARNING, INFO, DEBUG };
	
	
	/**
	 * <p>
	 * Loga uma exceção como uma mensagem de erro.
	 * <p><i>
	 * Logs a throwable object as an error message.
	 * </i>
	 * 
	 * @param exception exceção a ser logada
	 * <br><i>the exception object to be logged</i>
	 */
	public void logException(Throwable exception);
	
	/**
	 * <p>
	 * Loga uma mensagem de erro.
	 * <p><i>
	 * Logs an error message.
	 * </i>
	 * 
	 * @param message mensagem a ser logada
	 * <br><i>the message to be logged</i>
	 */
	public void logError(String message);
	
	/**
	 * <p>
	 * Loga uma mensagem de aviso.
	 * <p><i>
	 * Logs a warning message.
	 * </i>
	 * 
	 * @param message mensagem a ser logada
	 * <br><i>the message to be logged</i>
	 */
	public void logWarning(String message);
	
	/**
	 * <p>
	 * Loga uma mensagem de informação.
	 * <p><i>
	 * Logs an information message.
	 * </i>
	 * 
	 * @param message mensagem a ser logada
	 * <br><i>the message to be logged</i>
	 */
	public void logInfo(String message);
	
	/**
	 * <p>
	 * Loga uma mensagem de debug.
	 * <p><i>
	 * Logs a debug message.
	 * </i>
	 * 
	 * @param message mensagem a ser logada
	 * <br><i>the message to be logged</i>
	 */
	public void logDebug(String message);
	
	/**
	 * <p>
	 * Especifica o nível das mensagens a serem logadas
	 * <p><i>
	 * Specifies the log level of the messages to be logged.
	 * </i>
	 * 
	 * @param logLevel nível das mensagens a serem logadas
	 * <br><i>the log level of the messages to be logged</i>
	 */
	public void setLevel(Level logLevel);
}
