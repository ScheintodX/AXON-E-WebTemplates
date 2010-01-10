package de.axone.webtemplate.elements.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.axone.webtemplate.converter.ConverterException;
import de.axone.webtemplate.element.AbstractHtmlInputElement;
import de.axone.webtemplate.element.FormValueFactory;
import de.axone.webtemplate.form.FormValue;

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
		return getChecked() ? "1" : "0";
	}

	@Override
	public void setValue( String value ) {
		if( value == null ) {
			setChecked( false );
		} else {
			setChecked( value.equals( getCheckedValue() ) );
		}
	}

	public void setCheckedValue( String value ) {
		setAttribute( ATTRIBUTE_VALUE, value );
	}

	public String getCheckedValue() {
		return getAttribute( ATTRIBUTE_VALUE );
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

	/**
	 * @deprecated This method is too static
	 */
	public static FormValue<Integer> createIntegerValue( InputType type,
			Locale locale, String name, Integer min, Integer max )
			throws ConverterException {

		return new FormValueFactory().createCheckboxIntegerValue( type, locale,
				name, min, max );
	}

	/**
	 * @deprecated This method is too static
	 */
	public static FormValue<Integer> createIntegerValue( String name,
			Locale locale, Integer min, Integer max ) throws ConverterException {
		return new FormValueFactory().createCheckboxIntegerValue( name, locale, min, max );
	}

	/**
	 * @deprecated This method is too static
	 */
	public static FormValue<Integer> createIntegerValue( String name,
			Locale locale ) throws ConverterException {
		return new FormValueFactory().createCheckboxIntegerValue( name, locale );
	}

	/**
	 * @deprecated This method is too static
	 */
	public static FormValue<Integer> createBooleanValue( String name )
			throws ConverterException {

		return new FormValueFactory().createCheckboxBooleanValue( name );
	}
}
