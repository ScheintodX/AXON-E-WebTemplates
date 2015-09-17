package de.axone.webtemplate;

import static org.assertj.core.api.Assertions.*;
import static org.testng.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.testng.annotations.Test;

import de.axone.web.TestHttpServletRequest;
import de.axone.web.TestHttpServletResponse;
import de.axone.webtemplate.Renderer.ContentCache;
import de.axone.webtemplate.form.Translator;
import de.axone.webtemplate.function.Function;
import de.axone.webtemplate.function.MissingAttributeException;

@Test( groups="webtemplate.dataholder" )
public class DataHolderTest {
	
	public void testFactory() throws Exception {
		
		File tmp = File.createTempFile( "template", ".xhtml" );
		try( PrintWriter out = new PrintWriter( new FileWriter( tmp ) ); ) {
		
			out.println();
			out.println( "@Name: TestTemplate" );
			out.println( "@Class: de.axon.shop.webtemplate.TestWebTemplate" );
			out.println( "@var3: V3" );
			out.println();
			out.println( "__var1__x__var2__x__var3__-__func a=123 <!==b \ncomment==>b='abc' c__!=__func a=456 b=\"def\" d__" );
		}
		
		DataHolder holder = FileDataHolderFactory.instantiate( tmp );
		
		//E.rr( holder.toString() );
		
		assertEquals( holder.getParameter( "name" ), "TestTemplate" );
		assertEquals( holder.getParameter( "cLass" ), "de.axon.shop.webtemplate.TestWebTemplate" );
		assertThat( holder.getKeys() )
				.contains( "var1", "var2", "var3", "func", "text1", "text2", "text3", "text4" )
				.hasSize( 8 )
				;
		/*
		assertNull( holder.getValue( "var1" ) );
		assertNull( holder.getValue( "var2" ) );
		assertEquals( holder.getValue( "text1" ), "x" );
		assertEquals( holder.getValue( "text2" ), "-" );
		assertEquals( holder.getValue( "text3" ), "!=" );
		*/
		
		// -- functions
		
		holder.setFunction( "func", new TestFunction() );
		holder.setValue( "var1", "V1" );
		holder.setValue( "var2", () -> "V2" );
		
		StringWriter sOut = new StringWriter();
		try( PrintWriter out = new PrintWriter( sOut ) ) {
		
			TestHttpServletResponse respo = new TestHttpServletResponse();
			holder.render( null, out, new TestHttpServletRequest(), respo, null, null );
		}
		String rendered = sOut.toString();
		
		assertEquals( rendered, "V1xV2xV3-A:123B:abcC:true!=A:456B:defC:false" );
	}
	
	private static class TestFunction implements Function {

		@Override
		public void render( String name , DataHolder holder , 
				PrintWriter out , HttpServletRequest request ,
				HttpServletResponse response , AttributeMap attributes , Object value , Translator translator , ContentCache cache 
		) throws IOException, MissingAttributeException {
			
			if( ! holder.isRendering() ) return;
			
			out.write( "A:"+attributes.getIntegerRequired( "a" ) );
			out.write( "B:"+attributes.getRequired( "b" ) );
			out.write( "C:"+attributes.containsKey( "c" ) );
		}
		
	}

}
