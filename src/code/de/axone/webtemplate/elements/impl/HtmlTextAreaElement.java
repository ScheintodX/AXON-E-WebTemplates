package de.axone.webtemplate.elements.impl;

import java.util.Arrays;
import java.util.List;

import de.axone.webtemplate.element.AbstractHtmlInputElement;
import de.axone.webtemplate.element.FormValueFactory;
import de.axone.webtemplate.form.FormValue;

public class HtmlTextAreaElement extends AbstractHtmlInputElement {
	
	private static String TAGNAME = "textarea";

	private static String ATTRIBUTE_NAME = "name";
	
	private static List<String> ATTRIBUTES = Arrays.asList( ATTRIBUTE_NAME );

	public HtmlTextAreaElement( String name ){
		
		this( name, null );
	}

	public HtmlTextAreaElement( String name, String value ) {
		
		super( TAGNAME, ATTRIBUTES );

		setName( name );
		setValue( value );
	}
	
	/**
	 * @deprecated This method is too static
	 */
	public static FormValue<String> createTextValue( String name, int length, boolean nullable ){
		
		return (new FormValueFactory()).createTextareaTextValue( name, length, nullable );
	}
	
	// --- Name ---
	@Override
	public String getName() {
		return getAttribute( ATTRIBUTE_NAME );
	}
	@Override
	public void setName( String name ){
		setAttribute( ATTRIBUTE_NAME, name );
		setAttribute( ATTRIBUTE_ID, "input_" + name );
	}

	// --- Value ---
	@Override
	public String getValue() {
		return (String) getContent();
	}
	@Override
	public void setValue( String value ) {
		setContent( value );
	}

	// --- Content ---
	@Override
	public void setContent( Object object ){
		throw new IllegalArgumentException( "setContent is not supported. Use setValue instead." );
	}
}
