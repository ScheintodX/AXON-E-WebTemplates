package de.axone.webtemplate.validator.impl;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

@Test( groups="webtemplate.lengthvalidator" )
public class LengthValidatorTest {

	public void testValidator() throws Exception {
		
		LengthValidator validator = new LengthValidator( 5 );
		
		String testCoolString = "12345";
		assertTrue( validator.isValid( testCoolString ) );
		assertNull( validator.validate( testCoolString ) );
		
		String testLongString = "123456";
		assertFalse( validator.isValid( testLongString ) );
		assertNotNull( validator.validate( testLongString ) );
		assertEquals( validator.validate( testLongString ), "VALIDATOR_LENGTH_MISSMATCH:5:6" );
		
		String testShortString = "";
		assertTrue( validator.isValid( testShortString ) );
		assertNull( validator.validate( testShortString ) );
		
		String testNoString = null;
		assertTrue( validator.isValid( testNoString ) );
		assertNull( validator.validate( testNoString ) );
	}
}
