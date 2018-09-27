package org.esfinge.liveprog.exception;

/**
 * <p>
 * Exceção base para todas as outras exceções não-checadas lançadas pelo framework.
 * <p><i>
 * Parent exception of all others unchecked exceptions thrown by the framework.
 * </i>
 */
@SuppressWarnings("serial")
public abstract class LiveprogRuntimeException extends RuntimeException
{
	/**
	 * <p>
	 * Constrói uma nova exceção do tipo LiveprogRuntimeException.
	 * <p><i>
	 * Constructs a new LiveprogRuntimeException.
	 * </i>
	 * 
	 * @param message mensagem descrevendo o motivo da exceção
	 * <br><i>the message describing the exception's cause</i>
	 */
	public LiveprogRuntimeException(String message)
	{
		super("\n**** Esfinge Liveprog RuntimeException **** \n -> " + message);
	}
	
	/**
	 * <p>
	 * Constrói uma nova exceção do tipo LiveprogRuntimeException.
	 * <p><i>
	 * Constructs a new LiveprogRuntimeException.
	 * </i>
	 * 
	 * @param message mensagem descrevendo o motivo da exceção
	 * <br><i>the message describing the exception's cause</i>
	 * @param cause exceção de origem que causou essa exceção
	 * <br><i>the original exception related to this exception</i> 
	 */
	public LiveprogRuntimeException(String message, Throwable cause)
	{
		super("\n**** Esfinge Liveprog RuntimeException **** \n -> " + message, cause);
	}
}
