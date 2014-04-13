package de.axone.webtemplate.validator.impl;


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
	protected String error( String originalPattern ){
		return NO_VALID_POSTAL_CODE + ":" + examplify( originalPattern );
	}

}
