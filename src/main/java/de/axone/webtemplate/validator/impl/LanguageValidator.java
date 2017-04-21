package de.axone.webtemplate.validator.impl;

import java.util.Locale;

import javax.annotation.Nullable;

import de.axone.webtemplate.form.Translator;
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
	public String validate( String value, @Nullable Translator t ) {
		
		if( value == null || value.length() == 0 ) return null;
		
		String[] l = Locale.getISOLanguages();
		
		for( String lang: l ){
			if( lang.equalsIgnoreCase( value ) ) return null;
		}
		
		return NO_LOCALE;
	}

}
