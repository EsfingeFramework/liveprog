package org.esfinge.liveprog.exception;

@SuppressWarnings("serial")
public class IncompatibleLiveClassException extends Exception
{
	public IncompatibleLiveClassException(String message)
	{
		super(message);
	}
	
	public IncompatibleLiveClassException(String message, Throwable cause)
	{
		super(message,cause);
	}
}
