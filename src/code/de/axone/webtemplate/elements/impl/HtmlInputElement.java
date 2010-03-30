package de.axone.webtemplate.elements.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.axone.webtemplate.element.AbstractHtmlInputElement;
import de.axone.webtemplate.element.FormValueFactory;
import de.axone.webtemplate.form.FormValue;

public class HtmlInputElement extends AbstractHtmlInputElement {

	private static String NAME = "input";

	private static String ATTRIBUTE_TYPE = "type";
	private static String ATTRIBUTE_NAME = "name";
	private static String ATTRIBUTE_VALUE = "value";

	private static List<String> ATTRIBUTES = Arrays.asList( ATTRIBUTE_TYPE,
			ATTRIBUTE_NAME, ATTRIBUTE_VALUE );

	public enum InputType {
		TEXT, PASSWORD, HIDDEN
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

	/* ***************** FACTORY METHODS ******************** */

	/**
	 * @deprecated This method is too static
	 */
	public static FormValue<String> createTextValue( InputType type,
			String name, int length, boolean nullable ) {

		return new FormValueFactory().createInputTextValue( type, name, length,
				nullable );
	}

	/**
	 * @deprecated This method is too static
	 */
	public static FormValue<String> createTextValue( String name, int length,
			boolean nullable ) {

		return new FormValueFactory().createInputTextValue( name, length,
				nullable );
	}

	/**
	 * @deprecated This method is too static
	 */
	public static FormValue<String> createPasswordValue( String name,
			int length, boolean nullable ) {

		return new FormValueFactory().createInputPasswordValue( name, length,
				nullable );
	}

	/**
	 * @deprecated This method is too static
	 */
	public static FormValue<String> createPostalcodeValue( String name, 
			boolean nullable, FormValue<String> countryProvider ) {

		return new FormValueFactory().createInputPostalcodeValue( name, 
				nullable, countryProvider );
	}

	/**
	 * @deprecated This method is too static
	 */
	public static FormValue<String> createEMailValue( String name, int length,
			boolean nullable ) {

		return new FormValueFactory().createInputEMailValue( name, length,
				nullable );
	}

	/**
	 * @deprecated This method is too static
	 */
	public static FormValue<String> createUrlValue( String name, int length,
			boolean nullable ) {

		return new FormValueFactory().createInputUrlValue( name, length,
				nullable );
	}

	/**
	 * @deprecated This method is too static
	 */
	public static FormValue<String> createPhoneValue( String name, int length,
			boolean nullable ) {

		return new FormValueFactory().createInputPhoneValue( name, length,
				nullable );
	}

	/**
	 * @deprecated This method is too static
	 */
	public static FormValue<String> createCountryValue( String name,
			boolean nullable ) {

		return new FormValueFactory().createInputCountryValue( name, nullable );
	}

	/**
	 * @deprecated This method is too static
	 */
	public static FormValue<String> createLanguageValue( String name,
			boolean nullable ) {

		return new FormValueFactory().createInputLanguageValue( name, nullable );
	}

	/**
	 * @deprecated This method is too static
	 */
	public static FormValue<Integer> createIntegerValue( InputType type,
			Locale locale, String name, Integer min, Integer max,
			boolean nullable ) {

		return new FormValueFactory().createInputIntegerValue( type, locale,
				name, min, max, nullable );
	}

	/**
	 * @deprecated This method is too static
	 */
	public static FormValue<Integer> createIntegerValue( String name,
			Locale locale, Integer min, Integer max, boolean nullable ) {

		return new FormValueFactory().createInputIntegerValue( name, locale,
				min, max, nullable );
	}

	/**
	 * @deprecated This method is too static
	 */
	public static FormValue<Integer> createIntegerValue( String name,
			Locale locale, boolean nullable ) {

		return new FormValueFactory().createInputIntegerValue( name, locale,
				nullable );
	}

	/**
	 * @deprecated This method is too static
	 */
	public static FormValue<String> createHiddenValue( String name ) {

		return new FormValueFactory().createInputHiddenValue( name );
	}

}
