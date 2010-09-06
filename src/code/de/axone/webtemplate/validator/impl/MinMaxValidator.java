package de.axone.webtemplate.validator.impl;

import de.axone.webtemplate.validator.AbstractValidator;

/**
 * Checks if a give string is not larger then a limit
 * 
 * @author flo
 */
public class MinMaxValidator extends AbstractValidator<Number> {
	
	private static final String TO_BIG = "VALIDATOR_TO_BIG";
	private static final String TO_SMALL = "VALIDATOR_TO_SMALL";
	
	private Number min, max;
	
	public MinMaxValidator( Number min, Number max ){
		
		this.min = min;
		this.max = max;
	}

	@Override
	protected String check( Number value ) {
		
		if( value == null ) return null;
			
		if( min != null && value.doubleValue() < min.doubleValue() )
			return TO_SMALL + ":" + value + ":" + min;
        			
		if( max != null && value.doubleValue() > max.doubleValue() )
			return TO_BIG + ":" + value + ":" + max;
		
		return null;
	}

}
