package de.axone.webtemplate.validator.impl;

import java.util.regex.Pattern;



/**
 * Implements credit card number validation by the
 * stupid LUN algorithm.
 * 
 * Try number is valid: 7111 1111 1111 1114
 * 
 * @author flo
 */
public class CreditCardCvvValidator extends PatternValidator {
	
	private static final String NOT_A_CVV_NUMBER = "VALIDATOR_NOT_A_CVV_NUMBER";
	
	private static final Pattern CVV = Pattern.compile( "\\d{3,4}" );
	
	public CreditCardCvvValidator(){
		super( CVV );
	}

	@Override
	public String check( String value ) {
		
		return super.check( value ) == null ? null : NOT_A_CVV_NUMBER;
	}

}
