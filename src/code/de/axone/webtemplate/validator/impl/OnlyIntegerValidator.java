package de.axone.webtemplate.validator.impl;

import java.util.regex.Pattern;

public class OnlyIntegerValidator extends PatternValidator {
	
	private static final String NO_INTEGER = "VALIDATOR_NO_INTEGER";
	
	private static Pattern withoutWhitespacePattern = Pattern.compile( "[0-9]*" );
	private static Pattern includingWhitespacePattern = Pattern.compile( "[0-9 ]*" );

	public OnlyIntegerValidator( boolean includeWhitespace ){
		super( includeWhitespace ? includingWhitespacePattern : withoutWhitespacePattern );
	}
	
	@Override
	protected String check( String value ) {
		
		if( super.check( value ) != null )
			return NO_INTEGER;
		
		return null;
	}
}
