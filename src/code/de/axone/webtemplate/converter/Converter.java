package de.axone.webtemplate.converter;

import de.axone.webtemplate.validator.Validator;


public interface Converter<T> extends Validator<String>{

	public T convertFromString( String value )
		throws ConverterException;
	
	public String convertToString( T value )
		throws ConverterException;
}
