package org.esfinge.liveprog.exception;

/**
 * <p>
 * Exceção que é lançada quando uma classe não é compatível com as regras para ser considerada uma classe dinâmica.
 * <p><i>
 * Exception that is thrown when a class does not comply with the LiveClass's requirements.
 * </i>
 * 
 * @see org.esfinge.liveprog.instrumentation.InstrumentationHelper#checkValidLiveClass(Class)
 */
@SuppressWarnings("serial")
public class IncompatibleLiveClassException extends LiveprogException
{
	/**
	 * <p>
	 * Constrói uma nova exceção do tipo IncompatibleLiveClassException.
	 * <p><i>
	 * Constructs a new IncompatibleLiveClassException.
	 * </i>
	 * 
	 * @param message mensagem descrevendo o motivo da exceção
	 * <br><i>the message describing the exception's cause</i>
	 */
	public IncompatibleLiveClassException(String message)
	{
		super("\n****Esfinge Liveprog Exception**** \n -> " + message);
	}
	
	/**
	 * <p>
	 * Constrói uma nova exceção do tipo IncompatibleLiveClassException.
	 * <p><i>
	 * Constructs a new IncompatibleLiveClassException.
	 * </i>
	 * 
	 * @param message mensagem descrevendo o motivo da exceção
	 * <br><i>the message describing the exception's cause</i>
	 * @param cause exceção de origem que causou essa exceção
	 * <br><i>the original exception related to this exception</i> 
	 */
	public IncompatibleLiveClassException(String message, Throwable cause)
	{
		super("\n****Esfinge Liveprog Exception**** \n -> " + message, cause);
	}
}