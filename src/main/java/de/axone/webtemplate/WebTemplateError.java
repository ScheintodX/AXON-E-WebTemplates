package de.axone.webtemplate;

import de.axone.web.encoding.Encoder_Html;


public class WebTemplateError extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8370058761102609537L;
	public WebTemplateError( String message, Throwable cause ){
		super( Encoder_Html.ENCODE( message ), cause );
	}
	public WebTemplateError( String message ){
		super( Encoder_Html.ENCODE( message ) );
	}
	public WebTemplateError( Throwable cause ){
		super( cause );
	}
}
