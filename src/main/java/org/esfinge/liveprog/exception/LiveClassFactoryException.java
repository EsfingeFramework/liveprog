package org.esfinge.liveprog.exception;

/**
 * <p>
 * Exceção que é lançada pela fábrica de objetos de classes dinâmicas.
 * <p><i>
 * Exception that is thrown by factories of LiveClass objects.
 * </i>
 */
@SuppressWarnings("serial")
public class LiveClassFactoryException extends LiveprogException
{
	/**
	 * <p>
	 * Constrói uma nova exceção do tipo LiveClassFactoryException.
	 * <p><i>
	 * Constructs a new LiveClassFactoryException.
	 * </i>
	 * 
	 * @param message mensagem descrevendo o motivo da exceção
	 * <br><i>the message describing the exception's cause</i>
	 */
	public LiveClassFactoryException(String message)
	{
		super(message);
	}
	
	/**
	 * <p>
	 * Constrói uma nova exceção do tipo LiveClassFactoryException.
	 * <p><i>
	 * Constructs a new LiveClassFactoryException.
	 * </i>
	 * 
	 * @param message mensagem descrevendo o motivo da exceção
	 * <br><i>the message describing the exception's cause</i>
	 * @param cause exceção de origem que causou essa exceção
	 * <br><i>the original exception related to this exception</i> 
	 */
	public LiveClassFactoryException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
