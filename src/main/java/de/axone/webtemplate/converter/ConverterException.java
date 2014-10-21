package de.axone.webtemplate.converter;

import de.axone.webtemplate.WebTemplate.WebTemplateInfo;
import de.axone.webtemplate.WebTemplateException;

public class ConverterException extends WebTemplateException implements InformativeException {
	
	private WebTemplateInfo info;

	private static final long serialVersionUID = 8610089703699486191L;

	public ConverterException( String message, Throwable cause ) {
		super( message, cause );
	}

	public ConverterException( String message ) {
		super( message );
	}

	public ConverterException( Throwable cause ) {
		super( cause );
	}
	
	public void setInfo( WebTemplateInfo info ){
		this.info = info;
	}

	@Override
	public WebTemplateInfo getInfo() {
		return info;
	}

}
