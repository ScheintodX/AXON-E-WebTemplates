package de.axone.webtemplate.converter.impl;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import de.axone.webtemplate.converter.AbstractConverter;
import de.axone.webtemplate.converter.ConverterException;

public class ShortConverter extends AbstractConverter<Short> {
	
	private NumberFormat numberFormat;
	
	public ShortConverter( Locale locale ){
		
		numberFormat = NumberFormat.getIntegerInstance( locale );
	}

	@Override
	public Short convertFromString( String value )
		throws ConverterException {
		
		if( value == null )
			return null;
		
		if( value.trim().length() == 0 )
			return null;
		
		try {
			return numberFormat.parse( value ).shortValue();
			
		} catch( ParseException e ) {
			throw new ConverterException( e );
		}
	}
	
	@Override
	public String convertToString( Short number )
		throws ConverterException {
		
		if( number == null ) return null;
		
		return numberFormat.format( number.longValue() );
	}

}
