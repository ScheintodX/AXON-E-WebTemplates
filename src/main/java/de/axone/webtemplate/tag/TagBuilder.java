package de.axone.webtemplate.tag;

import java.io.IOException;
import java.nio.charset.Charset;

import de.axone.web.SuperURL;
import de.axone.webtemplate.Renderer;

@SuppressWarnings( "unchecked" )
public class TagBuilder<X extends TagBuilder<X>> {

	protected TagRenderer target;
	
	public TagBuilder() {
		this.target = new TagRenderer();
	}
	
	public X cssClass( String cssClass ) {
		return attr( "class", cssClass );
	}
	public X cssId( String cssId ) {
		return attr( "id", cssId );
	}
	public X data( String name, String value ) {
		return attr( "data-" + name, value );
	}
	public X attr( String name, String value ) {
		target.attr( name, value );
		return (X)this;
	}
	
	public X content( String content ) {
		target.content( content );
		return (X)this;
	}
	
	public X append( String appendix ) {
		target.content( target.content() + appendix );
		return (X)this;
	}
	
	public X target( TagRenderer target ) {
		this.target = target;
		return (X)this;
	}
	
	public X dup() {
		return dup( target );
	}
	
	public X dup( TagRenderer target ) {
		return instance()
				.target( target.dup() )
				;
	}
	private X instance(){
		return (X) new TagBuilder<>();
	}
	
	public void write( Appendable a ) throws IOException {
		target.write( a );
	}
	
	public Renderer renderer() {
		return target;
	}
	
	@Override
	public String toString() {
		return target.toString();
	}
	
	public static class TagBuilderA extends TagBuilder<TagBuilderA> {
		
		{ target.name( "a" ); }
		
		public TagBuilderA href( String href ) {
			target.attr( "href", href );
			return this;
		}
		
		public TagBuilderA hrefPlain( String href ) {
			target.attrPlain( "href", href );
			return this;
		}

	}
	
	public static class TagBuilderMeta extends TagBuilder<TagBuilderMeta> {
		
		{ target.name( "meta" ); }
		
		public TagBuilderMeta name( String name ) {
			target.attr( "name", name );
			return this;
		}
		
		public TagBuilderMeta http_equiv( String equiv ) {
			target.attr( "http-equiv", equiv );
			return this;
		}
		
		public TagBuilderMeta charset( Charset charset ) {
			target.attr( "charset", charset.toString() );
			return this;
		}
	}
	
	public static class TagBuilderSpan extends TagBuilder<TagBuilderSpan> {
		
		{ target.name( "span" ); }
		
		public TagBuilderSpan(){}
		public TagBuilderSpan( String content ){
			content( content );
		}
	}
	
	public static class TagBuilderDiv extends TagBuilder<TagBuilderSpan> {
		
		{ target.name( "div" ); }
		
		public TagBuilderDiv(){}
		public TagBuilderDiv( String content ){
			content( content );
		}
	}
	
	public static class TagBuilderScript extends TagBuilder<TagBuilderScript> {
		
		{ target.name( "script" ); }
		
		public TagBuilderScript(){}
		public TagBuilderScript( String content ){
			content( content );
		}
		
		public TagBuilderScript src( SuperURL src ) {
			target.attr( "src", src.toAjax() );
			return this;
		}
	}
	
}
