package de.axone.webtemplate.converter.impl;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import de.axone.webtemplate.converter.AbstractConverter;
import de.axone.webtemplate.converter.ConverterException;

public class IntegerConverter extends AbstractConverter<Integer> {
	
	private NumberFormat numberFormat;
	
	public IntegerConverter( Locale locale ){
		
		numberFormat = NumberFormat.getIntegerInstance( locale );
	}

	@Override
	public Integer convertFromString( String value )
		throws ConverterException {
		
		if( value == null )
			return null;
		
		if( value.trim().length() == 0 )
			return null;
		
		try {
			return numberFormat.parse( value ).intValue();
			
		} catch( ParseException e ) {
			throw new ConverterException( e );
		}
	}
	
	@Override
	public String convertToString( Integer integer )
		throws ConverterException {
		
		if( integer == null ) return null;
		
		return ""+integer;
	}

}
