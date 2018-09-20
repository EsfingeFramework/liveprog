package org.esfinge.liveprog.util;

/**
 * <p>
 * Interface para customizar o logger utilizado pelo framework.
 * </p>
 * <p><i>
 * Interface to be implemented by classes to customize the logger object to be used by the framework.
 * </i></p> 
 */
public interface ILiveClassLogger
{
	/**
	 * <p>
	 * Níveis de log permitidos.
	 * </p>
	 * <p><i>
	 * Logging levels available.
	 * </i></p>
	 */
	public enum Level { ERROR, WARNING, INFO, DEBUG };
	
	
	/**
	 * <p>
	 * Loga a exceção como uma mensagem de erro.
	 * </p>
	 * <p><i>
	 * Logs the throwable object as an error message.
	 * </i></p>
	 * 
	 * @param exception - exceção a ser logada
	 * <br><i>the exception object to be logged</i>
	 */
	public void logException(Throwable exception);
	
	/**
	 * <p>
	 * Loga a mensagem de erro.
	 * </p>
	 * <p><i>
	 * Logs the error message.
	 * </i></p>
	 * 
	 * @param message - mensagem a ser logada
	 * <br><i>the message to be logged</i>
	 */
	public void logError(String message);
	
	/**
	 * <p>
	 * Loga a mensagem de aviso.
	 * </p>
	 * <p><i>
	 * Logs the warning message.
	 * </i></p>
	 * 
	 * @param message - mensagem a ser logada
	 * <br><i>the message to be logged</i>
	 */
	public void logWarning(String message);
	
	/**
	 * <p>
	 * Loga a mensagem de informação.
	 * </p>
	 * <p><i>
	 * Logs the information message.
	 * </i></p>
	 * 
	 * @param message - mensagem a ser logada
	 * <br><i>the message to be logged</i>
	 */
	public void logInfo(String message);
	
	/**
	 * <p>
	 * Loga a mensagem de debug.
	 * </p>
	 * <p><i>
	 * Logs the debug message.
	 * </i></p>
	 * 
	 * @param message - mensagem a ser logada
	 * <br><i>the message to be logged</i>
	 */
	public void logDebug(String message);
	
	/**
	 * <p>
	 * Especifica o nível das mensagens a serem logadas
	 * </p>
	 * <p><i>
	 * Specifies the log level of the messages to be logged.
	 * </i></p>
	 * 
	 * @param level - nível das mensagens a serem logadas
	 * <br><i>the log level of the messages to be logged</i>
	 */
	public void setLevel(Level logLevel);
}
