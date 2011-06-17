package de.axone.webtemplate.validator.impl;

import java.net.MalformedURLException;
import java.net.URL;

import de.axone.webtemplate.validator.AbstractValidator;

/**
 * Checks a given country for validity
 * 
 * @author hexxter
 */
public class UrlValidator extends AbstractValidator<String> {
	
	private static final String NO_URL = "VALIDATOR_NO_URL";
	
	public UrlValidator(){}

	@Override
	public String validate( String url ) {
		
		if( url == null || url.length() == 0 ) return null;
		
		try{
			@SuppressWarnings( "unused" )
			URL u = new URL( url );
		}catch( MalformedURLException e ){
			return NO_URL+"["+e.getMessage()+"]";
		}
		
		return null;
	}

}
