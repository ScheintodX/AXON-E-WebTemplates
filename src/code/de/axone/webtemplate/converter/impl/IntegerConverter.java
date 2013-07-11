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
	
	private static final HashMap<Locale,IntegerConverter> FOR_LOCALE =
		new HashMap<Locale,IntegerConverter>();
	
	public static synchronized IntegerConverter forLocale( Locale locale ){
		return forLocale( locale, true );
	}
	public static synchronized IntegerConverter forLocale( Locale locale, boolean useThousandsSeparator ){
		
		IntegerConverter result = FOR_LOCALE.get( locale );
		if( result == null ){
			result = new IntegerConverter( locale, useThousandsSeparator );
			FOR_LOCALE.put( locale, result );
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
