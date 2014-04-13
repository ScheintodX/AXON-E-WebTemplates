package de.axone.webtemplate.function;

import de.axone.webtemplate.WebTemplateException;

public class FunctionException extends WebTemplateException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1553908495149078641L;
	public FunctionException(String message, Throwable cause) { super( message, cause ); }
	public FunctionException(String message) { super( message ); }
	public FunctionException(Throwable cause) { super( cause ); }

}
