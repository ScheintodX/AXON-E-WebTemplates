package de.axone.webtemplate.validator.impl;

import java.util.Locale;

import de.axone.webtemplate.validator.AbstractValidator;

/**
 * Checks a given language for validity
 * 
 * @author hexxter
 */
public class LanguageValidator extends AbstractValidator<String> {
	
	private static final String NO_LOCALE = "VALIDATOR_NO_LOCALE";
	
	public LanguageValidator(){}

	@Override
	protected String check( String value ) {
		
		if( value == null || value.length() == 0 ) return null;
		
		String[] l = Locale.getISOLanguages();
		
		for( String lang: l ){
			if( lang.equalsIgnoreCase( value ) ) return null;
		}
		
		return NO_LOCALE;
	}

}
