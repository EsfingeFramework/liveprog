package org.esfinge.liveprog.exception;

/**
 * <p>
 * Exce��o que � lan�ada pelo proxy de objetos de classes din�micas.
 * </p>
 * <p><i>
 * Exception that is thrown by proxies of LiveClasses objects.
 * </i></p>
 */
@SuppressWarnings("serial")
public class LiveClassProxyException extends LiveprogRuntimeException
{
	/**
	 * <p>
	 * Constr�i uma nova exce��o do tipo LiveClassProxyException.
	 * </p>
	 * <p><i>
	 * Constructs a new LiveClassProxyException.
	 * </i></p>
	 * 
	 * @param message - mensagem descrevendo o motivo da exce��o
	 * <br><i>the message describing the exception's cause</i>
	 */
	public LiveClassProxyException(String message)
	{
		super(message);
	}
	
	/**
	 * <p>
	 * Constr�i uma nova exce��o do tipo LiveClassProxyException.
	 * </p>
	 * <p><i>
	 * Constructs a new LiveClassProxyException.
	 * </i></p>
	 * 
	 * @param message - mensagem descrevendo o motivo da exce��o
	 * <br>the message describing the exception's cause<i></i>
	 * @param cause - exce��o de origem que causou essa exce��o
	 * <br><i>the original exception related to this exception</i> 
	 */
	public LiveClassProxyException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
