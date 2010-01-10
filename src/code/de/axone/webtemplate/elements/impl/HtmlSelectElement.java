package de.axone.webtemplate.elements.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.DefaultDecorator;
import de.axone.webtemplate.Renderer;
import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.converter.impl.StringConverter;
import de.axone.webtemplate.element.AbstractHtmlInputElement;
import de.axone.webtemplate.element.HtmlElement;
import de.axone.webtemplate.form.FormValue;
import de.axone.webtemplate.form.FormValueImpl;
import de.axone.webtemplate.form.Translator;
import de.axone.webtemplate.validator.impl.InCollectionValidator;
import de.axone.webtemplate.validator.impl.NotNullValidator;

public class HtmlSelectElement extends AbstractHtmlInputElement {
	
	private static String TAGNAME = "select";

	private static String ATTRIBUTE_NAME = "name";
	private static String ATTRIBUTE_SIZE = "size";
	private static String ATTRIBUTE_MULTIPLE = "size";
	
	private static List<String> ATTRIBUTES = Arrays.asList( 
			ATTRIBUTE_NAME, ATTRIBUTE_SIZE, ATTRIBUTE_MULTIPLE );
	
	private List<Option> options;
	private String selected;

	public enum InputType {
		TEXT, PASSWORD, HIDDEN
	};
	
	public HtmlSelectElement( String name, List<Option> options ){
		this( name, null, options );
	}

	public HtmlSelectElement( String name, String value, List<Option> options ) {
		
		super( TAGNAME, ATTRIBUTES );

		setOptions( options );
		setName( name );
		setValue( value );
		
		// This is "super" because the method is 
		// locked in "this".
		super.setContent( new OptionRenderer() );
		
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
		return selected;
	}
	@Override
	public void setValue( String value ) {
		selected = value;
	}

	// --- Options ---
	public void setOptions( List<Option> options ){
		this.options = options;
	}
	public List<Option> getOptions(){
		return options;
	}

	// --- Lock Content ---
	@Override
	public void setContent( Object content ){
		throw new IllegalArgumentException( "No setting of content allowed here" );
	}
	
	
	/* ************** HtmlOptionElement *************** */
	
	public static class HtmlOptionElement extends HtmlElement {
		
		private static final String TAGNAME = "option";
		
		private static final String ATTRIBUTE_VALUE = "value";
		private static final String ATTRIBUTE_SELECTED = "selected";
		private static final List<String> ATTRIBUTES = Arrays.asList( ATTRIBUTE_VALUE, ATTRIBUTE_SELECTED );

		public HtmlOptionElement( Option option ) {
			
			super( TAGNAME, ATTRIBUTES );
			
			setContent( option.getText() );
			setAttribute( ATTRIBUTE_VALUE, option.getValue() );
		}
		
		public void setSelected( boolean selected ){
			if( selected ) setAttribute( ATTRIBUTE_SELECTED, ATTRIBUTE_SELECTED );
			else setAttribute( ATTRIBUTE_SELECTED, null );
		}
	}
	
	
	/* ************** OptionRenderer *************** */
	
	private class OptionRenderer implements Renderer {

		@Override
		public void render( Object object, HttpServletRequest request,
				HttpServletResponse response, Translator translator )
				throws IOException, WebTemplateException, Exception {
			
			
			for( Option option : options ){
				
				HtmlOptionElement optionElement = new HtmlOptionElement( option );
				
    			if( option.getValue() != null && option.getValue().equals( selected ) ){
    				optionElement.setSelected( true );
    			}
    			
    			optionElement.render( object, request, response, translator );
			}
		
		}
	
	}
	
	/* ************** Option *************** */
	
	public static interface Option {
		public String getValue();
		public String getText();
	}
	
	public static class OptionImpl implements Option {
		
		private String value;
		private String text;
		
		public OptionImpl( String value, String text ){
			this.value = value;
			this.text = text;
		}
		
		public String getValue() { return value; }
		public void setValue( String value ) { this.value = value; }
		
		public String getText() { return text; }
		public void setText( String text ) { this.text = text; }
		
	}
	
	public static class OptionComparator implements Comparator<Option>{

		@Override
		public int compare( Option o1, Option o2 ) {
			
			if( o1 == null && o2 == null ) return 0;
			if( o1 == null ) return -1;
			if( o2 == null ) return 1;
			
			return o1.getText().compareTo( o2.getText() );
		}
		
	}
	
	
	/* ************** FACTORY METHODS *************** */
	
	/**
	 * @deprecated Too static
	 */
	public static FormValue<String> createSelectValue( String name, List<Option> options, boolean nullable ){
		
		FormValue<String> result = new FormValueImpl<String>();
		
		HtmlSelectElement element = new HtmlSelectElement( name, null, options );
		element.setDecorator( new DefaultDecorator() );
		StringConverter converter = new StringConverter();
		
		result.setHtmlInput( element );
		result.setConverter( converter );
		
		// value must be one defined.
		Set<String> keys = new HashSet<String>();
		for( Option option : options ){
			keys.add( option.getValue() );
		}
		result.addValidator( new InCollectionValidator<String>( keys ) );
		
		if( ! nullable ){
    		result.addValidator( new NotNullValidator() );
		}
		return result;
	}
	
	/**
	 * @deprecated Too static
	 */
	public static FormValue<String> createSelectValue( String name, Map<String,String> options, boolean nullable ){
		
		LinkedList<Option> optionsList = new LinkedList<Option>();
		
		for( String key : options.keySet() ){
			
			String value = options.get( key );
			optionsList.add( new OptionImpl( key, value ) );
		}
		
		Collections.sort( optionsList, new OptionComparator() );
		
		return createSelectValue( name, optionsList, nullable );
	}
	
}
