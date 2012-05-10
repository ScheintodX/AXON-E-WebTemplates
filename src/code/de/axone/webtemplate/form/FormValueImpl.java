package de.axone.webtemplate.form;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import de.axone.webtemplate.converter.Converter;
import de.axone.webtemplate.converter.ConverterException;
import de.axone.webtemplate.element.HtmlInput;
import de.axone.webtemplate.validator.Validator;

public class FormValueImpl<T> implements FormValue<T> {
	
	protected HtmlInput htmlInput;
	protected Converter<T> converter;
	protected LinkedList<Validator<? super T>> validators = new LinkedList<Validator<? super T>>();

	@Override
	public void addValidator( Validator<? super T> validator ) {
		validators.addLast( validator );
	}

	@Override
	public HtmlInput getHtmlInput() {
		return htmlInput;
	}

	@Override
	public void setHtmlInput( HtmlInput htmlElement ) {
		this.htmlInput = htmlElement;
		
	}
	@Override
	public void initialize( HttpServletRequest request ) {
		htmlInput.initialize( request );
	}

	@Override
	public boolean isValid() {
		
		return validate().size() == 0;
	}

	@Override
	public List<String> validate() {
		
		LinkedList<String> result = new LinkedList<String>();
		
		T value = null;
		try {
			value = converter.convertFromString( htmlInput.getValue() );
		} catch( ConverterException e ) {
			result.add( e.getMessage() );
			htmlInput.setValid( false );
			return result;
		}
		
		for( Validator<? super T> validator : validators ){
			
			String text = validator.validate( value );
			
			if( text != null ){
				result.addLast( text );
			}
		}
		if( result.size() > 0 ){
			htmlInput.setValid( false );
		}
		return result;
	}

	@Override
	public T getValue() 
		throws ConverterException {
		return converter.convertFromString( htmlInput.getValue() );
	}
	
	@Override
	public void setValue( T value )
		throws ConverterException {
		
		/*
		E.rr( value.getClass() );
		E.rr( converter.getClass() );
		E.rr( htmlInput.getClass() );
		E.rr( converter.getClass() );
		*/
		
		htmlInput.setValue( converter.convertToString( value ) );
	}
	
	@Override
	public String getPlainValue(){
		
		return htmlInput.getValue();
	}

	@Override
	public void setConverter( Converter<T> converter ) {
		
		this.converter = converter;
	}
	
	@Override
	public String toString(){
		return "[FV:" + getPlainValue() + "]";
	}

}
