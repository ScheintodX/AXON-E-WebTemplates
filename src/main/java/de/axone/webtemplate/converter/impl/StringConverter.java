package de.axone.webtemplate.converter.impl;

import javax.annotation.Nullable;

import de.axone.webtemplate.converter.AbstractConverter;
import de.axone.webtemplate.converter.ConverterException;
import de.axone.webtemplate.form.Translator;

/**
 * Convert String to String
 * 
 * This doesn't do much
 * 
 * @author flo
 */
public class StringConverter extends AbstractConverter<String> {

	@Override
	public String convertFromString( String value ) throws ConverterException {
		if( value == null ) return null;
		return value.trim();
	}

	@Override
	public String convertToString( String value ) throws ConverterException {
		return value;
	}

	@Override
	public String validate( String value, @Nullable Translator t ){
		return null; // Doing nothing always works
	}

}
