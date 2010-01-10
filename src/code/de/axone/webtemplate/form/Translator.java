package de.axone.webtemplate.form;


public interface Translator {

	public String translate( String text );

	public String translate( String text, String ... arguments );

}
