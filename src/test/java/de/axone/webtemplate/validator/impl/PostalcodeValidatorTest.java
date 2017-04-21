package de.axone.webtemplate.validator.impl;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.axone.webtemplate.validator.impl.PostalcodeValidator_Dynamic.CountryProvider;

@Test( groups="helper.validator.postalcode" )
public class PostalcodeValidatorTest {
	
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
	
	static final String[] exsIe = new String[]{
		"A94 PD74", "X1X XXXX", "x1x xxxx", "111 1111"
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
		
		// Eircode
		PostalcodeValidator validatorIe
				= PostalcodeValidatorFactory.validatorFor( "ie" );
		
		assertTrue( validatorJe instanceof PostalcodeValidator_Uk );
		
		assertNull( validatorDe.validate( "12345", null ) );
		assertNotNull( validatorDe.validate( "2345", null ) );
		assertNotNull( validatorDe.validate( "123456", null ) );
		
		assertNull( validatorAt.validate( "1234", null ) );
		assertNotNull( validatorAt.validate( "234", null ) );
		assertNotNull( validatorAt.validate( "12345", null ) );
		
		for( String exGb : exsGb ){
			assertNull( validatorGb.validate( exGb, null ), exGb );
		}
		
		for( String exGb : exsGbErr ){
			assertNotNull( validatorGb.validate( exGb, null ), exGb );
		}
		
		for( String exJe : exsJe ){
			assertNull( validatorJe.validate( exJe, null ), exJe );
		}
		
		for( String exJe : exsJeErr ){
			assertNotNull( validatorJe.validate( exJe, null ), exJe );
		}
		
		for( String exIe : exsIe ) {
			assertNull( validatorIe.validate( exIe, null ), exIe );
		}
	}
	
	public void testDynamic(){
		
		TestCountryProvider tcp = new TestCountryProvider();
		
		PostalcodeValidator_Dynamic val 
				= new PostalcodeValidator_Dynamic( tcp );
		
		// Always validate without country
		val.validate( "abc123", null );
		
		tcp.setIso2( "de" );
		assertNull( val.validate( "12345", null ) );
		assertNotNull( val.validate( "2345", null ) );
		assertNotNull( val.validate( "123456", null ) );
		
		tcp.setIso2( "at" );
		assertNull( val.validate( "1234", null ) );
		assertNotNull( val.validate( "234", null ) );
		assertNotNull( val.validate( "12345", null ) );
		
		tcp.setIso2( "gb" );
		for( String exGb : exsGb ){
			assertNull( val.validate( exGb, null ) );
		}
		
		for( String exGb : exsGbErr ){
			assertNotNull( val.validate( exGb, null ) );
		}
	}
	
	public void testWildcardHandling(){
		
		PostalcodeValidator a = new PostalcodeValidator( "an" );
		assertNull( a.validate( "A1", null ) );
		assertNotNull( a.validate( "A-1", null ) );
		assertNotNull( a.validate( "A 1", null ) );
		
		PostalcodeValidator b = new PostalcodeValidator( "a n" );
		assertNull( b.validate( "A 1", null ) );
		assertNotNull( b.validate( "A-1", null ) );
		assertNotNull( b.validate( "A1", null ) );
		
		PostalcodeValidator c = new PostalcodeValidator( "a.n" );
		assertNull( c.validate( "A 1", null ) );
		assertNull( c.validate( "A-1", null ) );
		assertNotNull( b.validate( "A1", null ) );
		
		PostalcodeValidator d = new PostalcodeValidator( "a ?n" );
		assertNull( d.validate( "A1", null ) );
		assertNull( d.validate( "A 1", null ) );
		assertNotNull( d.validate( "A-1", null ) );
		
		PostalcodeValidator e = new PostalcodeValidator( "a?n" );
		assertNull( e.validate( "A1", null ) );
		assertNotNull( e.validate( "A 1", null ) );
		assertNotNull( e.validate( "A-1", null ) );
		assertNull( e.validate( "1", null ) );
		
		PostalcodeValidator f = new PostalcodeValidator( "a n", true );
		assertNull( f.validate( "A 1", null ) );
		assertNull( f.validate( "A1", null ) );
		assertNotNull( f.validate( "A-1", null ) );
		
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
