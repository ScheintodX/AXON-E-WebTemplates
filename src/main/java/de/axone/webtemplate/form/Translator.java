package de.axone.webtemplate.form;

import java.util.Date;
import java.util.Map;


public interface Translator {

	public String translate( TranslationKey text );

	public String translate( TranslationKey text, String ... arguments );
	
	public String translate( TranslationKey text, Map<String,String> arguments );
	
	public String format( Number number );
	
	public String format( int style , Date date );
	
	public boolean has( TranslationKey text );

	public static final Translator NO_TRANSLATOR = new AbstractTranslator(){

		@Override
		public boolean has( TranslationKey text ) {
			return true;
		}

		@Override
		protected String getPlainTranslation( TranslationKey text ) {
			return text.name();
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
