package de.axone.webtemplate.tag;

import static de.axone.webtemplate.tag.TagBuilderAssert.*;

import org.testng.annotations.Test;

@Test( groups="webtemplates.html" )
public class HtmlTest {

	public void testTags() throws Exception {
		
		assertThat( Html.a().href( "link" ).content( "text" ) )
				.hasName( "a" )
				.containsOnlyTheseAttributeNames( "href" )
				.hasContent( "text" )
				.isRenderAs( "<a href=\"link\">text</a>" )
				;
		
		assertThat( Html.a().href( "&" ) )
				.isRenderAs( "<a href=\"&amp;\" />" )
				;
		
		assertThat( Html.a().hrefPlain( "&" ) )
				.isRenderAs( "<a href=\"&\" />" )
				;
		
	}
}
