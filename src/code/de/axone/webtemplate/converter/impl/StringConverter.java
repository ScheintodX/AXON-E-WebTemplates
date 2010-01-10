package de.axone.webtemplate.converter.impl;

import de.axone.webtemplate.converter.AbstractConverter;
import de.axone.webtemplate.converter.ConverterException;

public class StringConverter extends AbstractConverter<String> {

	@Override
	public String convertFromString( String value ) throws ConverterException {
		return value;
	}

	@Override
	public String convertToString( String value ) throws ConverterException {
		return value;
	}

	@Override
	public String check( String value ){
		return null; // Doing nothing always works
	}

}
