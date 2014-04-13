package de.axone.webtemplate.validator.impl;


public class PostalcodeValidator_Empty extends PostalcodeValidator {
	
	public PostalcodeValidator_Empty(){
		super( null );
	}
	@Override
	public boolean isValid( String value ) {
		return true;
	}

	@Override
	public String validate( String value ) {
		return null;
	}

}
