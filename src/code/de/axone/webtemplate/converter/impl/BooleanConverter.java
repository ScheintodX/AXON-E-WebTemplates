package de.axone.webtemplate.converter.impl;

import de.axone.tools.E;
import de.axone.tools.EasyParser;
import de.axone.webtemplate.converter.AbstractConverter;
import de.axone.webtemplate.converter.ConverterException;

public class BooleanConverter extends AbstractConverter<Boolean> {
	
	@Override
	public Boolean convertFromString( String value )
		throws ConverterException {
		
		E.rr( "from string: " + value );
		
		if( value == null )
			return null;
		
		if( value.trim().length() == 0 )
			return null;
		
		return EasyParser.yesOrNoOrNull( value );
	}
	
	@Override
	public String convertToString( Boolean number )
		throws ConverterException {
		
		E.rr( "to string: " + number );
		
		if( number == null ) return null;
		
		return number ? "true" : "false";
	}

}
