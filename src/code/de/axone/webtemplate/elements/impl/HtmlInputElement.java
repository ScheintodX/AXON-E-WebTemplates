package de.axone.webtemplate.elements.impl;

import java.util.Arrays;
import java.util.List;

import de.axone.webtemplate.element.AbstractHtmlInputElement;

public class HtmlInputElement extends AbstractHtmlInputElement {

	private static String NAME = "input";

	private static String ATTRIBUTE_TYPE = "type";
	private static String ATTRIBUTE_NAME = "name";
	private static String ATTRIBUTE_VALUE = "value";

	private static List<String> ATTRIBUTES = Arrays.asList( ATTRIBUTE_TYPE,
			ATTRIBUTE_NAME, ATTRIBUTE_VALUE );

	public enum InputType {
		TEXT, PASSWORD, HIDDEN, RADIO
	};

	public HtmlInputElement(InputType type, String name) {
		this( type, name, null );
	}

	public HtmlInputElement(InputType type, String name, String value) {

		super( NAME, ATTRIBUTES );

		setName( name );
		setValue( value );

		setAttribute( ATTRIBUTE_TYPE, type.toString().toLowerCase() );
	}

	// --- Name ---
	@Override
	public String getName() {
		return getAttribute( ATTRIBUTE_NAME );
	}

	@Override
	public void setName( String name ) {
		setAttribute( ATTRIBUTE_NAME, name );
		setAttribute( ATTRIBUTE_ID, "input_" + name );
	}

	// --- Value ---
	@Override
	public String getValue() {
		return getAttribute( ATTRIBUTE_VALUE );
	}

	@Override
	public void setValue( String value ) {
		setAttribute( ATTRIBUTE_VALUE, value );
	}

}
