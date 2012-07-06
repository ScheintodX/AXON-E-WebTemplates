package de.axone.webtemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import de.axone.webtemplate.AbstractFileWebTemplate.ParserException;
import de.axone.webtemplate.DataHolder.DataHolderItemType;
import de.axone.webtemplate.processor.WebTemplateProcessor;

public abstract class AbstractDataHolderFactory {
	
	protected static DataHolder instantiate( String data, CacheProvider dataCache ) throws IOException,
			ParserException, ClassNotFoundException, InstantiationException, IllegalAccessException {
				
		DataHolder holder = new DataHolder();
	
		// Replace fucking dos newlines with unix ones
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
	
			if( ! trimmed.startsWith( "@" ) ) break; // Quit on first not header line
	
			trimmed = trimmed.substring( 1 ); // remove the @
	
			int indexOfSep = trimmed.indexOf( ':' );
			//String[] parts = trimmed.split( "\\s*:\\s*" ); // Split on :
			String key = trimmed.substring( 0, indexOfSep ).trim().toLowerCase();
			String value = trimmed.substring( indexOfSep+1 ).trim();
	
			// Store
			holder.setParameter( key, value );
	
			// Count header chars
			count += line.length() +1; // +1 for NL
		}
	
		// Remove header
		data = data.substring( count );
		data = data.trim();
		
		// Preprocess data
		String processorClass = holder.getParameter( "Processor" );
		WebTemplateProcessor processor = null;
		if( processorClass != null ){
			processor = webTemplateProcessor( processorClass );
		}
		
		if( processor != null ){
			data = processor.preProcess( data );
		}
	
		// Parse template for variables
		boolean translate = false;
		String translationParts[] = data.split( "@@@" );
	
		int c=1;
		for( int t = 0; t < translationParts.length; t++ ){
	
			String translationPart = translationParts[ t ];
	
			String parts[] = translationPart.split( "__" );
	
			for( int i = 0; i < parts.length; i++ ) {
	
				String part = parts[ i ];
	
				if( i % 2 == 0 ) {
					if( part.length() > 0 ){
	    				holder.addValue( "text" + c++, part,
	    						DataHolderItemType.TEXT, translate );
					}
				} else {
					holder.addValue( part, "__" + part + "__",
							DataHolderItemType.VAR, translate );
				}
			}
	
			translate = !translate;
		}
		
		if( processor != null ){
			holder = processor.postProcess( holder );
		}
		
		holder.setCacheProvider( dataCache );
	
		return holder;
	}

	private static WebTemplateProcessor webTemplateProcessor( String className )
			throws ClassNotFoundException, InstantiationException, IllegalAccessException{
	
		Class<?> clazz = Class.forName( className );

		Object object = clazz.newInstance();

		return (WebTemplateProcessor) object;
	}
	
}
