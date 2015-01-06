package de.axone.webtemplate.elements.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.Renderer;
import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.element.AbstractHtmlInputElement;
import de.axone.webtemplate.element.HtmlElement;
import de.axone.webtemplate.form.TKey;
import de.axone.webtemplate.form.Translator;

public class HtmlSelectElement extends AbstractHtmlInputElement {

	private static final String TAGNAME = "select";

	private static final String ATTRIBUTE_NAME = "name",
	                            ATTRIBUTE_SIZE = "size",
	                            ATTRIBUTE_MULTIPLE = "multiple";
	
	private static final String NOVAL = "";

	private static List<String> ATTRIBUTES = Arrays.asList(ATTRIBUTE_NAME,
			ATTRIBUTE_SIZE, ATTRIBUTE_MULTIPLE);

	private List<Option> options;
	private String selected;

	public HtmlSelectElement( String name, List<Option> options ) {
		
		this( name, null, options );
	}
	public HtmlSelectElement( String name, String value, List<Option> options ) {

		super(TAGNAME, ATTRIBUTES);

		setOptions(options);
		setName(name);
		setValue(value);

		// This is "super" because this method is forbidden in this class
		// because the content is generated vie OptionRenderer
		super.setContent( new OptionRenderer() );
	}

	// --- Name ---
	@Override
	public String getName() {
		return getAttribute(ATTRIBUTE_NAME);
	}

	@Override
	public void setName( String name ) {
		setAttribute(ATTRIBUTE_NAME, name);
		setAttribute(ATTRIBUTE_ID, "input_" + name);
	}

	// --- Value ---
	@Override
	public String getValue() {
		return selected;
	}

	@Override
	public void setValue( String value ) {
		selected = value;
	}

	// --- Options ---
	public void setOptions( List<Option> options ) {
		this.options = options;
	}

	public List<Option> getOptions() {
		return options;
	}

	// --- Lock Content ---
	@Override
	public void setContent( Object content ) {
		throw new IllegalArgumentException("No setting of content allowed here");
	}

	/*  ************** HtmlOptionElement *************** */

	public static class HtmlOptionElement extends HtmlElement {

		private static final String TAGNAME = "option";

		private static final String ATTRIBUTE_VALUE = "value";
		private static final String ATTRIBUTE_SELECTED = "selected";
		
		private static final List<String> ATTRIBUTES = Arrays.asList(
				ATTRIBUTE_VALUE, ATTRIBUTE_SELECTED);

		public HtmlOptionElement( String value, String text ) {

			super( TAGNAME, ATTRIBUTES );

			setContent(text);
			setAttribute(ATTRIBUTE_VALUE, value);
		}

		public void setSelected( boolean selected ) {
			if( selected )
				setAttribute(ATTRIBUTE_SELECTED, ATTRIBUTE_SELECTED);
			else
				setAttribute(ATTRIBUTE_SELECTED, null);
		}
	}

	/*  ************** OptionRenderer *************** */

	private class OptionRenderer implements Renderer {
		
		@Override
		public void render( Object object , PrintWriter out , HttpServletRequest request ,
				HttpServletResponse response , Translator translator , ContentCache cache  )
				throws IOException, WebTemplateException, Exception {

			for( Option option : options ) {
				
				String value = option.getValue();
				String text = option.getText();
				
				// the first part of the comparison is for speed
				if( value != null && value.length() > 6  && value.charAt(0) == '@'
						&& value.startsWith( "@@@" ) && value.endsWith( "@@@" ) ){
					value = translator.translate( TKey.dynamic( value.substring( 3, value.length()-3 ) ) );
				}
				if( text != null && text.length() > 6  && text.charAt(0) == '@'
						&& text.startsWith( "@@@" ) && text.endsWith( "@@@" ) ){
					text = translator.translate( TKey.dynamic( text.substring( 3, text.length()-3 ) ) );
				}

				if( value == null ) value = NOVAL;
				
				HtmlOptionElement optionElement = new HtmlOptionElement( value, text );
				
				if( value != NOVAL && value.equals(selected) ) {
					optionElement.setSelected(true);
				}else{
					optionElement.setSelected(false);
				}

				optionElement.render( object, out, request, response, translator, cache );
			}

		}

	}

	/*  ************** Option *************** */

	public static class OptionComparator implements Comparator<Option>, Serializable {

		@Override
		public int compare( Option o1, Option o2 ) {

			if( o1 == null && o2 == null )
				return 0;
			if( o1 == null )
				return -1;
			if( o2 == null )
				return 1;

			return o1.getText().compareTo(o2.getText());
		}

	}

}
