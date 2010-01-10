package de.axone.webtemplate.function;

import de.axone.webtemplate.WebTemplateException;

public class FunctionException extends WebTemplateException {

	public FunctionException(String message, Throwable cause) { super( message, cause ); }
	public FunctionException(String message) { super( message ); }
	public FunctionException(Throwable cause) { super( cause ); }

}
