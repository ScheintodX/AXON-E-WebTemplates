package de.axone.webtemplate.slicer;

import static org.testng.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import de.axone.tools.Mapper;
import de.axone.webtemplate.WebTemplateException;

@Test( groups="test.slicer" )
public class JerrySlicerTest {
	
	private File master;
	private File templates;
	private TestSlicer slicer;
	
	@BeforeClass
	public void createFiles() throws Exception{
		
		master = File.createTempFile( "master", ".xhtml" );
		assertTrue( master.isFile() );
		assertTrue( master.canWrite() );
		
		try( PrintWriter out = new PrintWriter( new FileWriter( master ) ) ){
			
			out.println(
"<html>\n" +
"	<head>\n" +
"		<title>Master</title>\n" +
"	</head>\n" +
"	<body>\n" +
"		<div class=\"cA\" id=\"a\">\n" +
"			<div class=\"cA cB\" id=\"b\">\n" +
"				<div class=\"cA cB cC\" id=\"c\">\n" +
"					<p class=\"content\" id=\"content\">Content</p>" +
"				</div>\n" +
"			</div>\n" +
"		</div>\n" +
"	</body>\n" +
"</html>\n"
			);
		}
		
		
		//template = File.createTempFile( "template", ".xhtml" );
		templates = Files.createTempDirectory( "templates" ).toFile();
		assertTrue( templates.isDirectory() );
		
	}
	
	@AfterClass
	public void deleteFiles() throws Exception{
		
		master.delete();
		
		for( File f : templates.listFiles() ){ f.delete(); }
		templates.delete();
	}
	
	@BeforeClass
	public void slicerInit() throws Exception {
		
		File masterBase = master.getParentFile();
		
		String masterName = master.getName();
		
		slicer = new TestSlicer();
		slicer.setMasterBase( masterBase );
		slicer.setTemplateBase( templates );
		
		slicer.init();
		
		slicer.load( masterName );
		
		slicer.prepare( masterName );
	}
	
	public void testSliceA() throws Exception {
		
		String masterName = master.getName();
		slicer.parse( masterName, "a" );
	}
	
	public void testSliceB() throws Exception {
		
		String masterName = master.getName();
		slicer.parse( masterName, "b" );
	}
	
	public void testSliceC() throws Exception {
		
		String masterName = master.getName();
		slicer.parse( masterName, "c" );
	}
	
	@SuppressWarnings( "unused" )
	private String slurp( File f ) throws IOException{
		
		StringBuilder b = new StringBuilder();
		try( BufferedReader in = new BufferedReader( new FileReader( f ) ) ){
			
			String line;
			while( ( line = in.readLine() ) != null ){
				
				b.append( line ).append( "\n" );
			}
		}
		return b.toString();
	}
	
	private class TestSlicer extends JerrySlicer {

		@Override
		public List<String> getTemplateNames( String master ) {
			return Mapper.arrayList( "a", "b", "c" );
		}

		@Override
		public Class<?> getTemplateClass( String master, String name ) {
			return this.getClass();
		}

		@Override
		public void makeTemplate( String master, String name )
				throws WebTemplateException {
			
			if( "a".equals( name ) ){
				sliceA();
			} else if( "b".equals( name ) ){
				sliceB();
			} else if( "c".equals( name ) ){
				sliceC();
			}
		}
		
		private void sliceA(){
			
			String inner = "Content";
			String outer = "<p class=\"content\" id=\"content\">Content</p>";

			use( "#content" );
			assertEquals( html(), inner );
			assertEquals( outerHtml(), outer );
			
			select( "#content" );
			assertEquals( html(), inner );
			assertEquals( outerHtml(), inner );
					
			selectOuter( "#content" );
			assertEquals( html(), outer );
			assertEquals( outerHtml(), outer );
			
			use( "#c" );
			view( "#content" );
			assertEquals( html( $() ), inner );
			assertEquals( outerHtml( $() ), outer );
		}
		
		private void sliceB(){
			
			// TODO: More tests
		}
		
		private void sliceC(){
			
			// More tests
		}

		@Override
		public void prepare( String master ) throws WebTemplateException {}

		@Override
		public File getTemplateFile( String name ) {
			return new File( getTemplateBase(), name + ".xhtml" );
		}

		@Override
		public String getTemplateName( File file ) {
			return file.getName().replace( ".xhtml", "" );
		}
	}
}
