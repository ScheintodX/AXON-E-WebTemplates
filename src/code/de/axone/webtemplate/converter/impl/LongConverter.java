package de.axone.webtemplate.converter.impl;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import de.axone.webtemplate.converter.AbstractConverter;
import de.axone.webtemplate.converter.ConverterException;

public class LongConverter extends AbstractConverter<Long> {
	
	private final NumberFormat numberFormat;
	
	public LongConverter(){
		this( null );
	}
	public LongConverter( Locale locale ){
		
		if( locale != null ){
			numberFormat = NumberFormat.getIntegerInstance( locale );
		} else {
			numberFormat = null;
		}
	}

	@Override
	public Long convertFromString( String value )
		throws ConverterException {
		
		if( value == null )
			return null;
		
		if( value.trim().length() == 0 )
			return null;
		
		try {
			if( numberFormat != null ){
				return numberFormat.parse( value ).longValue();
			} else {
				return Long.valueOf( value );
			}
			
		} catch( ParseException e ) {
			throw new ConverterException( e );
		}
	}
	
	@Override
	public String convertToString( Long number )
		throws ConverterException {
		
		if( number == null ) return null;
		
		if( numberFormat != null ){
			return numberFormat.format( number.longValue() );
		} else {
			return Long.toString( number );
		}
	}

}
