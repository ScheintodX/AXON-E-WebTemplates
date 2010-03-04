package de.axone.webtemplate.validator.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.axone.webtemplate.validator.Validator;

public class PostalcodeValidator implements Validator<String> {
	
	private static final String NO_VALID_POSTAL_CODE
			="VALIDATOR_NO_VALID_POSTAL_CODE";
	
	Pattern pattern;

	public PostalcodeValidator( String pattern ){
		
		if( pattern == null ) return;
		
		pattern = pattern.replaceAll( "a", "[a-zA-Z]" );
		pattern = pattern.replaceAll( "n", "[0-9]" );
		
		this.pattern = Pattern.compile( pattern );
	}
	
	@Override
	public String validate( String check ){
		
		if( check == null ) return null;
		
		Matcher matcher = pattern.matcher( check.toUpperCase() );
		if( ! matcher.matches() ) return NO_VALID_POSTAL_CODE;
		
		return null;
	}

	@Override
	public boolean isValid( String value ) {
		return validate( (String)value ) == null;
	}

}
