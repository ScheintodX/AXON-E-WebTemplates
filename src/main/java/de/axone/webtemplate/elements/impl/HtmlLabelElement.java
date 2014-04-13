package de.axone.webtemplate.elements.impl;

import de.axone.webtemplate.element.HtmlElement;

public class HtmlLabelElement extends HtmlElement {

	public HtmlLabelElement( String targetId, String value ) {
		
		super( "label" );
		
		addAttribute( "for", targetId );
		
		this.setContent( value );
	}

}
