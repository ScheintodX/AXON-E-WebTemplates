package de.axone.webtemplate.validator.impl;

import de.axone.webtemplate.form.Translator;


public class PostalcodeValidator extends AbstractAlphaNumValidator {
	
	private static final String NO_VALID_POSTAL_CODE
			="VALIDATOR_NO_VALID_POSTAL_CODE";
	
	public PostalcodeValidator( String pattern ){
		
		super( pattern, false );
	}
		
	public PostalcodeValidator( String pattern, boolean ignoreWhitespace ){
		
		super( pattern, ignoreWhitespace );
	}
	
	@Override
	protected String error( String originalPattern, Translator t ){
		return NO_VALID_POSTAL_CODE + ":" + examplify( originalPattern, t );
	}

}
