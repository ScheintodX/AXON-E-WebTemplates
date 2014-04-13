package de.axone.webtemplate.form;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import de.axone.webtemplate.converter.Converter;
import de.axone.webtemplate.converter.ConverterException;
import de.axone.webtemplate.element.HtmlInput;
import de.axone.webtemplate.validator.Validator;

public interface FormValue<T> {

	public void readValue( HttpServletRequest request );
	
	public void setHtmlInput( HtmlInput element );
	public HtmlInput getHtmlInput();
	
	public void setConverter( Converter<T> converter );
	public T getValue() throws ConverterException;
	public void setValue( T value ) throws ConverterException;
	
	public String getPlainValue();
	
	public void addValidator( Validator<? super T> validator );
	public boolean isValid();
	public List<String> validate();
	
}
