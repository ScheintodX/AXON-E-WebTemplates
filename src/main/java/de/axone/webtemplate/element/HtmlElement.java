package de.axone.webtemplate.element;

import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.web.encoding.Encoder_Attribute;
import de.axone.webtemplate.Renderer;
import de.axone.webtemplate.form.Translator;

/**
 * Base class for all Html Elements
 * 
 * @author flo
 */
public class HtmlElement implements Renderer {

	public static final String ATTRIBUTE_CLASS = "class";
	public static final String ATTRIBUTE_ID = "id";
	
	public enum WrapType {
		soft, hard, virtual, physical, off;
	};

	protected String tagName;
	protected LinkedHashMap<String,String> attributes;

	private Object content;

	/**
	 * Create a html tag/element with the given tag name
	 * 
	 * e.g. HtmlElement( "div" ) crates one div
	 * 
	 * @param tagName
	 */
	public HtmlElement( String tagName ){

		this( tagName, null );
	}

	/**
	 * Create a html element with the given tag name and 
	 * additional attribute declarations
	 * 
	 * <em>Attributes are not initialized but only declared.</em>
	 * 
	 * @param tagName
	 * @param addAttributes
	 */
	public HtmlElement( String tagName, List<String> addAttributes ){

		this.attributes = new LinkedHashMap<String,String>();

		setTagName( tagName );

		addAttribute( ATTRIBUTE_ID );
		addAttribute( ATTRIBUTE_CLASS);

		if( addAttributes != null ) for( String attributeName : addAttributes ){
			addAttribute( attributeName );
		}
	}

	@Override
	public void render( Object object , PrintWriter out , HttpServletRequest request ,
			HttpServletResponse response , Translator translator , ContentCache cache  ) throws Exception {

		out.write( '<' );
		out.write( getTagName() );

		// Render attributes
		for( Map.Entry<String,String> entry: attributes.entrySet() ){

			String attributeName = entry.getKey();
			String value = entry.getValue();
			
			if( value != null ) {

				out.write( ' ' );
				out.write( attributeName );
				out.write( "=\"" );
				out.write( Encoder_Attribute.ENCODE( value ) );
				out.write( '"' );
			}
		}

		// Render content if exists or empty tag
		if( hasContent() ) {

			out.write( '>' );

			renderContent( getContent(), object, out, request, response, translator, cache );

			out.write( "</" );
			out.write( getTagName() );
			out.write( '>' );
		} else {
			out.write( " />" );
		}
	}

	protected boolean hasContent(){
		return content != null;
	}

	protected void renderContent( Object content, Object object, PrintWriter out, HttpServletRequest request,
			HttpServletResponse response, Translator translator, ContentCache cache ) throws Exception {

		if( content != null ){
			
			if( content instanceof String ){
	
				out.write( (String)content );
	
			} else if( content instanceof Renderer ){
	
				((Renderer) content).render( object, out, request, response, translator, cache );
			} else {
				out.write( content.toString() );
			}
		}
	}

	public void setTagName( String tagName ){
		this.tagName = tagName;
	}
	public String getTagName(){
		return tagName;
	}

	public void addAttribute( String name ){
		this.attributes.put( name, null );
	}

	public void addAttribute( String name, String value ){
		this.attributes.put( name, value );
	}

	public void setAttribute( String key, String value ) {

		// Set Parameter casted to String which we will use
		// as such. This is for early fail.
		if( attributes.containsKey( key ) ) {

			attributes.put( key, value );

		} else {
			throw new IllegalArgumentException( key + " is not supported" );
		}
	}

	public String getAttribute( String key ) {

		if( attributes.containsKey( key ) ){
			return attributes.get( key );
		} else {
			throw new IllegalArgumentException( key + " is not supported" );
		}
	}
	
	public void setIdAttribute( String id ){
		setAttribute( ATTRIBUTE_ID, id );
	}
	public String getIdAttribute(){
		return getAttribute( ATTRIBUTE_ID );
	}
	
	public void setClassAttribute( String clazz ){
		setAttribute( ATTRIBUTE_CLASS, clazz );
	}
	public String getClassAttribute(){
		return getAttribute( ATTRIBUTE_CLASS );
	}
	public void addClassAttribute( String clazz ){
		if( clazz == null ) return;
		String oldClass = getClassAttribute();
		if( oldClass == null ) oldClass = "";
		if( oldClass.length() != 0 ) oldClass += " ";
		oldClass += clazz;
		setClassAttribute( oldClass );
	}

	public void setContent( Object content ){
		this.content = content;
	}

	public Object getContent(){
		return content;
	}
	
}
