package de.axone.webtemplate.converter.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import de.axone.webtemplate.converter.AbstractConverter;
import de.axone.webtemplate.converter.ConverterException;


public class BigDecimalConverter extends AbstractConverter<BigDecimal> {
	
	public static final NumberFormat EUR_DE_FORMAT = NumberFormat.getNumberInstance( Locale.GERMANY );
	static{
		EUR_DE_FORMAT.setMaximumFractionDigits( 2 );
		EUR_DE_FORMAT.setMinimumFractionDigits( 2 );
		EUR_DE_FORMAT.setRoundingMode( RoundingMode.HALF_UP );
	}
	
	private NumberFormat numberFormat;
	
	public BigDecimalConverter( NumberFormat numberFormat ){
		
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
			Number number = numberFormat.parse( value );
			return new BigDecimal( number.doubleValue() );
			
		} catch( ParseException e ) {
			throw new ConverterException( e );
		}
	}
	
	@Override
	public String convertToString( BigDecimal number )
		throws ConverterException {
		
		if( number == null ) return null;
		
		return numberFormat.format( number.doubleValue() );
	}

}
