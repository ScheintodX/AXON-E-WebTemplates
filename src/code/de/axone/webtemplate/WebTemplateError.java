package de.axone.webtemplate;

import de.axone.web.encoding.HtmlEncoder;


public class WebTemplateError extends Error {

	public WebTemplateError( String message, Throwable cause ){
		super( HtmlEncoder.ENCODE( message ), cause );
	}
	public WebTemplateError( String message ){
		super( HtmlEncoder.ENCODE( message ) );
	}
	public WebTemplateError( Throwable cause ){
		super( cause );
	}
}
