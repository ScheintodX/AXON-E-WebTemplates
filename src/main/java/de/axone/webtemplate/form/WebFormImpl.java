package de.axone.webtemplate.form;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import de.axone.web.rest.RestRequest;
import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.element.HtmlInput;

/**
 * This is the base implementation a webform.
 * 
 * @see WebForm
 * @author flo
 *
 */
public class WebFormImpl implements WebForm {

	private HashMap<String, FormValue<?>> connectorValues = new HashMap<String, FormValue<?>>();

	private Translator translator;

	private boolean showInvalid = false;
	
	@Override
	public String getName(){
		return this.getClass().getSimpleName();
	}

	@Override
	public void readFromRequest( HttpServletRequest request ){

		for( FormValue<?> value : connectorValues.values() ){

			value.readValue( request );
		}
	}

	@Override
	public void readFromJsonRequest( RestRequest request ) throws JsonParseException, JsonMappingException, IOException {
		
		String data = request.getParameter( "data" );
		
		MapType type = TypeFactory.defaultInstance().constructMapType( HashMap.class, String.class, String.class );
		
		HashMap<String,String> map = request.mapper().readValue( data, type );
		
		for( FormValue<?> value : connectorValues.values() ){

			value.readValue( map );
		}
	}

	@Override
	public String getPlainValue( String name )
		throws WebTemplateException {

		FormValue<?> value = connectorValues.get( name );

		if( value == null )
			throw new WebTemplateException( "Cannot find: " + name );

		HtmlInput input = value.getHtmlInput();

		if( input == null )
			throw new WebTemplateException( name + " has no HtmlInput" );

		return input.getValue();
	}

	@Override
	public Set<String> getFormValueNames() {
		return connectorValues.keySet();
	}

	@Override
	public void setTranslationProvider( Translator translator ) {

		this.translator = translator;
	}

	@Override
	public void addFormValue( String name, FormValue<?> connectorValue )
		throws WebTemplateException {

		if( connectorValues.containsKey( name ) )
			throw new WebTemplateException( "Connector " + name + " already exists" );

		connectorValues.put( name, connectorValue );
	}

	@Override
	public <T> FormValue<T> getFormValue( Class<T> type, String name ) {

		FormValue<?> result = connectorValues.get( name );
		
		if( type != null && ! type.isAssignableFrom( result.type() ) )
				throw new IllegalArgumentException( "Requested " + type + " for FormValue '" + name + "' but was: " + result.type() );
		
		@SuppressWarnings( "unchecked" )
		FormValue<T> theResult = (FormValue<T>) result;
		
		return theResult;
	}
	@Override
	public void remFormValue( String name ) {
		
		connectorValues.remove( name );
	}

	@Override
	public boolean isValid() {

		boolean res = true;
		for( FormValue<?> value : connectorValues.values() ) {

			if( !value.isValid() )
				res = false;
		}
		return res;
	}

	@Override
	public void setShowInvalid( boolean showInvalid ) {

		this.showInvalid = showInvalid;
	}

	@Override
	public List<String> validate( @Nullable Translator t ) {

		LinkedList<String> result = new LinkedList<String>();

		for( FormValue<?> value : connectorValues.values() ) {

			List<String> r = value.validate( t );

			if( r == null || r.size() == 0 )
				continue;

			LinkedList<String> messages = new LinkedList<String>();

			if( translator != null ) {

				for( String text : r ) {

					// TODO: Das beisst sich mit der übersetzung im Decorator!!!
					text = translator.translate( TKey.dynamic( text ) );
					messages.addLast( text );
				}
			} else {
				messages.addAll( r );
			}

			if( showInvalid && messages.size() > 0 ){

				value.getHtmlInput().setValid( false );
				value.getHtmlInput().setMessages( messages );
			} else {
				value.getHtmlInput().setValid( true );
				value.getHtmlInput().setMessages( null );
			}

			result.addAll( messages );
		}

		if( result.size() == 0 ){
			return null;
		} else {
    		return result;
		}
	}

	@Override
	public HtmlInput getHtmlInput( String name ) throws WebTemplateException {

		FormValue<?> value = this.getFormValue( null, name );
		
		if( value == null )
			throw new WebTemplateException( "Cannot find " + getName() + "'s value: " + name );
		
		return value.getHtmlInput();
	}
	
	@Override
	public String toString(){
		
		StringBuilder result = new StringBuilder();
		
		result.append( getName() ).append( '\n' );
		
		for( String name : connectorValues.keySet() ){
			
			FormValue<?> value = connectorValues.get( name );
			
			result
					.append( name )
					.append( ": " )
					.append( value )
					.append( '\n' )
			;
		}
		
		return result.toString();
	}

}
