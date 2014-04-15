package de.axone.webtemplate.converter.impl;

import de.axone.tools.EasyParser;
import de.axone.webtemplate.converter.AbstractConverter;
import de.axone.webtemplate.converter.ConverterException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class BooleanConverter extends AbstractConverter<Boolean> {
	
	@Override
	@SuppressFBWarnings( "NP_BOOLEAN_RETURN_NULL" )
	public Boolean convertFromString( String value )
		throws ConverterException {
		
		if( value == null )
			return null;
		
		if( value.trim().length() == 0 )
			return null;
		
		return EasyParser.yesOrNoOrNull( value );
	}
	
	@Override
	public String convertToString( Boolean number )
		throws ConverterException {
		
		if( number == null ) return null;
		
		return number ? "true" : "false";
	}

}
