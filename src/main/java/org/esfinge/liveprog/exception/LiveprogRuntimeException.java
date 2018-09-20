package org.esfinge.liveprog.exception;

/**
 * <p>
 * Exce��o base para todas as outras exce��es n�o-checadas lan�adas pelo framework.
 * </p>
 * <p><i>
 * Parent exception of all others unchecked exceptions thrown by the framework.
 * </i></p>
 */
@SuppressWarnings("serial")
public abstract class LiveprogRuntimeException extends RuntimeException
{
	/**
	 * <p>
	 * Constr�i uma nova exce��o do tipo LiveprogRuntimeException.
	 * </p>
	 * <p><i>
	 * Constructs a new LiveprogRuntimeException.
	 * </i></p>
	 * 
	 * @param message - mensagem descrevendo o motivo da exce��o
	 * <br><i>the message describing the exception's cause</i>
	 */
	public LiveprogRuntimeException(String message)
	{
		super("\n**** Esfinge Liveprog RuntimeException **** \n -> " + message);
	}
	
	/**
	 * <p>
	 * Constr�i uma nova exce��o do tipo LiveprogRuntimeException.
	 * </p>
	 * <p><i>
	 * Constructs a new LiveprogRuntimeException.
	 * </i></p>
	 * 
	 * @param message - mensagem descrevendo o motivo da exce��o
	 * <br>the message describing the exception's cause<i></i>
	 * @param cause - exce��o de origem que causou essa exce��o
	 * <br><i>the original exception related to this exception</i> 
	 */
	public LiveprogRuntimeException(String message, Throwable cause)
	{
		super("\n****Esfinge Liveprog RuntimeException**** \n -> " + message, cause);
	}

}
