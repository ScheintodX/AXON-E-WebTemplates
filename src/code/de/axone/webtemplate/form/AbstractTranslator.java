package de.axone.webtemplate.form;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import de.axone.tools.S;

public abstract class AbstractTranslator implements Translator {
	
	private static final String NO_TEXT_FOR = "NO TEXT FOR: ";
	
	/**
	 * Override this in a way that it returns a translation
	 * template for the given text key.
	 * 
	 * @param text
	 * @return
	 */
	protected abstract String getPlainTranslation( String text );

	@Override
	public String translate( String text ) {

		if( text == null ) return S.EMPTY;

		String [] parts = text.split( ":" );
		HashMap<String,String> params = new HashMap<String,String>();

		String realText = parts[ 0 ];

		// (first part is the key)
		for( int i = 1; i < parts.length; i++ ){

			params.put( ""+(i-1), parts[ i ].trim() );
		}
		
		return translate( realText, params );
	}

	@Override
	public String translate( String text, String ... arguments ) {

		HashMap<String,String> args = new HashMap<String,String>();
		if( arguments != null ) for( int i=0; i < arguments.length; i++ ){
			args.put( ""+i, arguments[i] );
		}
		
		return translate( text, args );

	}
	
	@Override
	public String translate( String text, Map<String,String> arguments ) {
		
		// Let the backend translate
		String result = getPlainTranslation( text );
		
		if( result == null ){
			return NO_TEXT_FOR + '"' + text + '"';
		}
		
		// Replace parameters
		if( arguments != null ) {
			for( String pKey : arguments.keySet() ) {
				String replaceMe = "###" + pKey + "###";
				String replacement = arguments.get( pKey );
				result = result.replaceAll( replaceMe,
						Matcher.quoteReplacement( replacement ) );
			}
		}
		
		return result;
	}

}
	