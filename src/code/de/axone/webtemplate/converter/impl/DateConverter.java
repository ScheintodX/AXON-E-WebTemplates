package de.axone.webtemplate.converter.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.axone.webtemplate.converter.AbstractConverter;
import de.axone.webtemplate.converter.ConverterException;

public class DateConverter extends AbstractConverter<Date> {
	
	// TODO: Das verbessern / erweitern / verschieben
	private static final HashMap<Locale, DateConverter> YMDForLocale = new HashMap<Locale,DateConverter>();
	static{
		YMDForLocale.put( Locale.US, new DateConverter( new SimpleDateFormat( "MM/dd/yyyy" ) ) );
		YMDForLocale.put( Locale.GERMANY, new DateConverter( new SimpleDateFormat( "dd.MM.yyyy" ) ) );
	}
	private static final HashMap<Locale, DateConverter> YMForLocale = new HashMap<Locale,DateConverter>();
	static{
		YMForLocale.put( Locale.US, new DateConverter( new SimpleDateFormat( "MM/yyyy" ) ) );
		YMForLocale.put( Locale.GERMANY, new DateConverter( new SimpleDateFormat( "MM.yyyy" ) ) );
	}
	public static DateConverter YMDForLocale( Locale locale ){
		return YMDForLocale.get( locale );
	}
	public static DateConverter YMForLocale( Locale locale ){
		return YMForLocale.get( locale );
	}
	
	private DateFormat dateFormat;
	
	public DateConverter( DateFormat format ){
		
		this.dateFormat = format;
	}

	@Override
	public Date convertFromString( String value )
		throws ConverterException {
		
		
		if( value == null )
			return null;
		
		if( value.trim().length() == 0 )
			return null;
		
		try {
			// DateFormat is not threadsafe
			DateFormat df = (DateFormat) dateFormat.clone();
			return df.parse( value );
			
		} catch( ParseException e ) {
			throw new ConverterException( e );
		}
	}
	
	@Override
	public String convertToString( Date date )
		throws ConverterException {
		
		if( date == null ) return null;
		
		DateFormat df = (DateFormat) dateFormat.clone();
		
		return df.format( date );
	}

}
