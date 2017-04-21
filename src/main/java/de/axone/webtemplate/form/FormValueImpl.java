package de.axone.webtemplate.form;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import de.axone.webtemplate.converter.Converter;
import de.axone.webtemplate.converter.ConverterException;
import de.axone.webtemplate.element.HtmlInput;
import de.axone.webtemplate.validator.Validator;

public class FormValueImpl<T> implements FormValue<T> {
	
	protected HtmlInput htmlInput;
	
	protected Converter<T> converter;
	
	protected LinkedList<Validator<? super T>> validators = new LinkedList<>();
	
	private final Class<T> type;
	
	public static <X> FormValueImpl<X> create( Class<X> type ) {
		return new FormValueImpl<>( type );
	}
	
	protected FormValueImpl( Class<T> type ) {
		
		this.type = type;
	}
	
	@Override
	public Class<T> type() {
		return type;
	}
	
	@Override
	public String toString() {
		
		StringBuilder result = new StringBuilder();
		
		result.append( htmlInput.getValue() )
				.append( " (" )
				.append( htmlInput.getClass().getSimpleName() )
				.append( " : " )
				.append( converter.getClass().getSimpleName() )
				;
		
		for( Validator<? super T> validator : validators ){
			
			result.append( " / " )
					.append( validator.getClass().getSimpleName() );
			
		}
		
		result.append( ")" );
		
		return result.toString();
	}

	
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
	public void readValue( HttpServletRequest request ) {
		htmlInput.initialize( request );
	}
	
	@Override
	public void readValue( Map<String,String> map ) {
		htmlInput.initialize( map );
	}
	

	@Override
	public boolean isValid() {
		
		return validate( null ).size() == 0;
	}

	@Override
	public List<String> validate( @Nullable Translator t ) {
		
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
			
			String text = validator.validate( value, t );
			
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
	public T getValue() throws ConverterException {
		
		return converter.convertFromString( getPlainValue() );
		
	}
	
	@Override
	public void setValue( T value ) throws ConverterException {
		
		htmlInput.setValue( converter.convertToString( value ) );
	}
	
	
	@Override
	public String getPlainValue() {
		
		return htmlInput.getValue();
	}
	

	@Override
	public void setConverter( Converter<T> converter ) {
		
		this.converter = converter;
	}

}
