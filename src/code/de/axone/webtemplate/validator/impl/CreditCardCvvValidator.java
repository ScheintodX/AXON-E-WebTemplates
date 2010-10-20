package de.axone.webtemplate.validator.impl;

import java.util.regex.Pattern;



/**
 * Implements credit card validatation number validation
 * 
 * All integers with 3-4 numbers are valid
 * 
 * Could be extended so that the number-count matches the credit-card requirements
 * 
 * @author flo
 */
public class CreditCardCvvValidator extends PatternValidator {
	
	private static final String NOT_A_CVV_NUMBER = "VALIDATOR_NOT_A_CVV_NUMBER";
	
	private static final Pattern CVV = Pattern.compile( "\\d{3,4}" );
	
	public CreditCardCvvValidator(){
		super( CVV, NOT_A_CVV_NUMBER );
	}
}
