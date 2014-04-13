package de.axone.webtemplate.validator.impl;

import de.axone.webtemplate.validator.AbstractValidator;

/**
 * Checks if a value is not null and not empty
 * 
 * "empty" means: Contains only whitespace
 * 
 * @author flo
 */
public class NotNullValidator extends AbstractValidator<Object> {
	
	private static final String IS_NULL = "VALIDATOR_IS_NULL";
	private static final String IS_EMPTY = "VALIDATOR_IS_EMPTY";
	
	@Override
	public String validate( Object value ) {
		
		if( value == null )
			return IS_NULL;
		else if( value instanceof String && ((String)value).trim().length() == 0 )
			return IS_EMPTY;
		else
			return null;
	}

}
