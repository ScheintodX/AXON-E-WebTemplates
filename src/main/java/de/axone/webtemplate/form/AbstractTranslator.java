package de.axone.webtemplate.form;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import de.axone.tools.E;
import de.axone.tools.S;
import de.axone.tools.Str;

public abstract class AbstractTranslator implements Translator {

	private static final char FS = ':';
	private static final String NO_TEXT_FOR = "NO TEXT FOR: ";

	/**
	 * Override this in a way that it returns a translation
	 * template for the given text key.
	 *
	 * @param text
	 * @return
	 */
	protected abstract String getPlainTranslation( String key, String defaultValue );

	@Override
	public boolean has( TranslationKey key ) {

		if( key == null ) return false;

		return getPlainTranslation( key.name(), null ) != null;
	}

	@Override
	public String translateDefault( String key, String defaultValue ) {

		if( key == null ) return defaultValue;

		return getPlainTranslation( key, defaultValue );
	}

	@Override
	public String translate( TranslationKey key ) {

		if( key == null ) return S.EMPTY;

		String text = key.name();

		if( text.contains( "DYN_" ) ) {
			E.x( text );
		}

		// Shortcut for speed
		if( ! Str.contains( text, FS ) ) return translate( key, (Map<String,String>)null );

		String [] parts = Str.splitFast( text, FS );
		HashMap<String,String> params = new HashMap<>();

		String realText = parts[ 0 ];

		// (first part is the key)
		for( int i = 1; i < parts.length; i++ ){

			params.put( ""+(i-1), parts[ i ].trim() );
		}

		return translate( TKey.dynamic( realText ), params );
	}

	@Override
	public String translate( TranslationKey text, String ... arguments ) {

		HashMap<String,String> args = new HashMap<>();
		if( arguments != null ) for( int i=0; i < arguments.length; i++ ){
			args.put( ""+i, arguments[i] );
		}
		return translate( text, args );
	}

	@Override
	public String translate( TranslationKey text, Translatable ... arguments ) {

		HashMap<String,String> args = new HashMap<>();
		if( arguments != null ) for( int i=0; i < arguments.length; i++ ){
			args.put( ""+i, arguments[i].translated( this ) );
		}

		return translate( text, args );

	}

	@Override
	public String translate( TranslationKey text, Map<String,String> arguments ) {

		// Let the backend translate
		String result = getPlainTranslation( text.name(), null );

		if( result == null ){
			return NO_TEXT_FOR + '"' + text + '"';
		}

		// Replace parameters
		if( arguments != null ) {

			for( Map.Entry<String,String> entry : arguments.entrySet() ) {

				String pKey = entry.getKey();
				String replaceMe = "###" + pKey + "###";
				String replacement = arguments.get( pKey );

				result = result.replaceAll( replaceMe,
						Matcher.quoteReplacement( replacement ) );
			}
		}

		return result;
	}

	@Override
	public String format( Number number ) {

		return number.toString();
	}

	@Override
	public String format( int style, Date date ) {

		return DateFormat.getDateInstance( style ).format( date );
	}

}
