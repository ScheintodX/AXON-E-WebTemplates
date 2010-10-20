package de.axone.webtemplate.validator.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.axone.webtemplate.validator.AbstractValidator;

public class PatternValidator extends AbstractValidator<String> {
	
	private final Pattern pattern;
	
	private static final String FAIL = "VALIDATOR_PATTERN_FAIL";
	
	private final String errorMessage;
	
	public PatternValidator( String regex, String errorMessage ){
		
		this( Pattern.compile( regex ), errorMessage );
	}
	
	public PatternValidator( Pattern pattern, String errorMessage ){
		
		this.pattern = pattern;
		this.errorMessage = errorMessage != null ? errorMessage : FAIL;
	}

	@Override
	public String validate( String value ) {
		
		if( value == null || value.length() == 0 ) return null;
		
		Matcher matcher = pattern.matcher( value );
		
		if( ! matcher.matches() )
			return errorMessage;
		
		return null;
	}

}
