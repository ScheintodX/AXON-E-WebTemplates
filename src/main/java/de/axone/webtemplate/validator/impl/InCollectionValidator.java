package de.axone.webtemplate.validator.impl;

import java.util.Collection;

import de.axone.webtemplate.validator.AbstractValidator;

/**
 * Checks if a give string is not larger then a limit
 * 
 * @author flo
 * @param <T> value type
 */
public class InCollectionValidator<T> extends AbstractValidator<T> {
	
	private static final String NOT_IN_LIST = "VALIDATOR_NOT_IN_LIST";
	
	Collection<T> list;
	
	public InCollectionValidator( Collection<T> list ){
		
		this.list = list;
	}

	@Override
	public String validate( T value ) {
		
		if( value == null ) return null;
		
		if( list.contains( value ) ) return null;
		
		return NOT_IN_LIST;
	}
}
