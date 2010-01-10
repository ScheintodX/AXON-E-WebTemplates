package de.axone.webtemplate.validator.impl;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

@Test( groups="webtemplate.notnullvalidator" )
public class NotNullValidatorTest {

	public void TestValidator() throws Exception {
		
		NotNullValidator validator = new NotNullValidator();
		
		String testCoolString = "blah";
		assertTrue( validator.isValid( testCoolString ) );
		assertNull( validator.validate( testCoolString ) );
		
		String testEmptyString = "   ";
		assertFalse( validator.isValid( testEmptyString ) );
		assertNotNull( validator.validate( testEmptyString ) );
		assertEquals( validator.validate( testEmptyString ), "VALIDATOR_IS_EMPTY" );
		
		String testNoString = null;
		assertFalse( validator.isValid( testNoString ) );
		assertNotNull( validator.validate( testNoString ) );
		assertEquals( validator.validate( testNoString ), "VALIDATOR_IS_NULL" );
	}
}
