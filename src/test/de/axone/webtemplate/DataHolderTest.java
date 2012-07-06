package de.axone.webtemplate;

import static org.testng.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.testng.annotations.Test;

import de.axone.web.TestHttpServletRequest;
import de.axone.web.TestHttpServletResponse;
import de.axone.webtemplate.form.Translator;
import de.axone.webtemplate.function.Function;
import de.axone.webtemplate.function.MissingAttributeException;

@Test( groups="webtemplate.dataholder" )
public class DataHolderTest {
	
	public void testFactory() throws Exception {
		
		File tmp = File.createTempFile( "template", ".xhtml" );
		PrintWriter out = new PrintWriter( new FileWriter( tmp ) );
		
		out.println();
		out.println( "@Name: TestTemplate" );
		out.println( "@Class: de.axon.shop.webtemplate.TestWebTemplate" );
		out.println();
		out.println( "__var1__x__var2__-__func a=123 b='abc' c__!=__func a=456 b=\"def\" d__" );
		
		out.close();
		
		DataHolder holder = FileDataHolderFactory.instantiate( tmp, null );
		
		//E.rr( holder.toString() );
		
		assertEquals( holder.getParameter( "name" ), "TestTemplate" );
		assertEquals( holder.getParameter( "cLass" ), "de.axon.shop.webtemplate.TestWebTemplate" );
		assertEquals( holder.getKeys().size(), 6 );
		assertTrue( holder.getKeys().contains( "var1" ) );
		assertTrue( holder.getKeys().contains( "var2" ) );
		assertTrue( holder.getKeys().contains( "func" ) );
		assertTrue( holder.getKeys().contains( "text1" ) );
		assertTrue( holder.getKeys().contains( "text2" ) );
		assertTrue( holder.getKeys().contains( "text3" ) );
		assertEquals( holder.getItem( "var1" ).getValue(), "__var1__" );
		assertEquals( holder.getItem( "var2" ).getValue(), "__var2__" );
		assertEquals( holder.getItem( "text1" ).getValue(), "x" );
		assertEquals( holder.getItem( "text2" ).getValue(), "-" );
		assertEquals( holder.getItem( "text3" ).getValue(), "!=" );
		//E.rr( holder.getItem( "func_1" ) );
		
		// -- functions
		
		holder.setFunction( "func", new TestFunction() );
		
		TestHttpServletResponse respo = new TestHttpServletResponse();
		holder.render( null, new TestHttpServletRequest(), respo, null );
	}
	
	private static class TestFunction implements Function {

		@Override
		public void render( String name, DataHolder holder, 
				HttpServletRequest request, HttpServletResponse response,
				AttributeMap attributes, Object value, Translator translator
		) throws IOException, MissingAttributeException {
			
			if( ! holder.isRendering() ) return;
			
			PrintWriter out = response.getWriter();
			out.write( "A:"+attributes.getAsIntRequired( "a" ) );
			out.write( "B:"+attributes.getAsStringRequired( "b" ) );
			out.write( "C:"+attributes.containsKey( "c" ) );
		}
		
	}

}
