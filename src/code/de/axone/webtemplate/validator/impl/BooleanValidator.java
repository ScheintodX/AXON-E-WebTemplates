package de.axone.webtemplate.validator.impl;


import de.axone.webtemplate.validator.AbstractValidator;

/**
 * Checks a given boolean for validity
 * 
 * @author flo
 */
public class BooleanValidator extends AbstractValidator<Integer> {
	
	private static final String NO_BOOLEAN = "VALIDATOR_NO_BOOLEAN";
	
	public BooleanValidator(){}

	@Override
	protected String check(Integer value) {
		
		if( value == null ) return null;
		
		if( value.intValue() > 1 || value.intValue() < 0 )
			return NO_BOOLEAN;
		
		return null;
	}

}
