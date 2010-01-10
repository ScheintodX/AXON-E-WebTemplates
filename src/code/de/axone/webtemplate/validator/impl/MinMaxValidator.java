package de.axone.webtemplate.validator.impl;

import de.axone.webtemplate.validator.AbstractValidator;

/**
 * Checks if a give string is not larger then a limit
 * 
 * @author flo
 */
public class MinMaxValidator extends AbstractValidator<Integer> {
	
	private static final String TO_BIG = "VALIDATOR_TO_BIG";
	private static final String TO_SMALL = "VALIDATOR_TO_SMALL";
	
	private Integer min, max;
	
	public MinMaxValidator( Integer min, Integer max ){
		
		this.min = min;
		this.max = max;
	}

	@Override
	protected String check( Integer value ) {
		
		if( value == null ) return null;
			
		if( min != null && value < min )
			return TO_SMALL + ":" + value.intValue() + ":" + min;
        			
		if( max != null && value > max )
			return TO_BIG + ":" + value.intValue() + ":" + max;
		
		return null;
	}

}
