package de.axone.webtemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Pattern;

import de.axone.webtemplate.AbstractFileWebTemplate.ParserException;
import de.axone.webtemplate.DataHolder.DataHolderItemType;
import de.axone.webtemplate.processor.WebTemplateProcessor;

public abstract class AbstractDataHolderFactory {
	
	private static final char PARAMETER_SEP = ':';
	
	private static final String PARAMETER_PREFIX = "@",
	                            VARIABLE_SEP = "__",
	                            VARIABLE_ESC = "_\\_",
	                            TRANSLATION_SEP = "@@@"
	                            ;
	
	private static final String BEGIN_TEMPLATE = "<!--TEMPLATE: BEGIN-->",
	                            END_TEMPLATE = "<!--TEMPLATE: END-->"
	                            ;
	
	private static final String COMMENT_START = "<!==",
	                            COMMENT_END = "==>"
	                            ;
	
	private static final Pattern COMMENT_PATTERN = Pattern.compile( 
			COMMENT_START + ".*?" + COMMENT_END, Pattern.DOTALL );
	

	protected static DataHolder instantiate( String source, String data ) throws IOException,
			ParserException, ClassNotFoundException, InstantiationException, IllegalAccessException {
				
		DataHolder holder = new DataHolder( source );
		
		// Cut Markers.
		data = removeMarker( data, BEGIN_TEMPLATE, END_TEMPLATE );
	
		// Remove special removable comments
		data = COMMENT_PATTERN.matcher( data ).replaceAll( "" );
		
		// Replace fucking DOS newlines with UNIX ones
		data = data.replace( "\r\n", "\n" );
		
		// Header
		BufferedReader in = new BufferedReader( new StringReader( data ) );
		String line = null;
		int count=0;
		while( ( line = in.readLine() ) != null ){
	
			// Skip empty lines
			String trimmed = line.trim();
			if( trimmed.length() == 0 ){
				count += line.length()+1;
				continue;
			}
			
			// Remove some comment chars
			if( trimmed.startsWith( "<!--" ) && trimmed.endsWith( "-->" ) ){
				trimmed = trimmed.substring( 4, trimmed.length()-3 ).trim();
			} else if( trimmed.startsWith( "//" ) ){
				trimmed = trimmed.substring( 2 ).trim();
			} else if( trimmed.startsWith( "#" ) ){
				trimmed = trimmed.substring( 1 ).trim();
			}
			
			if( trimmed.startsWith( PARAMETER_PREFIX ) ){ 
	
				trimmed = trimmed.substring( 1 ); // remove the @
				
			} else {
				// Quit on first not header line
				break;
			}
			
			int indexOfSep = trimmed.indexOf( PARAMETER_SEP );
			String key = trimmed.substring( 0, indexOfSep ).trim().toLowerCase();
			String value = trimmed.substring( indexOfSep+1 ).trim();
	
			// Store
			holder.setSystemParameter( key, value );
	
			// Count header chars
			count += line.length() +1; // +1 for NL
		}
	
		// Remove header
		if( count > data.length() ) count=data.length();
		data = data.substring( count );
		
		// Cut between markers
		String cut = holder.getParameter( DataHolder.PARAM_CUT );
		if( cut != null ){
			data = removeMarker( data, cut, cut );
		}
		
		// Trim
		data = data.trim();
		
		// Preprocess data
		String processorClass = holder.getParameter( DataHolder.PARAM_PROCESSOR );
		WebTemplateProcessor processor = null;
		if( processorClass != null ){
			processor = webTemplateProcessor( processorClass );
		}
		
		if( processor != null ){
			data = processor.preProcess( data );
		}
	
		// Parse template for variables
		boolean translate = false;
		String translationParts[] = data.split( TRANSLATION_SEP );
	
		int c=1;
		for( int t = 0; t < translationParts.length; t++ ){
	
			String translationPart = translationParts[ t ];
	
			String parts[] = translationPart.split( VARIABLE_SEP );
	
			for( int i = 0; i < parts.length; i++ ) {
	
				String part = parts[ i ];
				
				part = part.replace( VARIABLE_ESC, VARIABLE_SEP );
	
				if( i % 2 == 0 ) {
					if( part.length() > 0 ){
						// Add as Text-Part. Names: text1, text2, ... 
	    				holder.addValue( "text" + c++, part,
	    						DataHolderItemType.TEXT, translate );
					}
				} else {
					// Add as Variable-Part. Default values: __abc__, __def__ etc
					holder.addValue( part, VARIABLE_SEP + part + VARIABLE_SEP,
							DataHolderItemType.VAR, translate );
				}
			}
	
			translate = !translate;
		}
		
		if( processor != null ){
			holder = processor.postProcess( holder );
		}
		
		holder.setSystemParameter( DataHolder.P_TRUE, Boolean.TRUE.toString() );
		holder.setSystemParameter( DataHolder.P_FALSE, Boolean.FALSE.toString() );
		
		return holder;
	}

	
	private static WebTemplateProcessor webTemplateProcessor( String className )
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
	
		Class<?> clazz = Class.forName( className );

		Object object = clazz.newInstance();

		return (WebTemplateProcessor) object;
	}
	
	
	private static String removeMarker( String data, String startCut, String endCut ) {
		
		int begin = data.indexOf( startCut );
		int end = data.indexOf( endCut, begin );
		
		begin = begin > 0 ? begin + BEGIN_TEMPLATE.length() : 0;
		end = end > 0 ? end : data.length();
		
		return data.substring( begin, end ).trim();
	}
	
}
