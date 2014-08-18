package de.axone.webtemplate.list;

import static org.testng.Assert.*;

import java.io.PrintWriter;

import org.testng.annotations.Test;

import de.axone.web.SuperURLHttpServletRequest;
import de.axone.web.TestHttpServletResponse;

@Test( groups="webtemplate.pager" )
public class DefaultPagerTest {

	private static final String dps = "<div class=\"pager\">";
	private static final String dpe = "</div>";
		
	SuperURLHttpServletRequest request =
			new SuperURLHttpServletRequest("https://www.axon-e.de/blah?test=1&test=2&pageme-page=22");
		
	public void testDefaultPager1() throws Exception {
		
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		@SuppressWarnings( "resource" )
		PrintWriter out = response.getWriter();
		 
		Pager pager = new TestDefaultPager( "pageme", 2, 5 );
		pager.render( null, out, request, response, null );
		
		String content = response.getContent();
		response.resetBuffer();
		
		assertEquals( content, dps+"<<1-01[2]34-3>>"+dpe );
		
	}
		
	public void testDefaultPager2() throws Exception {
		
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		@SuppressWarnings( "resource" )
		PrintWriter out = response.getWriter();
		 
		Pager pager = new TestDefaultPager( "pageme", 0, 100 );
		pager.render( null, out, request, response, null );
		
		String content = response.getContent();
		response.resetBuffer();
		
		assertEquals( content, dps+"<a class=\"active\">&lt;&lt;</a>[0]12345678910...99-1>>"+dpe );
		
	}
		
	public void testDefaultPager3() throws Exception {
		
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		@SuppressWarnings( "resource" )
		PrintWriter out = response.getWriter();
		 
		Pager pager = new TestDefaultPager( "pageme", 50, 100 );
		pager.render( null, out, request, response, null );
		
		String content = response.getContent();
		response.resetBuffer();
		
		assertEquals( content, dps+"<<49-0...4546474849[50]5152535455...99-51>>"+dpe );
		
	}
		
	public void testDefaultPager4() throws Exception {
		
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		@SuppressWarnings( "resource" )
		PrintWriter out = response.getWriter();
		 
		Pager pager = new TestDefaultPager( "pageme", 99, 100 );
		pager.render( null, out, request, response, null );
		
		String content = response.getContent();
		response.resetBuffer();
		
		assertEquals( content, dps+"<<98-0...89909192939495969798[99]<a class=\"active\">&gt;&gt;</a>"+dpe );
	}
		
		//--LinkBuilding--
		
	public void testDefaultPager5() throws Exception {
		
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		@SuppressWarnings( "resource" )
		PrintWriter out = response.getWriter();
		 
		TestDefaultPager pager = new TestDefaultPager( "pageme", 0, 1 );
		pager.setSelectedTemplate( "__no__: __link__" );
		pager.setRenderIfOnlyOnePage( true );
		pager.render( null, out, request, response, null );
		
		String content = response.getContent();
		response.resetBuffer();
		
		assertEquals( content, dps+"<a class=\"active\">&lt;&lt;</a>1: /blah?test=1&test=2&pageme-page=0<a class=\"active\">&gt;&gt;</a>"+dpe );
			
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
