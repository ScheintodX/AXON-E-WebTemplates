package de.axone.webtemplate.form;

import java.util.Date;
import java.util.Map;



public interface Translator {
	
	public boolean has( TranslationKey text );

	/**
	 * Translates the given Text using Properties substitution.
	 *
	 * In difference to translate( config, context, key, map ) this one accepts
	 * the properties as part of the string to translate. E.g.:
	 *
	 * In properties file:
	 * MY_TEXT:		Welcome ###0### ###1### at our site!
	 *
	 * call:		translate( "MY_TEXT:Mister:Meier" ),
	 *
	 * would result in "Welcome Mister Meier at our site!"
	 *
	 * @param key in format KEY_TO_LOOK_UP{:FIRST_PARAM{:SECOND_PARAM{:...}}}
	 * @return the translated text
	 */
	public String translate( TranslationKey key );
	
	public String translate( TranslationKey key, String ... arguments );
	
	public String translate( TranslationKey key, Map<String,String> arguments );
	
	public String translateDefault( String key, String defaultValue );

	public String format( Number number );
	
	public String format( int style , Date date );
	
	public static final Translator NO_TRANSLATOR = new AbstractTranslator(){

		@Override
		protected String getPlainTranslation( String key, String defaultValue ) {
			return key;
		}

		@Override
		public String format( Number number ) {
			return number.toString();
		}

		@Override
		public String format( int style, Date date ) {
			return ""+date.getTime();
		}
		
	};
}
