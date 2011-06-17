/**
 * 
 */
package de.axone.webtemplate.elements.impl;

public class OptionImpl implements Option {

	private String value;
	private String text;

	public OptionImpl( String value, String text ) {
		this.value = value;
		this.text = text;
	}

	@Override
	public String getValue() {
		return value;
	}
	public void setValue( String value ) {
		this.value = value;
	}

	@Override
	public String getText() {
		return text;
	}
	public void setText( String text ) {
		this.text = text;
	}
}