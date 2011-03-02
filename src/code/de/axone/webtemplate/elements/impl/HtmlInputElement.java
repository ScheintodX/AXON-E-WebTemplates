package de.axone.webtemplate.elements.impl;

import java.util.Arrays;
import java.util.List;

import de.axone.webtemplate.element.AbstractHtmlInputElement;

public class HtmlInputElement extends AbstractHtmlInputElement {

	private static String NAME = "input";

	private static String ATTRIBUTE_TYPE = "type";
	private static String ATTRIBUTE_NAME = "name";
	private static String ATTRIBUTE_VALUE = "value";
	private static String ATTRIBUTE_READONLY = "readonly";
	private static String ATTRIBUTE_SIZE = "size";
	private static String ATTRIBUTE_MAXLENGTH = "maxlength";
	private static String ATTRIBUTE_ACCEPT = "accept";

	private static List<String> ATTRIBUTES = Arrays.asList(
			ATTRIBUTE_TYPE, ATTRIBUTE_NAME, ATTRIBUTE_VALUE,
			ATTRIBUTE_READONLY, ATTRIBUTE_SIZE, ATTRIBUTE_MAXLENGTH, ATTRIBUTE_ACCEPT );

	public enum InputType {
		TEXT, PASSWORD, CHECKBOX, RADIO, SUBMIT, RESET, FILE, HIDDEN, IMAGE, BUTTON
	};

	public HtmlInputElement(InputType type, String name) {
		this( type, name, null );
	}

	public HtmlInputElement(InputType type, String name, String value) {

		super( NAME, ATTRIBUTES );

		setName( name );
		setValue( value );
		setType( type );
	}

	// === Name ===
	@Override
	public String getName() {
		return getAttribute( ATTRIBUTE_NAME );
	}

	@Override
	public void setName( String name ) {
		setAttribute( ATTRIBUTE_NAME, name );
		setAttribute( ATTRIBUTE_ID, "input_" + name );
	}

	// === Value ===
	@Override
	public String getValue() {
		return getAttribute( ATTRIBUTE_VALUE );
	}

	@Override
	public void setValue( String value ) {
		setAttribute( ATTRIBUTE_VALUE, value );
	}
	
	// --- Type ---
	public void setType( InputType type ){
		if( type == null ) throw new IllegalArgumentException( "'type' is null" );
		setAttribute( ATTRIBUTE_TYPE, type.toString().toLowerCase() );
	}
	public InputType getType(){
		String typeStr = getAttribute( ATTRIBUTE_TYPE );
		if( typeStr == null ) return null;
		return InputType.valueOf( typeStr );
	}

	// --- Readonly ---
	public void setReadonly( boolean isReadonly ){
		if( isReadonly ){
			setAttribute( ATTRIBUTE_READONLY, ATTRIBUTE_READONLY );
		} else {
			setAttribute( ATTRIBUTE_READONLY, null );
		}
	}
	public boolean isReadonly(){
		return getAttribute( ATTRIBUTE_READONLY ).equals( ATTRIBUTE_READONLY );
	}
	
	// --- Size ---
	public void setSize( Integer size ){
		setAttribute( ATTRIBUTE_SIZE, size != null ? size.toString() : null );
	}
	public Integer getSize(){
		String sizeStr = getAttribute( ATTRIBUTE_SIZE  );
		if( sizeStr == null ) return null;
		return Integer.parseInt( sizeStr );
	}
	
	// --- MaxLength ---
	public void setMaxLength( Integer maxLength ){
		setAttribute( ATTRIBUTE_MAXLENGTH, maxLength != null ? maxLength.toString() : null );
	}
	public Integer getMaxLength(){
		String maxLengthStr = getAttribute( ATTRIBUTE_MAXLENGTH  );
		if( maxLengthStr == null ) return null;
		return Integer.parseInt( maxLengthStr );
	}
	
	// --- Accept ---
	public void setAccept( String accept ){
		setAttribute( ATTRIBUTE_ACCEPT, accept );
	}
	public String getAccept(){
		return getAttribute( ATTRIBUTE_ACCEPT );
	}
	
}
