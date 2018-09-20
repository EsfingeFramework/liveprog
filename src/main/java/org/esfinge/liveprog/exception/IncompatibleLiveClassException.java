package org.esfinge.liveprog.exception;

/**
 * <p>
 * Exce��o que � lan�ada quando uma classe n�o � compat�vel com as regras para ser considerada uma classe din�mica.
 * </p>
 * <p><i>
 * Exception that is thrown when a class does not comply with the LiveClass's requirements.
 * </i></p>
 * 
 * @see org.esfinge.liveprog.instrumentation.InstrumentationHelper#checkValidLiveClass(Class)
 */
@SuppressWarnings("serial")
public class IncompatibleLiveClassException extends LiveprogException
{
	/**
	 * <p>
	 * Constr�i uma nova exce��o do tipo IncompatibleLiveClassException.
	 * </p>
	 * <p><i>
	 * Constructs a new IncompatibleLiveClassException.
	 * </i></p>
	 * 
	 * @param message - mensagem descrevendo o motivo da exce��o
	 * <br><i>the message describing the exception's cause</i>
	 */
	public IncompatibleLiveClassException(String message)
	{
		super("\n****Esfinge Liveprog Exception**** \n -> " + message);
	}
	
	/**
	 * <p>
	 * Constr�i uma nova exce��o do tipo IncompatibleLiveClassException.
	 * </p>
	 * <p><i>
	 * Constructs a new IncompatibleLiveClassException.
	 * </i></p>
	 * 
	 * @param message - mensagem descrevendo o motivo da exce��o
	 * <br>the message describing the exception's cause<i></i>
	 * @param cause - exce��o de origem que causou essa exce��o
	 * <br><i>the original exception related to this exception</i> 
	 */
	public IncompatibleLiveClassException(String message, Throwable cause)
	{
		super("\n****Esfinge Liveprog Exception**** \n -> " + message, cause);
	}
}