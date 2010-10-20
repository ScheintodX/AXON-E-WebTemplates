package de.axone.webtemplate.validator.impl;

import de.axone.webtemplate.validator.AbstractValidator;

/**
 * Checks if a give string is not larger then a limit
 * 
 * @author flo
 */
public class LengthValidator extends AbstractValidator<String> {
	
	private static final String LENGTH_MISSMATCH = "VALIDATOR_LENGTH_MISSMATCH";
	
	private int maxLength;
	
	public LengthValidator( int maxLength ){
		
		this.maxLength = maxLength;
	}

	@Override
	public String validate( String value ) {
		
		if( value == null ) return null;
			
		int actual = value.length();
			
		if( actual <= maxLength ) return null;
				
		return LENGTH_MISSMATCH + ":" + maxLength + ":" + actual;
	}
}
