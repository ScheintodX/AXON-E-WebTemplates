package de.axone.webtemplate.converter.impl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Locale;

import org.testng.annotations.Test;

import de.axone.webtemplate.converter.ConverterException;

@Test( groups="webtemplate.integerconverter")
public class IntegerConverterTest {

	public void testConverter() throws Exception {
		
		IntegerConverter converter = new IntegerConverter( Locale.GERMAN );
		
		String coolString = "1.234";
		assertTrue( converter.isValid( coolString ) );
		assertNull( converter.validate( coolString ) );
		assertEquals( converter.convertFromString( coolString ), new Integer( 1234 ) );
		
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
