package org.esfinge.liveprog.exception;

/**
 * <p>
 * Exce��o que � lan�ada pelo classloader de classes din�micas.
 * </p>
 * <p><i>
 * Exception that is thrown by LiveClasses classloaders.
 * </i></p>
 */
@SuppressWarnings("serial")
public class LiveClassLoaderException extends LiveprogException
{
	/**
	 * <p>
	 * Constr�i uma nova exce��o do tipo LiveClassLoaderException.
	 * </p>
	 * <p><i>
	 * Constructs a new LiveClassLoaderException.
	 * </i></p>
	 * 
	 * @param message - mensagem descrevendo o motivo da exce��o
	 * <br><i>the message describing the exception's cause</i>
	 */
	public LiveClassLoaderException(String message)
	{
		super(message);
	}
	
	/**
	 * <p>
	 * Constr�i uma nova exce��o do tipo LiveClassLoaderException.
	 * </p>
	 * <p><i>
	 * Constructs a new LiveClassLoaderException.
	 * </i></p>
	 * 
	 * @param message - mensagem descrevendo o motivo da exce��o
	 * <br>the message describing the exception's cause<i></i>
	 * @param cause - exce��o de origem que causou essa exce��o
	 * <br><i>the original exception related to this exception</i> 
	 */
	public LiveClassLoaderException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
