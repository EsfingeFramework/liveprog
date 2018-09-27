package org.esfinge.liveprog.exception;

/**
 * <p>
 * Exce��o base para todas as outras exce��es checadas lan�adas pelo framework.
 * <p><i>
 * Parent exception of all others checked exceptions thrown by the framework.
 * </i>
 */
@SuppressWarnings("serial")
public abstract class LiveprogException extends Exception
{
	/**
	 * <p>
	 * Constr�i uma nova exce��o do tipo LiveprogException.
	 * <p><i>
	 * Constructs a new LiveprogException.
	 * </i>
	 * 
	 * @param message mensagem descrevendo o motivo da exce��o
	 * <br><i>the message describing the exception's cause</i>
	 */
	public LiveprogException(String message)
	{
		super("\n**** Esfinge Liveprog Exception **** \n -> " + message);
	}
	
	/**
	 * <p>
	 * Constr�i uma nova exce��o do tipo LiveprogException.
	 * <p><i>
	 * Constructs a new LiveprogException.
	 * </i>
	 * 
	 * @param message mensagem descrevendo o motivo da exce��o
	 * <br><i>the message describing the exception's cause</i>
	 * @param cause exce��o de origem que causou essa exce��o
	 * <br><i>the original exception related to this exception</i> 
	 */
	public LiveprogException(String message, Throwable cause)
	{
		super("\n**** Esfinge Liveprog Exception **** \n -> " + message, cause);
	}
}
