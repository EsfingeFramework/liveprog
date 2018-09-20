package org.esfinge.liveprog.exception;

/**
 * <p>
 * Exce��o que � lan�ada pela f�brica de objetos de classes din�micas.
 * <p><i>
 * Exception that is thrown by factories of LiveClass objects.
 * </i>
 */
@SuppressWarnings("serial")
public class LiveClassFactoryException extends LiveprogException
{
	/**
	 * <p>
	 * Constr�i uma nova exce��o do tipo LiveClassFactoryException.
	 * <p><i>
	 * Constructs a new LiveClassFactoryException.
	 * </i>
	 * 
	 * @param message mensagem descrevendo o motivo da exce��o
	 * <br><i>the message describing the exception's cause</i>
	 */
	public LiveClassFactoryException(String message)
	{
		super(message);
	}
	
	/**
	 * <p>
	 * Constr�i uma nova exce��o do tipo LiveClassFactoryException.
	 * <p><i>
	 * Constructs a new LiveClassFactoryException.
	 * </i>
	 * 
	 * @param message mensagem descrevendo o motivo da exce��o
	 * <br><i>the message describing the exception's cause</i>
	 * @param cause exce��o de origem que causou essa exce��o
	 * <br><i>the original exception related to this exception</i> 
	 */
	public LiveClassFactoryException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
