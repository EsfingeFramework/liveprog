package org.esfinge.liveprog.exception;

/**
 * <p>
 * Exceção que é lançada pelo Builder na tentativa de construir uma nova fábrica de objetos de classes dinâmicas.
 * </p>
 * <p><i>
 * Exception that is thrown by the Builder in an attempt to build a new factory of LiveClass objects.
 * </i></p>
 */
@SuppressWarnings("serial")
public class LiveClassFactoryBuilderException extends LiveprogException
{
	/**
	 * <p>
	 * Constrói uma nova exceção do tipo LiveClassFactoryBuilderException.
	 * </p>
	 * <p><i>
	 * Constructs a new LiveClassFactoryBuilderException.
	 * </i></p>
	 * 
	 * @param message - mensagem descrevendo o motivo da exceção
	 * <br><i>the message describing the exception's cause</i>
	 */
	public LiveClassFactoryBuilderException(String message)
	{
		super(message);
	}
	
	/**
	 * <p>
	 * Constrói uma nova exceção do tipo LiveClassFactoryBuilderException.
	 * </p>
	 * <p><i>
	 * Constructs a new LiveClassFactoryBuilderException.
	 * </i></p>
	 * 
	 * @param message - mensagem descrevendo o motivo da exceção
	 * <br>the message describing the exception's cause<i></i>
	 * @param cause - exceção de origem que causou essa exceção
	 * <br><i>the original exception related to this exception</i> 
	 */
	public LiveClassFactoryBuilderException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
