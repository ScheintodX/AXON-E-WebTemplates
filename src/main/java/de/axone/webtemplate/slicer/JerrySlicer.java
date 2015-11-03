package de.axone.webtemplate.slicer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;

import jodd.jerry.Jerry;
import jodd.jerry.Jerry.JerryParser;
import jodd.lagarto.dom.LagartoDOMBuilder;
import de.axone.data.Charsets;
import de.axone.file.Slurper;
import de.axone.tools.E;
import de.axone.tools.Text;


public abstract class JerrySlicer extends Slicer {
	
	protected Jerry doc;

	protected StringBuilder
			out = new StringBuilder(),
			prepend = new StringBuilder(),
			append = new StringBuilder();
	
	protected LagartoDOMBuilder domBuilder;
	{
		domBuilder = new LagartoDOMBuilder();
		domBuilder.enableXhtmlMode();
		domBuilder.enableDebug();
	}
        
	
	
	protected Jerry selected;
	protected Jerry view;
	
	protected int indent=0;
	
	public Jerry $( String css ){
		
		return $().$( css );
    }
	
	public Jerry $(){
		
		if( view != null ){
			return view;
		} else {
	    	return selected;
		}
	}
	
	public Jerry use( String css ){
		
		view();
		selected = doc.$( css );
		
		if( selected.size() == 0 )
			throw new IllegalStateException( "Nothing selected by: " + css );
		
		return selected;
	}
	
	// Todo: $(">") instead of outer/inner
	public Jerry selectOuter( String css ){
		
    	JerryParser parser = new JerryParser( domBuilder );
    	
		view();
		selected = parser.parse( outerHtml( doc.$( css ) ) ).children();
		
		return selected;
	}
	
	public Jerry select( String css ){
		
    	JerryParser parser = new JerryParser( domBuilder );
    	
		view();
		selected = parser.parse( html( doc.$( css ) ) );
		
		return selected;
	}
	
	public void view(){
		view = null;
	}
	public Jerry view( String css ){
		
		view = selected.$( css );
		return view;
	}
	
    public Jerry doc(){
    	
    	return doc;
    }
    
    public String html(){
    	
    	return html( selected );
    }
    public static String html( Jerry jerry ){
    	
    	return jerry.html();
    }
    
    public String outerHtml(){
    	
    	return outerHtml( selected );
    }
    public static String outerHtml( Jerry jerry ){
    	
    	if( jerry.size() > 0 ){
	    	return jerry.get()[0].getHtml();
    	} else {
    		return "";
	    }
    }
    
    public void replace( String css, String replacement ){
    	$(css).before( replacement ).remove();
    }
    
    @Override
    public void prependText( String text ){
    	prepend.append( text );
    }
    @Override
    public void appendText( String text ){
    	append.append( text );
    }
    
   
    @Override
    public int outLen(){
    	return /*prepend.length() +*/ out.length() /*+ append.length()*/;
    }
    
	@Override
	public String out(){
		/*
		StringBuilder result = new StringBuilder();
		result.append( prepend )
			.append( out )
			.append( append );
		out = new StringBuilder();
		prepend = new StringBuilder();
		append = new StringBuilder();
		
		return result.toString();
		*/
		String result = out.toString();
		out = new StringBuilder();
		return result;
	}
	public String peek(){
		return out.toString();
	}
	
	public void print(){
		System.out.println( html( selected ) );
	}
	public void printOuter(){
		System.out.println( outerHtml( selected ) );
	}
	
	public void indent( int indent ){
		this.indent = indent;
	}
	
	public void write(){
		write( html( selected ) );
	}
	public void writeOuter(){
		write( prepend.toString() );
		write( outerHtml( selected ) );
		write( append.toString() );
		prepend = new StringBuilder();
		append = new StringBuilder();
	}
	public void write( Jerry snippet ){
    	write( snippet.html() );
	}
	public void write( String text ){
		
		if( indent == 0 ){
			out.append( text );
			
		} else {
			
			try( BufferedReader r = new BufferedReader( new StringReader( text ) ) ){
			
		    	String l;
		    	while( ( l = r.readLine() ) != null ){
		    		
		    		if( indent > 0 ) l = Text.line( '\t', indent ) + l;
		    		else for( int i=0; i<-indent; i++ ){
		    			if( l.length() > 0 && l.charAt( 0 ) == '\t' ) l = l.substring( 1 );
		    		}
		    		out.append( l ).append( "\n" );
		    	}
				
			} catch( IOException e ) {
				throw new RuntimeException( e );
			}
		}
		
	}
	
    @Override
	public void init() {
    }
    
    @Override
	public void load( String master ) throws IOException{
    	
    	E.rr( "LOAD", master );
    	
    	File masterFile = new File( getMasterBase(), master );
    	String content = Slurper.slurpString( masterFile );
    	
        /*
        Inspector.inspect( domBuilder );
        Inspector.inspect( domBuilder.getConfig() );
        Inspector.inspect( domBuilder.getConfig().getLagartoHtmlRenderer() );
        */
        
        /* -> debug */
        //Document d = domBuilder.parse( content );
        /* <- debug */
    	
    	JerryParser parser = new JerryParser( domBuilder );
    	doc = parser.parse( content );
    	
		log.debug( "    Read: {}", masterFile );
    }
    	   
    public void save( File file ) throws IOException{
    	
    	Writer f = new OutputStreamWriter( new FileOutputStream( file ), Charsets.UTF8 );
    	f.write( out.toString() );
    	f.close();
    }
	
}
