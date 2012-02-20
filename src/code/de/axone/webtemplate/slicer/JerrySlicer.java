package de.axone.webtemplate.slicer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import jodd.io.FileUtil;
import jodd.lagarto.dom.jerry.Jerry;


public abstract class JerrySlicer extends Slicer {
	
	protected Jerry doc;

	protected String buffer;
	protected StringBuilder out = new StringBuilder();
	
	protected Jerry selected;
	protected Jerry view;
	
	public Jerry $( String css, Object sux ){
		
		return $( css );
	}
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
	
	public Jerry select( String css ){
		
		view();
		selected = Jerry.jerry( outerHtml( doc.$( css ) ) );
		
		return selected;
	}
	
	public Jerry view( String css ){
		
		view = selected.$( css );
		return view;
	}
	public void view(){
		view = null;
	}
	
    public Jerry doc(){
    	
    	return doc;
    }
    
    public String html(){
    	
    	return html( selected );
    }
    public String html( Jerry jerry ){
    	
    	return jerry.html();
    }
    
    public String outerHtml(){
    	
    	return outerHtml( selected );
    }
    public String outerHtml( Jerry jerry ){
    	
    	if( jerry.size() > 0 ){
	    	return jerry.get()[0].getHtml();
    	} else {
    		return "";
	    }
    }
    
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
    	out.append( text );
	}
	
    @Override
	public void init() {
    }
    
    @Override
	public void load() throws IOException{
    	
    	File master = getMasterTemplate();
		doc = Jerry.jerry( FileUtil.readString( master ) );
		
		if( verbose )
			log.println( "    Read: " + master );
    }
    	   
    public void save( File file ) throws IOException{
    	
    	FileWriter f = new FileWriter( file );
    	f.write( out.toString() );
    	f.close();
    }
	
}
