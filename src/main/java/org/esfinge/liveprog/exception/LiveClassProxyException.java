package org.esfinge.liveprog.exception;

/**
 * <p>
 * Exceção que é lançada pelo proxy de objetos de classes dinâmicas.
 * <p><i>
 * Exception that is thrown by proxies of LiveClasses objects.
 * </i>
 */
@SuppressWarnings("serial")
public class LiveClassProxyException extends LiveprogRuntimeException
{
	/**
	 * <p>
	 * Constrói uma nova exceção do tipo LiveClassProxyException.
	 * <p><i>
	 * Constructs a new LiveClassProxyException.
	 * </i>
	 * 
	 * @param message mensagem descrevendo o motivo da exceção
	 * <br><i>the message describing the exception's cause</i>
	 */
	public LiveClassProxyException(String message)
	{
		super(message);
	}
	
	/**
	 * <p>
	 * Constrói uma nova exceção do tipo LiveClassProxyException.
	 * <p><i>
	 * Constructs a new LiveClassProxyException.
	 * </i>
	 * 
	 * @param message mensagem descrevendo o motivo da exceção
	 * <br><i>the message describing the exception's cause</i>
	 * @param cause exceção de origem que causou essa exceção
	 * <br><i>the original exception related to this exception</i> 
	 */
	public LiveClassProxyException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
