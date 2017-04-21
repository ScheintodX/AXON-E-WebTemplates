package de.axone.webtemplate.validator.impl;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

@Test( groups="webtemplate.notnullvalidator" )
public class NotNullValidatorTest {

	public void testValidator() throws Exception {
		
		NotNullValidator validator = new NotNullValidator();
		
		String testCoolString = "blah";
		assertTrue( validator.isValid( testCoolString ) );
		assertNull( validator.validate( testCoolString, null ) );
		
		String testEmptyString = "   ";
		assertFalse( validator.isValid( testEmptyString ) );
		assertNotNull( validator.validate( testEmptyString, null ) );
		assertEquals( validator.validate( testEmptyString, null ), "VALIDATOR_IS_EMPTY" );
		
		String testNoString = null;
		assertFalse( validator.isValid( testNoString ) );
		assertNotNull( validator.validate( testNoString, null ) );
		assertEquals( validator.validate( testNoString, null ), "VALIDATOR_IS_NULL" );
	}
}
