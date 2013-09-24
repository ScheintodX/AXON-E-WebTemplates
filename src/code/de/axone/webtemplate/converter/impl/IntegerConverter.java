package de.axone.webtemplate.converter.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;

import de.axone.webtemplate.converter.AbstractConverter;
import de.axone.webtemplate.converter.ConverterException;

public class IntegerConverter extends AbstractConverter<Integer> {
	
	private final NumberFormat numberFormat;
	
	private static final HashMap<String,IntegerConverter> FOR_LOCALE_X =
		new HashMap<>();
	
	public static synchronized IntegerConverter instance( Locale locale ){
		return instance( locale, true );
	}
	public static synchronized IntegerConverter instance( Locale locale, boolean useThousandsSeparator ){
		
		String key = locale.toString() + (useThousandsSeparator ? "_X" : "");
		IntegerConverter result = FOR_LOCALE_X.get( key );
		if( result == null ){
			result = new IntegerConverter( locale, useThousandsSeparator );
			FOR_LOCALE_X.put( key, result );
		}
		return result;
	}
	
	public IntegerConverter( Locale locale ){
		this( locale, true );
	}
	public IntegerConverter( Locale locale, boolean useThousandsSeparator ){
		
		numberFormat = NumberFormat.getIntegerInstance( locale );
		if( ! useThousandsSeparator ){
			if( !( numberFormat instanceof DecimalFormat ) )
				throw new IllegalStateException( "Didn't get the right number format" );
			
			((DecimalFormat) numberFormat).setGroupingUsed( false );
		}
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
	public String convertToString( Integer number )
		throws ConverterException {
		
		if( number == null ) return null;
		return numberFormat.format( number.longValue() );
	}

}
