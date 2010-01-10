package de.axone.webtemplate.validator.impl;

import java.util.regex.Pattern;

/**
 * Checks a given e-mail address for validity
 * 
 * @author flo
 */
public class PhoneValidator extends PatternValidator {
	
	private static final String NO_PHONE = "VALIDATOR_NO_PHONE";
	
	private static final String patternString = "\\+?[-0-9 /\\(\\)]+";
	private static Pattern pattern = Pattern.compile( patternString );
	
	public PhoneValidator(){
		
		super( pattern );
	}

	@Override
	protected String check( String value ) {
		
		if( super.check( value ) != null )
			return NO_PHONE;
		
		return null;
	}

}
