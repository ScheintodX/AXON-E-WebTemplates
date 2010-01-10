package de.axone.webtemplate.converter;

import de.axone.webtemplate.WebTemplateException;

public class ConverterException extends WebTemplateException {

	public ConverterException(String message, Throwable cause) {
		super( message, cause );
	}

	public ConverterException(String message) {
		super( message );
	}

	public ConverterException(Throwable cause) {
		super( cause );
	}

}
