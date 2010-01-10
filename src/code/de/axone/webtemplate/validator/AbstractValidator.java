package de.axone.webtemplate.validator;


public abstract class AbstractValidator<T> implements Validator<T> {
	
	protected abstract String check( T value );

	@Override
	public String validate( T value ) {
		
		return check( value );
	}

	@Override
	public boolean isValid( T value ) {
		
		return ( check( value ) == null );
	}

}
