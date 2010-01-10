package de.axone.webtemplate.validator.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.axone.webtemplate.validator.AbstractValidator;

public class PatternValidator extends AbstractValidator<String> {
	
	private Pattern pattern;
	
	private static final String FAIL = "VALIDATOR_PATTERN_FAIL";
	
	public PatternValidator( String regex ){
		
		this( Pattern.compile( regex ) );
	}
	
	public PatternValidator( Pattern pattern ){
		
		this.pattern = pattern;
	}

	@Override
	protected String check( String value ) {
		
		if( value == null || value.length() == 0 ) return null;
		
		Matcher matcher = pattern.matcher( value );
		
		if( ! matcher.matches() )
			return FAIL;
		
		return null;
	}

}
