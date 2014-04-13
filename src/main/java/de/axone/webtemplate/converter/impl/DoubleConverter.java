package de.axone.webtemplate.converter.impl;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import de.axone.webtemplate.converter.AbstractConverter;
import de.axone.webtemplate.converter.ConverterException;

public class DoubleConverter extends AbstractConverter<Double> {
	
	private NumberFormat numberFormat;
	
	public DoubleConverter( Locale locale ){
		
		numberFormat = NumberFormat.getNumberInstance( locale );
	}

	@Override
	public Double convertFromString( String value )
		throws ConverterException {
		
		if( value == null )
			return null;
		
		if( value.trim().length() == 0 )
			return null;
		
		try {
			return numberFormat.parse( value ).doubleValue();
			
		} catch( ParseException e ) {
			throw new ConverterException( e );
		}
	}
	
	@Override
	public String convertToString( Double number )
		throws ConverterException {
		
		if( number == null ) return null;
		
		return numberFormat.format( number.doubleValue() );
	}

}
