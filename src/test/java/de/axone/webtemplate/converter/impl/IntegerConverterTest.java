package de.axone.webtemplate.converter.impl;

import static org.testng.Assert.*;

import java.util.Locale;

import org.testng.annotations.Test;

import de.axone.webtemplate.converter.ConverterException;

@Test( groups="webtemplate.integerconverter")
public class IntegerConverterTest {

	public void testConverterDE() throws Exception {
		testConverter( IntegerConverter.instance( Locale.GERMAN ), "1.234", 1234 );
		testConverter( IntegerConverter.instance( Locale.GERMANY ), "1.234", 1234 );
	}
	public void testConverterUS() throws Exception {
		testConverter( IntegerConverter.instance( Locale.ENGLISH ), "1,234", 1234 );
		testConverter( IntegerConverter.instance( Locale.US ), "1,234", 1234 );
		testConverter( IntegerConverter.instance( Locale.UK ), "1,234", 1234 );
	}
	private void testConverter( IntegerConverter converter, String asNumber, Integer number ) throws Exception {
		
		String coolString = asNumber;
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
		
		IntegerConverter converter = new IntegerConverter( Locale.GERMAN );
		
		String badString = "Blah";
		converter.convertFromString( badString );
		
	}
	
}
