package de.axone.webtemplate.elements.impl;

import java.util.Arrays;
import java.util.List;

import de.axone.webtemplate.element.AbstractHtmlInputElement;

public class HtmlTextAreaElement extends AbstractHtmlInputElement {
	
	private static String TAGNAME = "textarea";

	private static String ATTRIBUTE_NAME = "name";
	
	public static final String ATTRIBUTE_COLS = "cols";
	public static final String ATTRIBUTE_ROWS = "rows";
	public static final String ATTRIBUTE_WRAP = "rows";
	
	private static List<String> ATTRIBUTES = Arrays.asList( ATTRIBUTE_NAME );

	public HtmlTextAreaElement( String name ){
		
		this( name, null );
	}

	public HtmlTextAreaElement( String name, String value ) {
		
		super( TAGNAME, ATTRIBUTES );

		setName( name );
		setValue( value );
		
		addAttribute( ATTRIBUTE_COLS );
		addAttribute( ATTRIBUTE_ROWS );
		addAttribute( ATTRIBUTE_WRAP );
	}
	
	@Override
	public boolean hasContent(){
		// TextArea always has content
		return true;
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
		// Has to be super because setContent is blocked here
		super.setContent( value );
	}

	// --- Content ---
	@Override
	public void setContent( Object object ){
		throw new IllegalArgumentException( "setContent cannot be used in TextArea element because the content is the value." );
	}
	
	// --- Additional Attributes ---
	public void setCols( int cols ){
		if( cols <= 0 ) throw new IllegalArgumentException( "cols <= 0" );
		setAttribute( ATTRIBUTE_COLS, ""+cols );
	}
	
	public void setRows( int rows ){
		if( rows <= 0 ) throw new IllegalArgumentException( "rows <= 0" );
		setAttribute( ATTRIBUTE_COLS, ""+rows );
	}
	
	public void setWrap( WrapType type ){
		setAttribute( ATTRIBUTE_WRAP, type.name() );
	}
}
