package de.axone.webtemplate.converter.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;

import de.axone.webtemplate.converter.AbstractConverter;
import de.axone.webtemplate.converter.ConverterException;


public class BigDecimalConverter extends AbstractConverter<BigDecimal> {
	
	private static final HashMap<Locale, BigDecimalConverter> FOR_LOCALE = new HashMap<Locale,BigDecimalConverter>();
	static {
	}
	
	public static synchronized BigDecimalConverter forLocale( Locale locale ){
		
		BigDecimalConverter result = FOR_LOCALE.get( locale );
		if( result == null ){
			result = new BigDecimalConverter( locale );
			FOR_LOCALE.put( locale, result );
		}
		return result;
	}
	
	private DecimalFormat numberFormat;
	
	public BigDecimalConverter( Locale locale ){
		
		NumberFormat f = NumberFormat.getNumberInstance( locale );
		if( !( f instanceof DecimalFormat ) )
			throw new NumberFormatException( "NumberFormat is of wrong Format" );
		DecimalFormat df = (DecimalFormat)f;
		df.setParseBigDecimal( true );
		df.setMaximumFractionDigits( 2 );
		df.setMinimumFractionDigits( 2 );
		df.setRoundingMode( RoundingMode.HALF_UP );
		
		this.numberFormat = df;
	}
	
	public BigDecimalConverter( DecimalFormat numberFormat ){
		
		this.numberFormat = numberFormat;
	}
	
	@Override
	public BigDecimal convertFromString( String value )
		throws ConverterException {
		
		if( value == null )
			return null;
		
		value = value.trim();
		
		if( value.length() == 0 )
			return null;
		
		try {
			DecimalFormat nf = (DecimalFormat) numberFormat.clone();
			BigDecimal number = (BigDecimal) nf.parse( value );
			return number;
			
		} catch( ParseException e ) {
			throw new ConverterException( e );
		}
	}
	
	@Override
	public String convertToString( BigDecimal number )
		throws ConverterException {
		
		if( number == null ) return null;
		
		DecimalFormat nf = (DecimalFormat) numberFormat.clone();
		return nf.format( number );
	}
}
