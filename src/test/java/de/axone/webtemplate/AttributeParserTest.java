package de.axone.webtemplate;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.axone.tools.E;

@Test( groups="webtemplate.attributeparser" )
public class AttributeParserTest {

	private static final String INPUT = "tag attr1 attr2 = 123   attr3=\" value \"  attr4=\"'blah'123\" attr5='\"blub\"123' ";
	private static final int RUNS = 100000;
	private static final int RUN_TIME = 2000;
	
	public void testParser() throws Exception {
    	
    	// Simple
		{
			AttributeMap attributes = AttributeParserByHand.parse( " tag " );
			assertEquals( attributes.size(), 1 );
			assertTrue( attributes.containsKey( "TAG" ) );
			assertException( () -> attributes.getInteger( "TAG" ), IllegalArgumentException.class );
			assertEquals( attributes.get( "TAG" ), "tag" );
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
			
			assertException( () -> attributes.getInteger( "TAG" ), IllegalArgumentException.class );
			assertEquals( attributes.get( "TAG" ), "tag" );
			
			assertNull( attributes.get( "attr1" ) );
			
			assertException( () -> attributes.get( "attr2" ), IllegalArgumentException.class );
			assertEquals( attributes.getInteger( "attr2" ), (Integer) 123 );
			
			assertException( () -> attributes.getInteger( "attr3" ), IllegalArgumentException.class );
			assertEquals( attributes.get( "attr3" ), " value " );
			
			assertException( () -> attributes.getInteger( "attr4" ), IllegalArgumentException.class );
			assertEquals( attributes.get( "attr4" ), "'blah'123" );
			
			assertException( () -> attributes.getInteger( "attr5" ), IllegalArgumentException.class );
			assertEquals( attributes.get( "attr5" ), "\"blub\"123" );
		}
		
	}
    
    @Test( groups="webtemplate.attributeparser" )
    public void testParserSpeed() throws Exception {
    	
    	long start, dur;
    	
    	for( int i =0; i < RUNS; i++ ){
    		AttributeParserByHand.parse( INPUT );
    	}
    	
    	start = System.currentTimeMillis();
    	for( int i =0; i < RUNS; i++ ){
    		AttributeParserByHand.parse( INPUT );
    	}
    	dur = System.currentTimeMillis() - start;
    	E.cho( "ByHand: Duation for " + RUNS + " runs was " + dur + "ms" );
    	
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
