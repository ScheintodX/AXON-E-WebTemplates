package de.axone.webtemplate.validator.impl;

import javax.annotation.Nullable;

import de.axone.webtemplate.form.Translator;


public class VatIdValidator extends AbstractAlphaNumValidator {
	
	private static final String NO_VALID_POSTAL_CODE
			="VALIDATOR_NO_VALID_POSTAL_CODE";
	
	public VatIdValidator( String pattern ){
		super( pattern, false );
	}
		
	public VatIdValidator( String pattern, boolean ignoreWhitespace ){
		
		super( pattern, ignoreWhitespace );
	}
	
	@Override
	protected String error( String originalPattern, @Nullable Translator t ){
		return NO_VALID_POSTAL_CODE + ":" + examplify( originalPattern, t );
	}

}
