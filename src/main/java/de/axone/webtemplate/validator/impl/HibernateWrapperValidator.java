package de.axone.webtemplate.validator.impl;

import javax.annotation.Nullable;

import de.axone.webtemplate.form.Translator;
import de.axone.webtemplate.validator.AbstractValidator;

public class HibernateWrapperValidator extends AbstractValidator<Object> {

	@Override
	public String validate( Object value, @Nullable Translator t ) {
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
