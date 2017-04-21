package de.axone.webtemplate.validator.impl;

import javax.annotation.Nullable;

import de.axone.webtemplate.form.Translator;


public class TinValidator extends AbstractAlphaNumValidator {
	
	private static final String NO_VALID_TIN_CODE
			="VALIDATOR_NO_VALID_TIN_CODE";
	
	public TinValidator( String pattern ){
		
		super( pattern, false );
	}
		
	public TinValidator( String pattern, boolean ignoreWhitespace ){
		
		super( pattern, ignoreWhitespace );
	}
	
	@Override
	protected String error( String originalPattern, @Nullable Translator t ){
		return NO_VALID_TIN_CODE + ":" + examplify( originalPattern, t );
	}

}
