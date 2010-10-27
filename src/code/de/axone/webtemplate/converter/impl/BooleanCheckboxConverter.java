package de.axone.webtemplate.converter.impl;

import de.axone.webtemplate.converter.AbstractConverter;
import de.axone.webtemplate.converter.ConverterException;

public class BooleanCheckboxConverter extends AbstractConverter<Boolean> {
	
	@Override
	public Boolean convertFromString( String value )
		throws ConverterException {
		
		if( value == null )
			return Boolean.FALSE;
		
		if( value.trim().length() == 0 )
			return Boolean.FALSE;
		
		return Boolean.TRUE;
	}
	
	@Override
	public String convertToString( Boolean value )
		throws ConverterException {
		
		if( value == null ) return null;
		
		return value ? "checked" : null;
	}

}
