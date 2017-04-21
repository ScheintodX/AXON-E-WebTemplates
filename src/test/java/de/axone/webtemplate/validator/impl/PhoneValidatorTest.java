package de.axone.webtemplate.validator.impl;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

@Test( groups="webtemplate.phonevalidator" )
public class PhoneValidatorTest {

	public void testValidator() throws Exception {
		
		PhoneValidator validator = new PhoneValidator();
		
		String testCoolPhone = "0941 / 599 854-0";
		assertTrue( validator.isValid( testCoolPhone ) );
		assertNull( validator.validate( testCoolPhone, null ) );
		
		String testWrongPhone1 = "No Phone";
		assertFalse( validator.isValid( testWrongPhone1 ) );
		assertNotNull( validator.validate( testWrongPhone1, null ) );
		assertEquals( validator.validate( testWrongPhone1, null ), "VALIDATOR_NO_PHONE" );
	}
}
