package org.esfinge.liveprog.exception;

/**
 * <p>
 * Exceção que é lançada pelo classloader de classes dinâmicas.
 * <p><i>
 * Exception that is thrown by LiveClasses classloaders.
 * </i>
 */
@SuppressWarnings("serial")
public class LiveClassLoaderException extends LiveprogException
{
	/**
	 * <p>
	 * Constrói uma nova exceção do tipo LiveClassLoaderException.
	 * <p><i>
	 * Constructs a new LiveClassLoaderException.
	 * </i>
	 * 
	 * @param message mensagem descrevendo o motivo da exceção
	 * <br><i>the message describing the exception's cause</i>
	 */
	public LiveClassLoaderException(String message)
	{
		super(message);
	}
	
	/**
	 * <p>
	 * Constrói uma nova exceção do tipo LiveClassLoaderException.
	 * <p><i>
	 * Constructs a new LiveClassLoaderException.
	 * </i>
	 * 
	 * @param message mensagem descrevendo o motivo da exceção
	 * <br><i>the message describing the exception's cause</i>
	 * @param cause exceção de origem que causou essa exceção
	 * <br><i>the original exception related to this exception</i> 
	 */
	public LiveClassLoaderException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
