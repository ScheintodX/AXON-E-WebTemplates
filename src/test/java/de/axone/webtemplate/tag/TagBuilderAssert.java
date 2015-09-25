package de.axone.webtemplate.tag;

import static org.testng.Assert.*;

import java.io.IOException;

import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;

import de.axone.tools.Mapper;
import de.axone.webtemplate.tag.TagRenderer.Attr;

public class TagBuilderAssert extends AbstractObjectAssert<TagBuilderAssert,TagBuilder<?>> {

	protected TagBuilderAssert( TagBuilder<?> actual ) {
		super( actual, AbstractObjectAssert.class );
	}
	
	public TagBuilderAssert hasName( String name ) {
		
		assertEquals( actual.target.name(), name );
		
		return this;
	}
	
	public TagBuilderAssert hasContent( String content ) {
		
		assertEquals( actual.target.content(), content );
		
		return this;
	}
	
	public TagBuilderAssert isRenderAs( String result ) throws IOException {
		
		StringBuilder out = new StringBuilder();
		actual.write( out );
		String rendered = actual.toString();
		
		assertEquals( rendered, result );
		
		return this;
	}
	
	public TagBuilderAssert containsOnlyTheseAttributeNames( String ... attributes ) {
		
		Assertions.assertThat( actual.target.attrs().keySet() )
				.isEqualTo( Mapper.hashSet( attributes ) )
				;
		
		return this;
	}
	
	public TagBuilderAssert containsAttribute( String key, String value ) {
		
		Assertions.assertThat( actual.target.attrs() )
				.containsEntry( key, new Attr( value ) )
				;
		
		return this;
	}
		
	public static TagBuilderAssert assertThat( TagBuilder<?> actual ) {
		return new TagBuilderAssert( actual );
	}

}
