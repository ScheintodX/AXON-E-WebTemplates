package de.axone.webtemplate.form;

import java.util.Map;


public interface Translator {

	public String translate( TranslationKey text );

	public String translate( TranslationKey text, String ... arguments );
	
	public String translate( TranslationKey text, Map<String,String> arguments );
	
	public boolean has( TranslationKey text );

	public static final Translator NoTranslator = new AbstractTranslator(){

		@Override
		public boolean has( TranslationKey text ) {
			return true;
		}

		@Override
		protected String getPlainTranslation( TranslationKey text ) {
			return text.name();
		}
		
	};
}
