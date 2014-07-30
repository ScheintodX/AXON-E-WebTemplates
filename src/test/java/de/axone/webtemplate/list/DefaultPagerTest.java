package de.axone.webtemplate.list;

import static org.testng.Assert.*;

import java.io.PrintWriter;

import org.testng.annotations.Test;

import de.axone.web.TestHttpServletRequest;
import de.axone.web.TestHttpServletResponse;

@Test( groups="webtemplate.pager" )
public class DefaultPagerTest {

	private static final String dps = "<div class=\"pager\">";
	private static final String dpe = "</div>";
		
	public void testDefaultPager() throws Exception {
		
		TestHttpServletRequest request = new TestHttpServletRequest();
		
		request.setRequestURL( new StringBuffer( "https://www.axon-e.de/blah?test=1&test=2&pageme-page=22" ) );
		
		TestHttpServletResponse response = new TestHttpServletResponse();
		try( PrintWriter out = response.getWriter(); ){
			DefaultPager pager;
			String content;
			
			pager = new TestDefaultPager( "pageme", 2, 5 );
			pager.render( null, out, request, response, null );
			
			content = response.getContent();
			response.resetBuffer();
			
			assertEquals( content, dps+"<<1-01[2]34-3>>"+dpe );
			
			//----
			
			pager = new TestDefaultPager( "pageme", 0, 100 );
			pager.render( null, out, request, response, null );
			
			content = response.getContent();
			response.resetBuffer();
			
			assertEquals( content, dps+"<a class=\"active\">&lt;&lt;</a>[0]12345678910...99-1>>"+dpe );
			
			//----
			
			pager = new TestDefaultPager( "pageme", 50, 100 );
			pager.render( null, out, request, response, null );
			
			content = response.getContent();
			response.resetBuffer();
			
			assertEquals( content, dps+"<<49-0...4546474849[50]5152535455...99-51>>"+dpe );
			
			//----
			
			pager = new TestDefaultPager( "pageme", 99, 100 );
			pager.render( null, out, request, response, null );
			
			content = response.getContent();
			response.resetBuffer();
			
			assertEquals( content, dps+"<<98-0...89909192939495969798[99]<a class=\"active\">&gt;&gt;</a>"+dpe );
			
			//--LinkBuilding--
			
			pager = new TestDefaultPager( "pageme", 0, 1 );
			pager.setSelectedTemplate( "__no__: __link__" );
			pager.setRenderIfOnlyOnePage( true );
			pager.render( null, out, request, response, null );
			
			content = response.getContent();
			response.resetBuffer();
			
			assertEquals( content, dps+"<a class=\"active\">&lt;&lt;</a>1: /blah?test=1&test=2&pageme-page=0<a class=\"active\">&gt;&gt;</a>"+dpe );
			
		}
		
	}
	
	private static class TestDefaultPager extends DefaultPager {

		public TestDefaultPager(String nameBase, int selectedPage, int numPages) {
			
			super( nameBase, selectedPage, numPages );
			
    		setNoHost( true );
    		setLeftTemplate( "<<__index__-" );
    		setInnerTemplate( "__index__" );
    		setSelectedTemplate( "[__index__]" );
    		setRightTemplate( "-__index__>>" );
    		setSkippedTemplalte( "..." );
    		setSpaceTemplalte( "" );
		}
		
	}
}
