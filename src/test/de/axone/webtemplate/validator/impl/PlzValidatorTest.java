package de.axone.webtemplate.validator.impl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

@Test( groups="webtemplate.plzvalidator" )
public class PlzValidatorTest {

	public void testValidator() throws Exception {
		
		PlzValidator validator = new PlzValidator( 5 );
		
		String testCoolPlz = "01234";
		assertTrue( validator.isValid( testCoolPlz ) );
		assertNull( validator.validate( testCoolPlz ) );
		
		String testWrongPlz1 = "1234";
		assertFalse( validator.isValid( testWrongPlz1 ) );
		assertNotNull( validator.validate( testWrongPlz1 ) );
		assertEquals( validator.validate( testWrongPlz1 ), "VALIDATOR_NO_PLZ" );
		
		String testWrongPlz2 = "A1234";
		assertFalse( validator.isValid( testWrongPlz2 ) );
		assertNotNull( validator.validate( testWrongPlz2 ) );
		assertEquals( validator.validate( testWrongPlz2 ), "VALIDATOR_NO_PLZ" );
	}
}
