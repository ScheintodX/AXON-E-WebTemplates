package de.axone.webtemplate.validator.impl;

import javax.annotation.Nullable;

import de.axone.webtemplate.form.Translator;


public class PostalcodeValidator_Empty extends PostalcodeValidator {
	
	public PostalcodeValidator_Empty(){
		super( null );
	}
	@Override
	public boolean isValid( String value ) {
		return true;
	}

	@Override
	public String validate( String value, @Nullable Translator t ) {
		return null;
	}

}
