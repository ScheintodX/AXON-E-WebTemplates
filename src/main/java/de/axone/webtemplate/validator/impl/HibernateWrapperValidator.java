package de.axone.webtemplate.validator.impl;

import de.axone.webtemplate.validator.AbstractValidator;

public class HibernateWrapperValidator extends AbstractValidator<Object> {

	@Override
	public String validate( Object value ) {
		throw new UnsupportedOperationException( "This is removed to remove dependency" );
	}
	
	/*
	private org.hibernate.validator.Validator<?> hibernateValidator;
	
	private static final String FAIL = "VALIDATOR_HIBERNATE_FAILED";
	
	public HibernateWrapperValidator( org.hibernate.validator.Validator<?> hibernateValidator ){
		
		this.hibernateValidator = hibernateValidator;
	}

	@Override
	public String validate( Object value ) {
		
		if( ! hibernateValidator.isValid( value ) )
			return FAIL;
		
		return null;
	}
	*/

}
