package de.axone.webtemplate.validator;

import javax.annotation.Nullable;

import de.axone.webtemplate.form.Translator;



public interface Validator<T> {
	
	public boolean isValid( T value );
	
	public String validate( T value, @Nullable Translator t );
}
