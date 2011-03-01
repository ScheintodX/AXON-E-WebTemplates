package de.axone.webtemplate.elements.impl;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.testng.annotations.Test;

import de.axone.web.TestHttpServletRequest;
import de.axone.web.TestHttpServletResponse;
import de.axone.webtemplate.DefaultDecorator;


public class HtmlSelectElementTest {

	public static void main( String[] args ) throws Exception {
		
		HtmlInputElementTest test = new HtmlInputElementTest();
		test.testHtmlInputElement();
	}

	@Test( groups="webtemplate.htmlselectelement" )
	public void testHtmlSelectElement() throws Exception {
		
		List<Option> options = makeOptionsList(
				
				Arrays.asList( new String[]{ "key1", "key2", "key3" } ),
				Arrays.asList( new String[]{ "val1", "val2", "val3" } )
		);
		
		HtmlSelectElement element = new HtmlSelectElement( "testname", "key2", options );
		element.setDecorator( new DefaultDecorator() );
		element.setAttribute( HtmlSelectElement.ATTRIBUTE_ID, "testid" );
		element.setAttribute( HtmlSelectElement.ATTRIBUTE_CLASS, "testclass" );
		
		TestHttpServletRequest request = new TestHttpServletRequest();
		TestHttpServletResponse response = new TestHttpServletResponse();
		
		assertEquals( element.getName(), "testname" );
		assertEquals( element.getValue(), "key2" );
		
		element.render( null, request, response, null );
		
		//E.rr( response.getContent() );
		
		assertEquals( response.getContent(), 
				"<div class=\"valid\"><select id=\"testid\" class=\"testclass\" name=\"testname\">"+
					"<option value=\"key1\">val1</option>" +
					"<option value=\"key2\" selected=\"selected\">val2</option>" +
					"<option value=\"key3\">val3</option>" +
				"</select></div>"
		);
		
	}
	
	/*
	private List<HtmlSelectElement.Option> makeOptionsList( HashMap<String, String> options ){
		
		LinkedList<HtmlSelectElement.Option> result = new LinkedList<HtmlSelectElement.Option>();
		
		for( String key : options.keySet() ){
			
			String value = options.get( key );
			HtmlSelectElement.OptionImpl option = new HtmlSelectElement.OptionImpl( key, value );
			
			result.add( option );
		}
		return result;
	}
	*/
	
	private List<Option> makeOptionsList( List<String> keys, List<String> values ){
		
		LinkedList<Option> result = new LinkedList<Option>();
		
		for( int i = 0; i < keys.size(); i++ ){
			
			String key = keys.get( i );
			String value = values.get( i );
			
			OptionImpl option = new OptionImpl( key, value );
			
			result.add( option );
		}
		return result;
	}
	
}
