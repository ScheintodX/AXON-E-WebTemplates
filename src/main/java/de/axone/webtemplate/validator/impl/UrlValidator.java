package de.axone.webtemplate.validator.impl;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Nullable;

import de.axone.webtemplate.form.Translator;
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
	public String validate( String url, @Nullable Translator t ) {
		
		if( url == null || url.length() == 0 ) return null;
		
		try{
			new URL( url ); //<-- throws Exception if URL is wrong
		}catch( MalformedURLException e ){
			return NO_URL+"["+e.getMessage()+"]";
		}
		
		return null;
	}

}
