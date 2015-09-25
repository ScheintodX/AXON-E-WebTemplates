package de.axone.webtemplate.tag;

import de.axone.webtemplate.tag.TagBuilder.TagBuilderA;
import de.axone.webtemplate.tag.TagBuilder.TagBuilderMeta;

public abstract class Html {

	public static TagBuilderA a() {
		return new TagBuilderA();
	}
	
	public static TagBuilderMeta meta() {
		return new TagBuilderMeta();
	}
	
}
