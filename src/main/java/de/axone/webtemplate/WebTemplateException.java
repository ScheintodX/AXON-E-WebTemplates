package de.axone.webtemplate;

import de.axone.web.encoding.Encoder_Html;


public class WebTemplateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4095990154660635781L;
	public WebTemplateException( String message, Throwable cause ){
		super( Encoder_Html.ENCODE( message ), cause );
	}
	public WebTemplateException( String message ){
		super( Encoder_Html.ENCODE( message ) );
	}
	public WebTemplateException( Throwable cause ){
		super( cause );
	}
}
