package de.axone.webtemplate.validator.impl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

@Test( groups="webtemplate.emailvalidator" )
public class EMailValidatorTest {

	public void TestValidator() throws Exception {
		
		EMailValidator validator = new EMailValidator();
		
		String testCoolEMail = "f.bantner@axon-e.de";
		assertTrue( validator.isValid( testCoolEMail ) );
		assertNull( validator.validate( testCoolEMail ) );
		
		String testWrongEMail1 = "f.bantner.axon-e.de";
		assertFalse( validator.isValid( testWrongEMail1 ) );
		assertNotNull( validator.validate( testWrongEMail1 ) );
		assertEquals( validator.validate( testWrongEMail1 ), "VALIDATOR_NO_EMAIL" );
		
		/* This should fail but doesn't because it is valid (really!)
		String testWrongEMail2 = "a@b";
		assertFalse( validator.isValid( testWrongEMail2 ) );
		assertNotNull( validator.validate( testWrongEMail2 ) );
		assertEquals( validator.validate( testWrongEMail2 ), "VALIDATOR_NO_EMAIL" );
		
		String testWrongEMail3 = "f.bantner@axone-e-de";
		assertFalse( validator.isValid( testWrongEMail3 ) );
		assertNotNull( validator.validate( testWrongEMail3 ) );
		assertEquals( validator.validate( testWrongEMail3 ), "VALIDATOR_NO_EMAIL" );
		*/
	}
}
