package org.esfinge.liveprog.exception;

/**
 * <p>
 * Exceção base para todas as outras exceções checadas lançadas pelo framework.
 * <p><i>
 * Parent exception of all others checked exceptions thrown by the framework.
 * </i>
 */
@SuppressWarnings("serial")
public abstract class LiveprogException extends Exception
{
	/**
	 * <p>
	 * Constrói uma nova exceção do tipo LiveprogException.
	 * <p><i>
	 * Constructs a new LiveprogException.
	 * </i>
	 * 
	 * @param message mensagem descrevendo o motivo da exceção
	 * <br><i>the message describing the exception's cause</i>
	 */
	public LiveprogException(String message)
	{
		super("\n**** Esfinge Liveprog Exception **** \n -> " + message);
	}
	
	/**
	 * <p>
	 * Constrói uma nova exceção do tipo LiveprogException.
	 * <p><i>
	 * Constructs a new LiveprogException.
	 * </i>
	 * 
	 * @param message mensagem descrevendo o motivo da exceção
	 * <br><i>the message describing the exception's cause</i>
	 * @param cause exceção de origem que causou essa exceção
	 * <br><i>the original exception related to this exception</i> 
	 */
	public LiveprogException(String message, Throwable cause)
	{
		super("\n**** Esfinge Liveprog Exception **** \n -> " + message, cause);
	}
}
