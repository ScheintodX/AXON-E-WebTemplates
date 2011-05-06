package de.axone.webtemplate.form;

import static org.testng.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import de.axone.tools.Mapper;
import de.axone.tools.PasswordBuilder;

@Test( groups="webtemplate.translator" )
public class AbstractTranslatorTest {

	public void testAbstractTranslator() throws Exception {
		
		MyTranslator t = new MyTranslator();
		
		assertEquals( t.translate( "a" ), "A" );
		assertEquals( t.translate( "a", (HashMap<String,String>)null ), "A" );
		assertEquals( t.translate( "a", (String[])null ), "A" );
		
		assertEquals( t.translate( "b:x:y" ), "B x/y" );
		assertEquals( t.translate( "b",
				Mapper.hashMap( "0", "x", "1", "y" ) ), "B x/y" );
		assertEquals( t.translate( "b",
				new String[]{ "x", "y" } ), "B x/y" );
		
	}
	
	public void scatterTest() throws Exception {
		
		MyTranslator t = new MyTranslator();
		
		for( int i=0; i<000; i++ ){
			
			String password = PasswordBuilder.makePasswd( 8 );
			
			assertEquals( t.translate( "p", password ), "P"+password+"P", "Missmatch " + i );
		}
	}
	
	private static class MyTranslator extends AbstractTranslator {
		
		private static final Map<String,String> translations =
			Mapper.hashMap( "a", "A", "b", "B ###0###/###1###", "p", "P###0###P" );

		@Override
		protected String getPlainTranslation( String text ) {
			return translations.get( text );
		}

		@Override
		public boolean has( String text ) {
			return getPlainTranslation( text ) != null;
		}
		
	}
}
