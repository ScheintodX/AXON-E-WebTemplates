package de.axone.webtemplate;

import de.axone.web.encoding.HtmlEncoder;


public class WebTemplateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4095990154660635781L;
	public WebTemplateException( String message, Throwable cause ){
		super( HtmlEncoder.ENCODE( message ), cause );
	}
	public WebTemplateException( String message ){
		super( HtmlEncoder.ENCODE( message ) );
	}
	public WebTemplateException( Throwable cause ){
		super( cause );
	}
}