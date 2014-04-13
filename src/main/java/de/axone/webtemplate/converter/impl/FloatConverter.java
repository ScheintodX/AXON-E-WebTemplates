package de.axone.webtemplate.converter.impl;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import de.axone.webtemplate.converter.AbstractConverter;
import de.axone.webtemplate.converter.ConverterException;

public class FloatConverter extends AbstractConverter<Float> {
	
	private NumberFormat numberFormat;
	
	public FloatConverter( Locale locale ){
		
		numberFormat = NumberFormat.getNumberInstance( locale );
	}

	@Override
	public Float convertFromString( String value )
		throws ConverterException {
		
		if( value == null )
			return null;
		
		if( value.trim().length() == 0 )
			return null;
		
		try {
			return numberFormat.parse( value ).floatValue();
			
		} catch( ParseException e ) {
			throw new ConverterException( e );
		}
	}
	
	@Override
	public String convertToString( Float number )
		throws ConverterException {
		
		if( number == null ) return null;
		
		return numberFormat.format( number.floatValue() );
	}

}
