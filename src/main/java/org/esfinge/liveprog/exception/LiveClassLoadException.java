package org.esfinge.liveprog.exception;

@SuppressWarnings("serial")
public class LiveClassLoadException extends Exception
{
	public LiveClassLoadException(String message)
	{
		super(message);
	}
	
	public LiveClassLoadException(String message, Throwable cause)
	{
		super(message,cause);
	}
}
