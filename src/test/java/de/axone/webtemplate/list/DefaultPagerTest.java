package de.axone.webtemplate.list;

import static org.testng.Assert.*;

import java.io.PrintWriter;

import org.testng.annotations.Test;

import de.axone.web.SuperURLHttpServletRequest;
import de.axone.web.TestHttpServletResponse;

@Test( groups="webtemplate.pager" )
public class DefaultPagerTest {

	SuperURLHttpServletRequest request =
			new SuperURLHttpServletRequest("https://www.axon-e.de/blah?test=1&test=2&pageme-page=22");
		
	public void testDefaultPager1() throws Exception {
		
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		PrintWriter out = response.getWriter();
		 
		Pager pager = new TestDefaultPager( "pageme", 2, 5 );
		pager.render( null, out, request, response, null, null );
		
		String content = response.getContent();
		response.resetBuffer();
		
		assertEquals( content, "{<<1-0(1][2][3)4-3>>}" );
		
	}
		
	public void testDefaultPager2() throws Exception {
		
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		PrintWriter out = response.getWriter();
		 
		Pager pager = new TestDefaultPager( "pageme", 0, 100 );
		pager.render( null, out, request, response, null, null );
		
		String content = response.getContent();
		response.resetBuffer();
		
		assertEquals( content, "{[<<-1]-[0][1)2345678910...99-1>>}" );
		
	}
		
	public void testDefaultPager3() throws Exception {
		
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		PrintWriter out = response.getWriter();
		 
		Pager pager = new TestDefaultPager( "pageme", 50, 100 );
		pager.render( null, out, request, response, null, null );
		
		String content = response.getContent();
		response.resetBuffer();
		
		assertEquals( content, "{<<49-0...45464748(49][50][51)52535455...99-51>>}" );
		
	}
		
	public void testDefaultPager4() throws Exception {
		
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		PrintWriter out = response.getWriter();
		 
		Pager pager = new TestDefaultPager( "pageme", 99, 100 );
		pager.render( null, out, request, response, null, null );
		
		String content = response.getContent();
		response.resetBuffer();
		
		assertEquals( content, "{<<98-0...899091929394959697(98][99]-[100>>]}" );
	}
		
		//--LinkBuilding--
		
	public void testDefaultPager5() throws Exception {
		
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		PrintWriter out = response.getWriter();
		 
		TestDefaultPager pager = new TestDefaultPager( "pageme", 0, 1 );
		pager.setSelectedTemplate( "__no__: __link__" );
		pager.setRenderIfOnlyOnePage( true );
		pager.render( null, out, request, response, null, null );
		
		String content = response.getContent();
		response.resetBuffer();
		
		assertEquals( content, "{[<<-1]-1: /blah?test=1&amp;test=2&amp;pageme-page=0-[1>>]}" );
			
	}
	
	private static class TestDefaultPager extends DefaultPager {

		public TestDefaultPager(String nameBase, int selectedPage, int numPages) {
			
			super( nameBase, selectedPage, numPages );
			
    		setNoHost( true );
    		setLeftTemplate( "<<__index__-" );
    		setSelectedLeftTemplate( "[<<__index__]-" );
    		setInnerTemplate( "__index__" );
    		setSelectedTemplate( "[__index__]" );
    		setLeftOfSelectedTemplate( "(__index__]" );
    		setRightOfSelectedTemplate( "[__index__)" );
    		setRightTemplate( "-__index__>>" );
    		setSelectedRightTemplate( "-[__index__>>]" );
    		setSkippedTemplate( "..." );
    		setSpaceTemplalte( "" );
    		
    		setLeftContainer( "{" );
    		setRightContainer( "}" );
		}
		
	}
}
