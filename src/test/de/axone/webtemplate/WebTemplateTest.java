package de.axone.webtemplate;

import static org.testng.Assert.*;

import java.io.File;

import org.testng.annotations.Test;

import de.axone.cache.CacheHashMap;
import de.axone.web.TestHttpServletRequest;
import de.axone.web.TestHttpServletResponse;

@Test( groups="webtemplate.webtemplate" )
public class WebTemplateTest {
	
	public void testAutomatedTemplate() throws Exception {
		
		File file = new File( "src/test/de/axone/webtemplate/TestTemplate.txt" );
		@SuppressWarnings( "unchecked" )
		DataHolder holder = new FileDataHolderFactory( new CacheHashMap() ).holderFor( file );
		AutomatedFileWebTemplate template = new AutomatedFileWebTemplate( holder );
		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setParameter( "name", "Hugo" );
		request.setParameter( "no", "123" );
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		template.render( null, request, response, null );
		
		assertEquals(  "Hello, I'm Hugo. This is WebTemplate no. 123. Name again: Hugo." , response.getContent() );
		
		TestHttpServletRequest request2 = new TestHttpServletRequest();
		TestHttpServletResponse response2 = new TestHttpServletResponse();
		template.render( null, request2, response2, null );
		
		assertEquals(  "Hello, I'm Hugo. This is WebTemplate no. 123. Name again: Hugo." , response2.getContent() );
		
		template.reset();
		
		TestHttpServletResponse response3 = new TestHttpServletResponse();
		template.render( null, request2, response3, null );
		
		assertEquals( "Hello, I'm . This is WebTemplate no. . Name again: .", response3.getContent() );
	}
	
}
