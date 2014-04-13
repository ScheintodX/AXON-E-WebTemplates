package de.axone.webtemplate.validator.impl;

import de.axone.webtemplate.validator.AbstractValidator;


/**
 * Implements credit card number validation by the
 * stupid lun algorithm.
 * 
 * Try number is valid: 7111 1111 1111 1114
 * 
 * @author flo
 */
public class CreditCardNumberValidator extends AbstractValidator<String> {
	
	private static final String NOT_A_NUMBER = "VALIDATOR_NOT_A_NUMBER";
	private static final String NOT_A_CC_NUMBER = "VALIDATOR_NOT_A_CC_NUMBER";

	private static final int lookupMulSum[] = { 0, 2, 4, 6, 8, 1, 3, 5, 7, 9 };
	

	@Override
	public String validate( String value ) {
		
		// Empty values are valid
		if( value == null || value.length() == 0 ) return null;
		
		//Remove whitespace
		String number = value.replaceAll( "\\s", "" );
		
		// Traverse from right to left
		// to calculate Luhn Sum in one loop
		int sum = 0;
		boolean alt = false;
		for( int i = number.length()-1; i >= 0; i-- ){
			
			char c = number.charAt( i );
			
			int num = c - '0';
			
			// Check if every char is a number
			if( num < 0 || num > 10 )
				return NOT_A_NUMBER + "," + c;
			
			// Multiply by two and build sum of digits
			if( alt )
				num = lookupMulSum[ num ];
			
			alt = !alt;
			
			sum += num;
		}
		
		// Valid cc card numbers have 
		// this sum be dividable by 10
		if( sum % 10 == 0 ){
			return null;
		} else {
			return NOT_A_CC_NUMBER;
		}
	}


}
