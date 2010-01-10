package de.axone.webtemplate.converter;

import de.axone.webtemplate.validator.AbstractValidator;

public abstract class AbstractConverter<T> extends AbstractValidator<String> implements Converter<T> {
	
	private static final String CANNOT_CONVERT = "CONVERTER_CANNOT_CONVERT";

	@Override
	protected String check( String value ) {
		try{
			convertFromString( value );
		} catch( ConverterException e ){
			return CANNOT_CONVERT;
		}
		return null;
	}

}
