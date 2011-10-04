package de.axone.webtemplate;

import static org.testng.Assert.*;

import java.util.Map;
import java.util.regex.Pattern;

import org.testng.annotations.Test;

public class AttributeParserTest {

	public static void main( String [] args ) throws Exception {
		
		new AttributeParserTest().testParser();
		new AttributeParserTest().testParserSpeed();
	}
	
	private static final String input = "tag attr1 attr2 = 123   attr3=\" value \"  attr4=\"'blah'123\" attr5='\"blub\"123' ";
	private static final int runs = 10000;
	private static final int runTime = 500;
	
    @Test( groups="webtemplate.attributeparser" )
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
		
		assertMatches( AttributeParser.TAG, input );
		
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
	
    @Test( groups="webtemplate.attributeparser" )
	public void testParser() throws Exception {
    	
		Map<String,Attribute> attributes;
		
    	// Simple
		attributes = AttributeParser.parse( " tag " );
		assertEquals( attributes.size(), 1 );
		assertTrue( attributes.containsKey( "TAG" ) );
		assertNull( attributes.get( "TAG" ).asInteger() );
		assertEquals( attributes.get( "TAG" ).asString(), "tag" );
		
		
		// Complex
		attributes = AttributeParser.parse( input );
		//E.rr( attributes );
		
		assertEquals( attributes.size(), 6 ); // Including TAG
		assertTrue( attributes.containsKey( "TAG" ) );
		assertTrue( attributes.containsKey( "attr1" ) );
		assertTrue( attributes.containsKey( "attr2" ) );
		assertTrue( attributes.containsKey( "attr3" ) );
		assertTrue( attributes.containsKey( "attr4" ) );
		assertTrue( attributes.containsKey( "attr5" ) );
		
		assertNull( attributes.get( "TAG" ).asInteger() );
		assertEquals( attributes.get( "TAG" ).asString(), "tag" );
		
		assertNull( attributes.get( "attr1" ) );
		
		assertEquals( attributes.get( "attr2" ).asString(), "123" );
		assertEquals( attributes.get( "attr2" ).asInteger(), new Integer( 123 ) );
		
		assertNull( attributes.get( "attr3" ).asInteger() );
		assertEquals( attributes.get( "attr3" ).asString(), " value " );
		
		assertNull( attributes.get( "attr4" ).asInteger() );
		assertEquals( attributes.get( "attr4" ).asString(), "'blah'123" );
		
		assertNull( attributes.get( "attr5" ).asInteger() );
		assertEquals( attributes.get( "attr5" ).asString(), "\"blub\"123" );
		
	}
    
    @Test( groups="webtemplate.attributeparser" )
    public void testParserSpeed() throws Exception {
    	
    	long start = System.currentTimeMillis();
    	for( int i =0; i < runs; i++ ){
    		AttributeParser.parse( input );
    	}
    	long end = System.currentTimeMillis();
    	long dur = end-start;
    	//E.rr( "Duation for " + runs + " runs was " + dur + "ms" );
    	assertTrue( dur < runTime, "Shouldn't take longer than " + runTime + "ms" );
    }
}
