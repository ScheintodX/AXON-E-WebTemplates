package de.axone.webtemplate.validator.impl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

@Test( groups="webtemplate.phonevalidator" )
public class PhoneValidatorTest {

	public void testValidator() throws Exception {
		
		PhoneValidator validator = new PhoneValidator();
		
		String testCoolPhone = "0941 / 599 854-0";
		assertTrue( validator.isValid( testCoolPhone ) );
		assertNull( validator.validate( testCoolPhone ) );
		
		String testWrongPhone1 = "No Phone";
		assertFalse( validator.isValid( testWrongPhone1 ) );
		assertNotNull( validator.validate( testWrongPhone1 ) );
		assertEquals( validator.validate( testWrongPhone1 ), "VALIDATOR_NO_PHONE" );
	}
}
