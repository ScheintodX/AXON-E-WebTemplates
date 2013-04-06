package de.axone.webtemplate.elements.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.Renderer;
import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.element.AbstractHtmlInputElement;
import de.axone.webtemplate.form.TKey;
import de.axone.webtemplate.form.Translator;

public class HtmlRadioElement extends AbstractHtmlInputElement {

	private static String TAGNAME = "span";

	private List<Option> options;
	private String selected;
	private String name;
	private boolean lineBreak;

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
	
	public void setLineBreak( boolean lineBreak ){
		this.lineBreak = lineBreak;
	}
	public boolean isLineBreak(){
		return this.lineBreak;
	}

	// --- Lock Content ---
	@Override
	public void setContent( Object content ) {
		throw new IllegalArgumentException("No setting of content allowed here");
	}


	/*  ************** OptionRenderer *************** */

	private class OptionRenderer implements Renderer {

		@Override
		public void render( Object object, PrintWriter out, HttpServletRequest request,
				HttpServletResponse response, Translator translator )
				throws IOException, WebTemplateException, Exception {

			boolean first=true;
			for( Option option : options ) {
				
				if( first ) first = false;
				else if( lineBreak ) response.getWriter().println( "<br/>" );

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

				HtmlInputElement inputElement = new HtmlInputElement( HtmlInputElement.InputType.RADIO, getName(), value );
				String myId = getIdAttribute() + "_" + value;
				inputElement.setIdAttribute( myId );

				if( value != null && value.equals(selected) ) {
					inputElement.addAttribute( "checked", "checked" );
				}

				inputElement.render( object, out, request, response, translator );
				
				//out.write( text );
				HtmlLabelElement label = new HtmlLabelElement( myId, text );
				label.addAttribute( "class", "radio-label" );
				label.render( object, out, request, response, translator );
			}
		}
	}
}
