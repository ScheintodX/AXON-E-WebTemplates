package de.axone.webtemplate.validator;


public abstract class AbstractValidator<T> implements Validator<T> {
	
	@Override
	public boolean isValid( T value ) {
		
		return ( validate( value ) == null );
	}

}
