package de.axone.webtemplate.form;

import static org.testng.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import de.axone.tools.Mapper;
import de.axone.tools.PasswordBuilder;

@Test( groups="webtemplate.translator" )
public class AbstractTranslatorTest {

	public void testAbstractTranslator() throws Exception {
		
		MyTranslator t = new MyTranslator();
		
		assertEquals( t.translate( TKey.dynamic( "a" ) ), "A" );
		assertEquals( t.translate( TKey.dynamic( "a" ), (HashMap<String,String>)null ), "A" );
		assertEquals( t.translate( TKey.dynamic( "a" ), (String[])null ), "A" );
		
		assertEquals( t.translate( TKey.dynamic( "b:x:y" ) ), "B x/y" );
		assertEquals( t.translate( TKey.dynamic( "b" ),
				Mapper.hashMap( "0", "x", "1", "y" ) ), "B x/y" );
		assertEquals( t.translate( TKey.dynamic( "b" ),
				new String[]{ "x", "y" } ), "B x/y" );
		
	}
	
	public void scatterTest() throws Exception {
		
		MyTranslator t = new MyTranslator();
		
		for( int i=0; i<000; i++ ){
			
			String password = PasswordBuilder.makePasswd( 8 );
			
			assertEquals( t.translate( TKey.dynamic( "p" ), password ), "P"+password+"P", "Missmatch " + i );
		}
	}
	
	private static class MyTranslator extends AbstractTranslator {
		
		private static final Map<String,String> translations =
			Mapper.hashMap( "a", "A", "b", "B ###0###/###1###", "p", "P###0###P" );

		@Override
		protected String getPlainTranslation( String key, String defaultValue ) {
			String result = translations.get( key );
			return result != null ? result : defaultValue;
		}

		@Override
		public boolean has( TranslationKey text ) {
			return getPlainTranslation( text.name(), null ) != null;
		}

		@Override
		public String format( Number number ) {
			return number.toString();
		}

		@Override
		public String format( int style, Date date ) {
			return ""+date.getTime();
		}
	}
	
}
