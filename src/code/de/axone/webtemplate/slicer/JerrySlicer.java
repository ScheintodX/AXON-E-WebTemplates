package de.axone.webtemplate.slicer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import jodd.io.FileUtil;
import jodd.lagarto.dom.jerry.Jerry;
import de.axone.tools.Text;


public abstract class JerrySlicer extends Slicer {
	
	protected Jerry doc;

	protected String buffer;
	protected StringBuilder out = new StringBuilder();
	
	protected Jerry selected;
	protected Jerry view;
	
	protected int indent=0;
	
	public Jerry $( String css ){
		
		return looking().$( css );
    }
	
	public Jerry looking(){
		
		if( view != null ){
			return view;
		} else {
	    	return selected;
		}
	}
	
	public boolean has(){
		return looking().size() > 0;
	}
    
	public Jerry use( String css ){
		
		view();
		selected = doc.$( css );
		
		if( selected.size() == 0 )
			throw new IllegalStateException( "Nothing selected by: " + css );
		
		return selected;
	}
	
	// Todo: $(">") anstelle von outer/inner
	public Jerry selectOuter( String css ){
		
		view();
		selected = Jerry.jerry( outerHtml( doc.$( css ) ) ).children();
		
		return selected;
	}
	
	public Jerry select( String css ){
		
		view();
		selected = Jerry.jerry( html( doc.$( css ) ) );
		
		return selected;
	}
	
	public static Jerry clone( Jerry other ){
		return Jerry.jerry( outerHtml( other ) ).$( ">" );
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
    /*
    public void conditional( String css, String condition ){
    	
    	String html = $(css).html();
    	$(css).html( "__if condition=\" + contidion + \"__" + html + "__endif__" );
    }
    */
    
	@Override
	public String out(){
		String result = out.toString();
		out = new StringBuilder();
		return result;
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
		write( outerHtml( selected ) );
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
    	
    	File masterFile = new File( getMasterBase(), master );
		doc = Jerry.jerry( FileUtil.readString( masterFile ) );
		
		if( verbose )
			log.println( "    Read: " + masterFile );
    }
    	   
    public void save( File file ) throws IOException{
    	
    	FileWriter f = new FileWriter( file );
    	f.write( out.toString() );
    	f.close();
    }
	
}
