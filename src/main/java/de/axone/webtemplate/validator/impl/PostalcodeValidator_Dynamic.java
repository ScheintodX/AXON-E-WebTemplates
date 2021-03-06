package de.axone.webtemplate.validator.impl;

import javax.annotation.Nullable;

import de.axone.webtemplate.form.Translator;
import de.axone.webtemplate.validator.Validator;

public class PostalcodeValidator_Dynamic implements Validator<String> {
	
	private static final String NO_COUNTRY_EXC 
			= "No Country set in validator";
	
	private CountryProvider countryProvider;
	public PostalcodeValidator_Dynamic( CountryProvider countryProvider ){
		this.countryProvider = countryProvider;
	}
	
	@Override
	public boolean isValid( String value ) {
		
		return validate( value, null ) == null;
	}

	@Override
	public String validate( String value, @Nullable Translator t ) {
		
		String iso2 = countryProvider.getCode();
		if( iso2 == null ) return null; // No country -> allways valid
		
		PostalcodeValidator validator = 
				PostalcodeValidatorFactory.validatorFor( iso2 );
		
		if( validator == null ) 
			throw new IllegalArgumentException( NO_COUNTRY_EXC );
		
		return validator.validate( value, t );
	}

	public static interface CountryProvider {
		public String getCode();
	}
}
