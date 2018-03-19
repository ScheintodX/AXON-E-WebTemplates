package de.axone.webtemplate.tag;

import de.axone.webtemplate.tag.TagBuilder.TagBuilderA;
import de.axone.webtemplate.tag.TagBuilder.TagBuilderDiv;
import de.axone.webtemplate.tag.TagBuilder.TagBuilderMeta;
import de.axone.webtemplate.tag.TagBuilder.TagBuilderScript;
import de.axone.webtemplate.tag.TagBuilder.TagBuilderSpan;

public abstract class Html {

	public static TagBuilderMeta meta() {
		return new TagBuilderMeta();
	}
	
	public static TagBuilderSpan span( String content ) {
		return new TagBuilderSpan( content );
	}
	
	public static TagBuilderScript script() {
		return new TagBuilderScript();
	}
	
	public static TagBuilderDiv div( String content ) {
		return new TagBuilderDiv( content );
	}
	
	public static TagBuilderA a() {
		return new TagBuilderA();
	}
	
}
