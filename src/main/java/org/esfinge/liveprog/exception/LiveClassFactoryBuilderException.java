package org.esfinge.liveprog.exception;

/**
 * <p>
 * Exce��o que � lan�ada pelo Builder na tentativa de construir uma nova f�brica de objetos de classes din�micas.
 * <p><i>
 * Exception that is thrown by the Builder in an attempt to build a new factory of LiveClass objects.
 * </i>
 */
@SuppressWarnings("serial")
public class LiveClassFactoryBuilderException extends LiveprogException
{
	/**
	 * <p>
	 * Constr�i uma nova exce��o do tipo LiveClassFactoryBuilderException.
	 * <p><i>
	 * Constructs a new LiveClassFactoryBuilderException.
	 * </i>
	 * 
	 * @param message mensagem descrevendo o motivo da exce��o
	 * <br><i>the message describing the exception's cause</i>
	 */
	public LiveClassFactoryBuilderException(String message)
	{
		super(message);
	}
	
	/**
	 * <p>
	 * Constr�i uma nova exce��o do tipo LiveClassFactoryBuilderException.
	 * <p><i>
	 * Constructs a new LiveClassFactoryBuilderException.
	 * </i>
	 * 
	 * @param message mensagem descrevendo o motivo da exce��o
	 * <br><i>the message describing the exception's cause</i>
	 * @param cause exce��o de origem que causou essa exce��o
	 * <br><i>the original exception related to this exception</i> 
	 */
	public LiveClassFactoryBuilderException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
