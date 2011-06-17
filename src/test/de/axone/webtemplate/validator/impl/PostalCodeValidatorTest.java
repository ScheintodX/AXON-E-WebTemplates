package de.axone.webtemplate.validator.impl;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.axone.webtemplate.validator.impl.PostalcodeValidator_Dynamic.CountryProvider;

@Test
public class PostalCodeValidatorTest {
	
	static final String[] exsGb = new String[]{ 
		"M1 1AA", "M60 1NW", "CR2 6XH", "DN55 1PT", "W1A 1HQ", "EC1A 1BB",
		"m1 1aa", "m60 1nw", "cr2 6xh", "dn55 1pt", "w1a 1hq", "ec1a 1bb",
	};
	
	static final String[] exsGbErr = new String[]{ 
		"Mo 1AA", "M60 oNW", "CRo 6XH", "DN55 oPT", "W11o 1HQ", "EC1A oBB",
		"m1 19a", "960 1nw", "cr2 69h", "d955 1pt", "w1a 19q", "19ec 1bb",
		"M11AA", "M601NW", "CR26XH", "DN551PT", "W1A1HQ", "EC1A1BB",
		"m11aa", "m601nw", "cr26xh", "dn551pt", "w1a1hq", "ec1a1bb",
	};
	
	static final String[] exsJe = new String[]{ 
		 "JE2 6XH", "JE55 1PT", "JE1A 1BB",
		 "je2 6xh", "je55 1pt", "je1a 1bb",
	};
	
	static final String[] exsJeErr = new String[]{ 
		 "CR2 6XH", "DN55 1PT", "EC1A 1BB",
		 "cr2 6xh", "dn55 1pt", "ec1a 1bb",
	};
	
	public void testValidation(){
		
		PostalcodeValidator validatorDe 
				= PostalcodeValidatorFactory.validatorFor( "de" );
		assertTrue( validatorDe instanceof PostalcodeValidator_De );
		
		PostalcodeValidator validatorAt 
				= PostalcodeValidatorFactory.validatorFor( "at" );
		
		PostalcodeValidator validatorUs 
				= PostalcodeValidatorFactory.validatorFor( "us" );
		assertTrue( validatorUs instanceof PostalcodeValidator_Us );
		
		PostalcodeValidator validatorGb 
				= PostalcodeValidatorFactory.validatorFor( "gb" );
		assertTrue( validatorGb instanceof PostalcodeValidator_Uk );
		
		// Jersey (in uk postcode system but specific prefix)
		PostalcodeValidator validatorJe 
				= PostalcodeValidatorFactory.validatorFor( "je" );
		
		assertTrue( validatorJe instanceof PostalcodeValidator_Uk );
		
		assertNull( validatorDe.validate( "12345" ) );
		assertNotNull( validatorDe.validate( "2345" ) );
		assertNotNull( validatorDe.validate( "123456" ) );
		
		assertNull( validatorAt.validate( "1234" ) );
		assertNotNull( validatorAt.validate( "234" ) );
		assertNotNull( validatorAt.validate( "12345" ) );
		
		for( String exGb : exsGb ){
			assertNull( validatorGb.validate( exGb ), exGb );
		}
		
		for( String exGb : exsGbErr ){
			assertNotNull( validatorGb.validate( exGb ), exGb );
		}
		
		for( String exJe : exsJe ){
			assertNull( validatorJe.validate( exJe ), exJe );
		}
		
		for( String exJe : exsJeErr ){
			assertNotNull( validatorJe.validate( exJe ), exJe );
		}
	}
	
	public void testDynamic(){
		
		TestCountryProvider tcp = new TestCountryProvider();
		
		PostalcodeValidator_Dynamic val 
				= new PostalcodeValidator_Dynamic( tcp );
		
		// Always validate without country
		val.validate( "abc123" );
		
		tcp.setIso2( "de" );
		assertNull( val.validate( "12345" ) );
		assertNotNull( val.validate( "2345" ) );
		assertNotNull( val.validate( "123456" ) );
		
		tcp.setIso2( "at" );
		assertNull( val.validate( "1234" ) );
		assertNotNull( val.validate( "234" ) );
		assertNotNull( val.validate( "12345" ) );
		
		tcp.setIso2( "gb" );
		for( String exGb : exsGb ){
			assertNull( val.validate( exGb ) );
		}
		
		for( String exGb : exsGbErr ){
			assertNotNull( val.validate( exGb ) );
		}
		
	}
	private static class TestCountryProvider implements CountryProvider {

		private String iso2;
		void setIso2( String iso2 ){
			this.iso2 = iso2;
		}
		@Override
		public String getCode() {
			
			return iso2;
		}
		
	}
	
}
