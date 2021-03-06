package de.axone.webtemplate.validator.impl;

import javax.annotation.Nullable;

import de.axone.webtemplate.form.FormValue;
import de.axone.webtemplate.form.Translator;
import de.axone.webtemplate.validator.AbstractValidator;

public class EqualsValidator extends AbstractValidator<String> {
	
	private static final String NO_MATCH = "VALIDATOR_NO_MATCH";
	
	private FormValue<?> other;
	
	public EqualsValidator( FormValue<?> other ){
		this.other = other;
	}

	@Override
	public String validate( String value, @Nullable Translator t ) {
		
		if( value == null && other.getPlainValue() == null ) return null;
		
		if( value == null ) return NO_MATCH;
		
		if( value.equals( other.getPlainValue() ) ) return null;
		
		return NO_MATCH;
	}

}
