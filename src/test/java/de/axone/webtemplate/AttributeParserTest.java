package de.axone.webtemplate;

import static org.testng.Assert.*;

import java.util.regex.Pattern;

import org.testng.annotations.Test;

import de.axone.tools.E;

@Test( groups="webtemplate.attributeparser" )
public class AttributeParserTest {

	/*
	public static void main( String [] args ) throws Exception {
		
		new AttributeParserTest().testParser();
		new AttributeParserTest().testParserSpeed();
	}
	*/
	
	private static final String INPUT = "tag attr1 attr2 = 123   attr3=\" value \"  attr4=\"'blah'123\" attr5='\"blub\"123' ";
	private static final int RUNS = 10000;
	private static final int RUN_TIME = 500;
	
    @SuppressWarnings( "deprecation" )
	public void testRegexes() throws Exception {
    	
		assertMatches( AttributeParser.EQUAL, " =\t" );
		assertMatches( AttributeParser.NAME, "tagname_123" );
		
		assertMatches( AttributeParser.INTVAL, "12345" );
		assertMatches( AttributeParser.INTVAL, "-12345" );
		assertMatchesNot( AttributeParser.INTVAL, "12-213" );
		
		assertMatches( AttributeParser.STRINGVAL_IN_QUOT, "\"ab dd 'bla'\"" );
		assertMatchesNot( AttributeParser.STRINGVAL_IN_QUOT, "\"ab dd 'bla'" );
		assertMatches( AttributeParser.STRINGVAL_IN_APOS, "'ab dd \"bla\"'" );
		assertMatchesNot( AttributeParser.STRINGVAL_IN_APOS, "'ab dd 'bla'" );
		
		assertMatches( AttributeParser.TAGNAME, "tagname_123" );
		assertMatchesNot( AttributeParser.TAGNAME, "_tagname" );
		assertMatchesNot( AttributeParser.TAGNAME, "1tagname" );
		
		assertMatches( AttributeParser.ATTRIBUTE, "att1" );
		assertMatches( AttributeParser.ATTRIBUTE, "att2=123" );
		assertMatches( AttributeParser.ATTRIBUTE, "att3=\"abc\"" );
		assertMatches( AttributeParser.ATTRIBUTE, "att4='abc'" );
		assertMatches( AttributeParser.ATTRIBUTE, "att5='a\"bc'" );
		assertMatches( AttributeParser.ATTRIBUTE, "att5 = 'a\"bc'" );
		
		assertMatches( AttributeParser.TAG, INPUT );
		
	}
	private static boolean m( String pattern, String shouldMatch ){
		
		return Pattern.compile( pattern ).matcher( shouldMatch ).matches();
	}
	private static void assertMatches( String pattern, String shouldMatch ){
		
		assertTrue( m( pattern, shouldMatch ), shouldMatch );
	}
	private static void assertMatchesNot( String pattern, String shouldMatch ){
		assertFalse( m( pattern, shouldMatch ), shouldMatch );
	}
	
	public void testParser() throws Exception {
    	
    	// Simple
		{
			AttributeMap attributes = AttributeParserByHand.parse( " tag " );
			assertEquals( attributes.size(), 1 );
			assertTrue( attributes.containsKey( "TAG" ) );
			assertException( () -> attributes.getAsInteger( "TAG" ), IllegalArgumentException.class );
			assertEquals( attributes.getAsString( "TAG" ), "tag" );
		}
		
		
		// Complex
		{
			AttributeMap attributes = AttributeParserByHand.parse( INPUT );
		
			assertEquals( attributes.size(), 6 ); // Including TAG
			assertTrue( attributes.containsKey( "TAG" ) );
			assertTrue( attributes.containsKey( "attr1" ) );
			assertTrue( attributes.containsKey( "attr2" ) );
			assertTrue( attributes.containsKey( "attr3" ) );
			assertTrue( attributes.containsKey( "attr4" ) );
			assertTrue( attributes.containsKey( "attr5" ) );
			
			assertException( () -> attributes.getAsInteger( "TAG" ), IllegalArgumentException.class );
			assertEquals( attributes.getAsString( "TAG" ), "tag" );
			
			assertNull( attributes.getAsString( "attr1" ) );
			
			assertEquals( attributes.getAsString( "attr2" ), "123" );
			assertEquals( attributes.getAsInteger( "attr2" ), (Integer) 123 );
			
			assertException( () -> attributes.getAsInteger( "attr3" ), IllegalArgumentException.class );
			assertEquals( attributes.getAsString( "attr3" ), " value " );
			
			assertException( () -> attributes.getAsInteger( "attr4" ), IllegalArgumentException.class );
			assertEquals( attributes.getAsString( "attr4" ), "'blah'123" );
			
			assertException( () -> attributes.getAsInteger( "attr5" ), IllegalArgumentException.class );
			assertEquals( attributes.getAsString( "attr5" ), "\"blub\"123" );
		}
		
	}
    
    @Test( groups="webtemplate.attributeparser" )
    public void testParserSpeed() throws Exception {
    	
    	long start = System.currentTimeMillis();
    	for( int i =0; i < RUNS; i++ ){
    		AttributeParserByHand.parse( INPUT );
    	}
    	long end = System.currentTimeMillis();
    	long dur = end-start;
    	E.cho( "Duation for " + RUNS + " runs was " + dur + "ms" );
    	assertTrue( dur < RUN_TIME, "Shouldn't take longer than " + RUN_TIME + "ms" );
    }
    
    private void assertException( Runnable r, Class<? extends Exception> expected ){
    	
    	try {
    		r.run();
    	} catch( Exception e ){
    		assertEquals( e.getClass(), expected );
    	}
    }
    		
}
