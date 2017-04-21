package de.axone.webtemplate.validator.impl;

import java.util.Locale;

import javax.annotation.Nullable;

import de.axone.webtemplate.form.Translator;
import de.axone.webtemplate.validator.AbstractValidator;

/**
 * Checks a given country for validity
 * 
 * @author flo
 */
public class CountryValidator extends AbstractValidator<String> {
	
	private static final String NO_COUNTRY = "VALIDATOR_NO_COUNTRY";
	
	public CountryValidator(){}

	@Override
	public String validate( String value, @Nullable Translator t ) {
		
		if( value == null || value.length() == 0 ) return null;
		
		String[] c = Locale.getISOCountries();
		for( String country : c ){
			if( country.equalsIgnoreCase( value ) ) return null;
		}
		
		return NO_COUNTRY;
	}

}
