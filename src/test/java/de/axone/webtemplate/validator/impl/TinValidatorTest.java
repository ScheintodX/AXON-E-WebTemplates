package de.axone.webtemplate.validator.impl;

import static org.testng.Assert.*;

import org.assertj.core.api.AbstractAssert;
import org.testng.annotations.Test;

import de.axone.i18n.StaticCountries;

// Postalcode tests include pattern tests
@Test( groups="helper.validator.tin" )
public class TinValidatorTest {
	
	public void testConcreteCountryTins() throws Exception {
		
		validate( StaticCountries.BE, "BE1234567890", "BEA234567890", "BE123456789" ); //10
		validate( StaticCountries.BG, "BG123456789", "BEA23456789", "BE12345678" ); //9
		validate( StaticCountries.BG, "BG1234567890" );
		validate( StaticCountries.DK, "DK12345678", "DKA2345678", "DK1234567", "DK123456789" ); //8
		validate( StaticCountries.DE, "DE123456789", "DEA23456789", "DE12345678", "DE1234567890" ); //9
		validate( StaticCountries.EE, "EE123456789", "EEA23456789", "EE12345678", "EE1234567890" ); //9
		validate( StaticCountries.FI, "FI12345678", "FIA2345678", "FI1234567", "FI123456789" ); //8
		validate( StaticCountries.FR, "FR12345678901", "FR1234567890", "FR123456789012" ); //11
		validate( StaticCountries.FR, "FRA2345678901", "FR123456789A1" );
		validate( StaticCountries.FR, "FR1A345678901", "FR1234567890A" );
		validate( StaticCountries.FR, "FRAB345678901", "FR123456789AB" );
		validate( StaticCountries.GR, "EL123456789", "ELA23456789", "EL12345678", "EL1234567890" ); //9
		validate( StaticCountries.IE, "IE1234567A", "IEA234567A", "IE123456A", "IE12345678A" ); //7+X
		validate( StaticCountries.IE, "IE1A34567B", "IE1A345678" );
		validate( StaticCountries.IE, "IE1234567AB", "IE12345678B", "IE1234567A9" );
		validate( StaticCountries.FR, "FR12345678901", "FR1234567890", "FR123456789012" ); //11
		validate( StaticCountries.HR, "HR12345678901", "HR1234567890", "HR123456789012" ); //11
		validate( StaticCountries.LV, "LV12345678901", "LV1234567890", "LV123456789012" ); //11
		validate( StaticCountries.LT, "LT123456789", "LT12345678", "LT1234567890" ); //9 | 12
		validate( StaticCountries.LT, "LT123456789012", "LT12345678901", "LT1234567890123" ); //9 | 12
		validate( StaticCountries.LU, "LU12345678", "LUA2345678", "LU1234567", "LU123456789" ); //8
		validate( StaticCountries.MT, "MT12345678", "MTA2345678", "MT1234567", "MT123456789" ); //8
		validate( StaticCountries.NL, "NL123456789B12", "NL123456789B1", "NL123456789B123", "NL123456789A12" ); //12 incl. B
		validate( StaticCountries.AT, "ATU12345678", "ATO12345678", "AT123456789", "ATU1234567" ); //U+8
		validate( StaticCountries.PL, "PL1234567890", "PL123456789", "PL12345678901" ); //10
		validate( StaticCountries.PT, "PT123456789", "PTA23456789", "PT12345678", "PT1234567890" ); //9
		validate( StaticCountries.RO, "RO1234567890", "ROA234567890", "RO12345678901" ); //10
		validate( StaticCountries.RO, "RO123", "ROA23" );
		validate( StaticCountries.RO, "RO123456", "ROA23456" );
		validate( StaticCountries.SE, "SE123456789001", "SE12345678901", "SE1234567890101", "SE123456789002", "SE123456789011" ); //9 | 12
		validate( StaticCountries.SK, "SK1234567890", "SKA234567890", "SK12345678901" ); //10
		validate( StaticCountries.SI, "SI12345678", "SIA2345678", "SI1234567", "SI123456789" ); //8
		validate( StaticCountries.ES, "ES123456789", "ES12345678", "ES1234567890" ); //9
		validate( StaticCountries.ES, "ESA23456789" );
		validate( StaticCountries.ES, "ES12345678A" );
		validate( StaticCountries.ES, "ESA2345678B" );
		validate( StaticCountries.CZ, "CZ12345678", "CZA2345678", "CZ1234567" ); //8, 9, 10
		validate( StaticCountries.CZ, "CZ123456789", "CZA23456789" ); //9
		validate( StaticCountries.CZ, "CZ1234567890", "CZA234567890" ); //10
		validate( StaticCountries.HU, "HU12345678", "HUA2345678", "HU1234567", "HU123456789" ); //8
		validate( StaticCountries.GB, "GB123456789", "GBA23456789", "GB12345678", "GB1234567890" ); //9 | 12
		validate( StaticCountries.GB, "GB123456789012", "GBA23456789012", "GB12345678901", "GB1234567890123" ); //9 | 12
		validate( StaticCountries.GB, "GBGD123", "GBGDA23", "GBGE123", "GBGD12", "GBGD1234" ); //GBGDnnn
		validate( StaticCountries.GB, "GBHA123", "GBHAA23", "GBHE123", "GBHA12", "GBHA1234" ); //GBHAnnn
		validate( StaticCountries.CY, "CY12345678A", "CY123456789", "CY1234567A", "CY123456789A" ); //9
		
	}
	
	private void validate( StaticCountries country, String valid, String ... invalids ) {
		
		TinValidatorAssert tin = assertThis( new TinValidator( country.getTin() ) );
		
		tin.isValid( valid );
		for( String invalid : invalids ) {
			tin.isInvalid( invalid );
		}
	}
	
	public static TinValidatorAssert assertThis( TinValidator actual ) {
		
		return new TinValidatorAssert( actual );
	}
	
	public static class TinValidatorAssert extends AbstractAssert<TinValidatorAssert,TinValidator> {

		protected TinValidatorAssert( TinValidator actual ) {
			
			super( actual, TinValidatorAssert.class );
		}
		
		TinValidatorAssert isValid( String test ) {
			
			boolean valid = actual.isValid( test );
			
			if( !valid ) fail( "Invalid: " + test + " for pattern " + actual.originalPattern
					+ " (" + actual.examplify( actual.originalPattern, null ) + ")" );
			
			return this;
		}
		
		TinValidatorAssert isInvalid( String test ) {
			
			boolean valid = actual.isValid( test );
			
			if( valid ) fail( "Valid: " + test + " for pattern " + actual.originalPattern
					+ " (" + actual.examplify( actual.originalPattern, null ) + ")" );
			
			return this;
		}
	}
	
}
