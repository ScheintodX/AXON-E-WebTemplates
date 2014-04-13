package de.axone.webtemplate.elements.impl;

import java.util.Arrays;
import java.util.List;

import de.axone.webtemplate.element.AbstractHtmlInputElement;

public class HtmlCheckboxElement extends AbstractHtmlInputElement {

	private static String TAGNAME = "input";

	private static String ATTRIBUTE_TYPE = "type";
	private static String ATTRIBUTE_NAME = "name";
	private static String ATTRIBUTE_VALUE = "value";
	private static String ATTRIBUTE_CHECKED = "checked";

	private boolean checked = false;

	private static List<String> ATTRIBUTES = Arrays.asList( ATTRIBUTE_TYPE,
			ATTRIBUTE_NAME, ATTRIBUTE_VALUE, ATTRIBUTE_CHECKED );

	public enum InputType {
		CHECKBOX, RADIO
	};

	public HtmlCheckboxElement(String name) {
		this( name, null, InputType.CHECKBOX );
	}

	public HtmlCheckboxElement(String name, String value, InputType type) {

		super( TAGNAME, ATTRIBUTES );

		setName( name );

		setAttribute( ATTRIBUTE_VALUE, value ); // Note: Not "setValue" (this
												// would set checked)
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
		return getChecked() ? "true" : "false";
	}

	@Override
	public void setValue( String value ) {
	
		if( value == null ) {
			setChecked( false );
		} else {
			setChecked( true );
		}
	}

	public void setCheckedValue( String value ) {
		setAttribute( ATTRIBUTE_CHECKED, value );
	}

	public String getCheckedValue() {
		return getAttribute( ATTRIBUTE_CHECKED );
	}

	// --- is checked ---
	public void setChecked( boolean checked ) {
		this.checked = checked;

		if( checked ) {
			setAttribute( ATTRIBUTE_CHECKED, ATTRIBUTE_CHECKED );
		} else {
			setAttribute( ATTRIBUTE_CHECKED, null );
		}
	}

	public boolean getChecked() {
		return checked;
	}

}
