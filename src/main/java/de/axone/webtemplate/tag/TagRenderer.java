package de.axone.webtemplate.tag;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.exception.Assert;
import de.axone.web.encoding.Encoder_Attribute;
import de.axone.web.encoding.Encoder_Xml;
import de.axone.webtemplate.Renderer;
import de.axone.webtemplate.form.Translator;

public class TagRenderer implements Renderer {
	
	private String name;
	private Map<String,Attr> attrs;
	private boolean encodeContent;
	private String content;
	
	TagRenderer(){
		attrs = new HashMap<>();
	}
	
	private TagRenderer( String name, String content, Map<String,Attr> attrs, boolean encodeContent ) {
		this.name = name;
		this.attrs = attrs;
		this.encodeContent = encodeContent;
		this.content = content;
	}
	
	@Override
	public void render( Object object, PrintWriter out,
			HttpServletRequest request, HttpServletResponse response,
			Translator translator, ContentCache cache ) throws Exception {
		
		write( out );
	}
	
	@Override
	public String toString() {
		
		StringBuilder out = new StringBuilder();
		
		try {
			write( out );
		} catch( IOException e ) {
			throw new Error( "Cannot write to StringBuilder", e );
		}
		return out.toString();
	}
	
	<T extends Appendable> T write( T out ) throws IOException {
		
		Assert.notNull( name, "name" );
		// Null allowed: Content, args

		out
			.append( "<" )
			.append( name )
		;

		if( attrs != null ) for( Map.Entry<String,Attr> entry : attrs.entrySet() ){
			
			String argName = entry.getKey();
			Attr argValue = entry.getValue();

			out
				.append( " " )
				.append( argName )
				.append( "=\"" )
				.append( argValue.encode ? Encoder_Attribute.ENCODE( argValue.value ) : argValue.value  )
				.append( "\"" )
			;
		}

		if( content == null ){

    		out.append( " />" );
		} else {
			out
				.append( ">" )
    			.append( encodeContent ? Encoder_Xml.ENCODE( content ) : content )
    			.append( "</" )
    			.append( name )
    			.append( ">" )
			;
		}

		return out;
	}
	
	TagRenderer dup() {
		
		return new TagRenderer( name, content, new HashMap<>( attrs ), encodeContent );
	}
	
	void name( String name ) {
		this.name = name;
	}
	String name() {
		return name;
	}
	
	void content( String content ) {
		this.content = content;
	}
	String content() {
		return content;
	}
	
	void attr( String key, String value ) {
		attrs.put( key, new Attr( value ) );
	}
	void attrPlain( String key, String value ) {
		attrs.put( key, new Attr( value, false ) );
	}
	
	String attr( String key ) {
		Attr attr = attrs.get( key );
		if( attr == null ) return null;
		return attr.value;
	}
	
	Map<String,Attr> attrs() {
		return attrs;
	}
	
	static class Attr {
		
		private final String value;
		private final boolean encode;
		
		Attr( String value ) {
			this.value = value;
			this.encode = true;
		}
		Attr( String value, boolean encode ) {
			this.value = value;
			this.encode = encode;
		}
	}
}
