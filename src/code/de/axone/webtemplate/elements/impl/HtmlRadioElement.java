package de.axone.webtemplate.elements.impl;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.Renderer;
import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.element.AbstractHtmlInputElement;
import de.axone.webtemplate.form.Translator;

public class HtmlRadioElement extends AbstractHtmlInputElement {

	private static String TAGNAME = "div";

	private List<Option> options;
	private String selected;
	private String name;

	public enum InputType {
		TEXT, PASSWORD, HIDDEN
	};

	public HtmlRadioElement( String name, List<Option> options ) {
		
		this( name, null, options );
	}
	public HtmlRadioElement( String name, String value, List<Option> options ) {

		super(TAGNAME, null);
		
		setAttribute( ATTRIBUTE_ID, "input_"+name );
		
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
		return name;
	}

	@Override
	public void setName( String name ) {
		this.name = name;
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


	/*  ************** OptionRenderer *************** */

	private class OptionRenderer implements Renderer {

		@Override
		public void render( Object object, HttpServletRequest request,
				HttpServletResponse response, Translator translator )
				throws IOException, WebTemplateException, Exception {

			for( Option option : options ) {

				String value = option.getValue();
				String text = option.getText();
				
				// the first part of the comparison is for speed
				if( value != null && value.length() > 6  && value.charAt(0) == '@'
						&& value.startsWith( "@@@" ) && value.endsWith( "@@@" ) ){
					value = translator.translate( value.substring( 3, value.length()-3 ) );
				}
				if( text != null && text.length() > 6  && text.charAt(0) == '@'
						&& text.startsWith( "@@@" ) && text.endsWith( "@@@" ) ){
					text = translator.translate( text.substring( 3, text.length()-3 ) );
				}

				HtmlInputElement inputElement = new HtmlInputElement( HtmlInputElement.InputType.RADIO, getName(), value );
				inputElement.setContent( text );
				inputElement.setIdAttribute( null );

				if( value != null && value.equals(selected) ) {
					inputElement.addAttribute( "checked", "checked" );
				}

				inputElement.render(object, request, response, translator);
			}
		}
	}
}
