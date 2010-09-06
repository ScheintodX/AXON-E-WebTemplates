package de.axone.webtemplate.converter.impl;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import de.axone.webtemplate.converter.AbstractConverter;
import de.axone.webtemplate.converter.ConverterException;

public class LongConverter extends AbstractConverter<Long> {
	
	private NumberFormat numberFormat;
	
	public LongConverter( Locale locale ){
		
		numberFormat = NumberFormat.getIntegerInstance( locale );
	}

	@Override
	public Long convertFromString( String value )
		throws ConverterException {
		
		if( value == null )
			return null;
		
		if( value.trim().length() == 0 )
			return null;
		
		try {
			return numberFormat.parse( value ).longValue();
			
		} catch( ParseException e ) {
			throw new ConverterException( e );
		}
	}
	
	@Override
	public String convertToString( Long number )
		throws ConverterException {
		
		if( number == null ) return null;
		
		return numberFormat.format( number.longValue() );
	}

}
