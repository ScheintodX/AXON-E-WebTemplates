package de.axone.webtemplate.form;

import java.util.Map;


public interface Translator {

	public String translate( String text );

	public String translate( String text, String ... arguments );
	
	public String translate( String text, Map<String,String> arguments );
	
	public boolean has( String text );

}
