package de.axone.webtemplate.converter.impl;

import static org.testng.Assert.*;

import java.math.BigDecimal;
import java.util.Locale;

import org.testng.annotations.Test;

import de.axone.webtemplate.converter.ConverterException;

@Test( groups="webtemplate.bigdecimalconverter")
public class BigDecimalConverterTest {
	
	public void testConverterDE() throws Exception {
		
		testConverter( BigDecimalConverter.forLocale( Locale.GERMAN ), "1.234,56", 1234.56 );
		testConverter( BigDecimalConverter.forLocale( Locale.GERMANY ), "1.234,56", 1234.56 );
	}
	
	public void testConverterUS() throws Exception {
		
		testConverter( BigDecimalConverter.forLocale( Locale.ENGLISH ), "1,234.56", 1234.56 );
		testConverter( BigDecimalConverter.forLocale( Locale.US ), "1,234.56", 1234.56 );
		testConverter( BigDecimalConverter.forLocale( Locale.UK ), "1,234.56", 1234.56 );
	}
	
	private void testConverter( BigDecimalConverter converter, String asString, double asDouble ) throws Exception {
		
		BigDecimal number = new BigDecimal( asDouble );
		String coolString = asString;
		assertTrue( converter.isValid( coolString ) );
		assertNull( converter.validate( coolString ) );
		assertEquals( converter.convertFromString( coolString ), number );
		assertEquals( converter.convertToString( number ), coolString );
		
		String emptyString = "";
		assertTrue( converter.isValid( emptyString ) );
		assertNull( converter.validate( emptyString ) );
		assertNull( converter.convertFromString( emptyString ) );
		
		String nullString = null;
		assertTrue( converter.isValid( nullString ) );
		assertNull( converter.validate( nullString ) );
		assertNull( converter.convertFromString( nullString ) );
		
		String badString = "Blah";
		assertFalse( converter.isValid( badString ) );
		assertNotNull( converter.validate( badString ) );
		
	}
	
	@Test( expectedExceptions=ConverterException.class )
	public void testConverterException() throws Exception {
		
		BigDecimalConverter converter = BigDecimalConverter.forLocale( Locale.GERMANY );
		
		String badString = "Blah";
		converter.convertFromString( badString );
		
	}
	
}
