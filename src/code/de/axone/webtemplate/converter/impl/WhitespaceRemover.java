package de.axone.webtemplate.converter.impl;

import de.axone.webtemplate.converter.AbstractConverter;
import de.axone.webtemplate.converter.ConverterException;

public class WhitespaceRemover extends AbstractConverter<String> {

	@Override
	public String convertFromString( String value ) throws ConverterException {
		
		if( value != null ){
    		return value.replaceAll( "\\s", "" );
		} else {
			return null;
		}
	}

	@Override
	public String convertToString( String value ) throws ConverterException {
		
		if( value != null ){
    		return value.replaceAll( "\\s", "" );
		} else {
			return null;
		}
	}


}
