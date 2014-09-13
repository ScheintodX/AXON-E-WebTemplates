package de.axone.webtemplate.elements.impl;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import de.axone.web.TestHttpServletRequest;
import de.axone.web.TestHttpServletResponse;
import de.axone.webtemplate.DefaultDecorator;
import de.axone.webtemplate.elements.impl.HtmlInputElement.InputType;

public class HtmlInputElementTest {
	
	@Test( groups="webtemplate.htmlinputelement" )
	public void testHtmlInputElement() throws Exception {
		
		HtmlInputElement element = new HtmlInputElement( InputType.TEXT, "testname", "testvalue" );
		element.setAttribute( HtmlInputElement.ATTRIBUTE_CLASS, "testclass" );
		element.setAttribute( HtmlInputElement.ATTRIBUTE_ID, "testid" );
		element.setDecorator( new DefaultDecorator() );
		
		TestHttpServletRequest request = new TestHttpServletRequest();
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		assertEquals( element.getName(), "testname" );
		assertEquals( element.getValue(), "testvalue" );
		
		element.render( null, response.getWriter(), request, response, null, null );
		
		assertEquals( response.getContent(), "<div class=\"valid\"><input id=\"testid\" class=\"testclass\" type=\"text\" name=\"testname\" value=\"testvalue\" /></div>" );
	}
}
