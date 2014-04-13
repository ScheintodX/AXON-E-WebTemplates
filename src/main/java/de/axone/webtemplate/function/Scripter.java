package de.axone.webtemplate.function;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Scripter {
	
	private static final Logger log = LoggerFactory.getLogger( Scripter.class );

	private ScriptEngine engine;
	
	public Scripter(){
		
	    ScriptEngineManager factory = new ScriptEngineManager();
	    
	    engine = factory.getEngineByName("JavaScript");
	    
	    /*
	    engine.put( "EMogul", EMogul.class );
	    engine.put( "STATICS", STATICS.class );
	    */
	    //engine.put( "rt", EMogul.rt() );
	    /*
	    engine.put( "em", em );
	    */
		
	}
	
	private String format( Object o ){
		
		String result = null;
		
		if( o instanceof Double ){
			// Simulate int results
			double val = ((Double)o).doubleValue();
			if( Math.floor( val ) == val ){
				result = "" + ((Double)o).intValue();
			}
		} 
		
		if( result == null ){
			result = o.toString();
		}
		
		return result;
	}

	private String eval( StringBuilder expression ) throws ScriptException {
		
		if( expression.length() == 0 ){
			log.warn( "Empty Expression" );
			return "";
		}
		
		String e = expression.toString();
			
		boolean doPrint = false;
		if( e.charAt( 0 ) == '=' ){
			e = e.substring( 1 );
			doPrint = true;
		}
		
		Object result = engine.eval( e );
		
		if( doPrint ){
			return format( result );
		} else {
			return "";
		}
		
	}
	public String run( CharSequence content ) throws ScriptException {
		
		int len = content.length();
		
		StringBuilder result = new StringBuilder( len );
		
		StringBuilder script = null;
		for( int i=0; i<len; i++ ){
			
			char c = content.charAt( i );
			
			if( script == null ){
				if( c == '<' && i+1 < len && content.charAt( i+1 ) == '?' ){
					script = new StringBuilder();
					i+=1;
					continue;
				}
			} else {
				if( c == '?' && i+1 < len && content.charAt( i+1 ) == '>' ){
					result.append( eval( script ) );
					script = null;
					i+=1;
					continue;
				}
			}
			
			if( script != null ){
				script.append( c );
			} else {
				result.append( c );
			}
			
		}
		
		return result.toString();
	}
	

}
