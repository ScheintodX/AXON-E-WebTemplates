package de.axone.webtemplate.validator;


public interface Validator<T> {
	
	public boolean isValid( T value );
	
	public String validate( T value );
}
