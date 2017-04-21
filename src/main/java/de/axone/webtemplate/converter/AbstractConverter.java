package de.axone.webtemplate.converter;

import javax.annotation.Nullable;

import de.axone.webtemplate.form.Translator;
import de.axone.webtemplate.validator.AbstractValidator;

public abstract class AbstractConverter<T> extends AbstractValidator<String> implements Converter<T> {
	
	private static final String CANNOT_CONVERT = "CONVERTER_CANNOT_CONVERT";

	@Override
	public String validate( String value, @Nullable Translator t ) {
		try{
			convertFromString( value );
		} catch( ConverterException e ){
			return CANNOT_CONVERT;
		}
		return null;
	}

}
