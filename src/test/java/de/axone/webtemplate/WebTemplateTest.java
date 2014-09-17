package de.axone.webtemplate;

import static org.testng.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.testng.annotations.Test;

import de.axone.cache.ng.CacheHashMap;
import de.axone.cache.ng.RealmImpl;
import de.axone.web.TestHttpServletRequest;
import de.axone.web.TestHttpServletResponse;
import de.axone.webtemplate.DataHolder.DataHolderItem;
import de.axone.webtemplate.DataHolder.DataHolderItemType;
import de.axone.webtemplate.form.Translator;

@Test( groups="webtemplate.webtemplate" )
public class WebTemplateTest {
	
	public void testAutomatedTemplate() throws Exception {
		
		// ../WebTemplates because we want this to be runnable from using project, too
		File file = new File( "../WebTemplates/src/test/resources/de/axone/webtemplate/TestTemplate.txt" );
		
		@SuppressWarnings( { "unchecked", "rawtypes" } )
		DataHolder holder = new FileDataHolderFactory( new CacheHashMap(
				new RealmImpl( "TestCache" ) ), null, null ).holderFor( file );
		
		StringWriter s = new StringWriter();
		holder.render( null, new PrintWriter( s ), null, null, null, null );
		
		AutomatedFileWebTemplate template = new AutomatedFileWebTemplate( holder );
		
		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setParameter( "name", "Hugo" );
		request.setParameter( "no", "123" );
		
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		
		template.render( null, response.getWriter(), request, response, null, null );
		
		assertEquals( response.getContent(), "Hello, I'm Hugo. This is WebTemplate no. 123. Name again: Hugo." );
		
		TestHttpServletRequest request2 = new TestHttpServletRequest();
		TestHttpServletResponse response2 = new TestHttpServletResponse();
		template.render( null, response2.getWriter(), request2, response2, null, null );
		
		assertEquals(  response2.getContent(), "Hello, I'm Hugo. This is WebTemplate no. 123. Name again: Hugo." );
		
		/*
		template.reset();
		
		TestHttpServletResponse response3 = new TestHttpServletResponse();
		template.render( null, response3.getWriter(), request2, response3, null, null );
		
		assertEquals( response3.getContent(), "Hello, I'm . This is WebTemplate no. . Name again: ." );
		*/
	}
	
	// Moved here because it is in no use but in this test
	// And this test is good because it tests more WT functionality
	private static final class AutomatedFileWebTemplate extends AbstractFileWebTemplate {
		
		public AutomatedFileWebTemplate( DataHolder holder ) {
			super( holder );
		}
		
		@Override
		public void render( Object object , PrintWriter out ,
				HttpServletRequest request , HttpServletResponse response ,
				Translator translator , ContentCache cache  ) throws WebTemplateException, IOException, Exception {
			
			for( String key : getHolder().getKeys() ){
				
				DataHolderItem value = getHolder().getItem( key );
				
				if( value.getType() == DataHolderItemType.VAR ){
					
					String parameter = request.getParameter( value.getName() );
	    			if( parameter != null ){
				
	    				getHolder().setValue( key, parameter );
	    			}
				}
			}
			
			//response.getWriter().write( getHolder().render().toString() );
			getHolder().render( object, out, request, response, translator, cache );
			
		}
		
}
}
