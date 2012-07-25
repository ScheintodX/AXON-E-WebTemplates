package de.axone.webtemplate;

import static org.testng.Assert.*;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.testng.annotations.Test;

import de.axone.cache.CacheHashMap;
import de.axone.web.TestHttpServletRequest;
import de.axone.web.TestHttpServletResponse;

@Test( groups="webtemplate.webtemplate" )
public class WebTemplateTest {
	
	public void testAutomatedTemplate() throws Exception {
		
		File file = new File( "src/test/de/axone/webtemplate/TestTemplate.txt" );
		@SuppressWarnings( { "unchecked", "rawtypes" } )
		DataHolder holder = new FileDataHolderFactory( new CacheHashMap( "TestCache" ), null, null ).holderFor( file, null );
		StringWriter s = new StringWriter();
		holder.render( null, new PrintWriter( s ), null, null, null );
		AutomatedFileWebTemplate template = new AutomatedFileWebTemplate( holder );
		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setParameter( "name", "Hugo" );
		request.setParameter( "no", "123" );
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		template.render( null, response.getWriter(), request, response, null );
		
		assertEquals( response.getContent(), "Hello, I'm Hugo. This is WebTemplate no. 123. Name again: Hugo." );
		
		TestHttpServletRequest request2 = new TestHttpServletRequest();
		TestHttpServletResponse response2 = new TestHttpServletResponse();
		template.render( null, response2.getWriter(), request2, response2, null );
		
		assertEquals(  response2.getContent(), "Hello, I'm Hugo. This is WebTemplate no. 123. Name again: Hugo." );
		
		template.reset();
		
		TestHttpServletResponse response3 = new TestHttpServletResponse();
		template.render( null, response3.getWriter(), request2, response3, null );
		
		assertEquals( response3.getContent(), "Hello, I'm . This is WebTemplate no. . Name again: ." );
	}
	
}
