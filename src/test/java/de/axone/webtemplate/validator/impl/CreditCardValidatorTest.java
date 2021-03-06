package de.axone.webtemplate.validator.impl;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

@Test( groups="webtemplate.notnullvalidator" )
public class CreditCardValidatorTest {

	public void testValidator() throws Exception {
		
		CreditCardNumberValidator validator = new CreditCardNumberValidator();
		
		String nullString = null;
		assertNull( validator.validate( nullString, null ) );
		
		String emptyString = "";
		assertNull( validator.validate( emptyString, null ) );
		
		String Visa				= "4111 1111 1111 1111";// (16 digits)
		String MasterCard		= "5500 0000 0000 0004";// (16 digits)
		String American_Express	= "3400 0000 0000 009";// (15 digits)
		String Diners_Club		= "3000 0000 0000 04";// (14 digits)
		String Carte_Blanche	= "3000 0000 0000 04";// (14 digits)
		String Discover			= "6011 0000 0000 0004";// (16 digits)
		String EnRoute			= "2014 0000 0000 009";// (15 digits)
		String JCB				= "3088 0000 0000 0009";// (16 digits)
		
		assertNull( validator.validate( Visa, null ) );
		assertNull( validator.validate( MasterCard, null ) );
		assertNull( validator.validate( American_Express, null ) );
		assertNull( validator.validate( Diners_Club, null ) );
		assertNull( validator.validate( Carte_Blanche, null ) );
		assertNull( validator.validate( Discover, null ) );
		assertNull( validator.validate( EnRoute, null ) );
		assertNull( validator.validate( JCB, null ) );
		
		String Visa_ERR =				"4111 1121 1111 1111";// (16 digits)
		String MasterCard_ERR =			"5500 0000 0100 0004";// (16 digits)
		String American_Express_ERR =	"3400 0000 0100 009";// (15 digits)
		String Diners_Club_ERR =		"3001 0000 0000 04";// (14 digits)
		String Carte_Blanche_ERR =		"3000 1000 0000 04";// (14 digits)
		String Discover_ERR =			"6011 0000 0000 1004";// (16 digits)
		String EnRoute_ERR =			"2014 0000 0010 009";// (15 digits)
		String JCB_ERR =				"3088 0000 0100 0009";// (16 digits)
		
		assertNotNull( validator.validate( Visa_ERR, null ) );
		assertNotNull( validator.validate( MasterCard_ERR, null ) );
		assertNotNull( validator.validate( American_Express_ERR, null ) );
		assertNotNull( validator.validate( Diners_Club_ERR, null ) );
		assertNotNull( validator.validate( Carte_Blanche_ERR, null ) );
		assertNotNull( validator.validate( Discover_ERR, null ) );
		assertNotNull( validator.validate( EnRoute_ERR, null ) );
		assertNotNull( validator.validate( JCB_ERR, null ) );
		
		
	}
}
