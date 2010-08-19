package de.axone.webtemplate.form;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.element.HtmlInput;

public class WebFormImpl implements WebForm {

	private HashMap<String, FormValue<?>> connectorValues = new HashMap<String, FormValue<?>>();

	private Translator textProvider;

	private boolean showInvalid = false;

	@Override
	public void initialize( HttpServletRequest request ){

		for( FormValue<?> value : connectorValues.values() ){

			value.initialize( request );
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
	public void setTranslationProvider( Translator textProvider ) {

		this.textProvider = textProvider;
	}

	@Override
	public void addFormValue( String name, FormValue<?> connectorValue )
		throws WebTemplateException {

		if( connectorValues.containsKey( name ) )
			throw new WebTemplateException( "Connector " + name + " already exists" );

		connectorValues.put( name, connectorValue );
	}

	@Override
	public Set<String> getValueNames() {

		return connectorValues.keySet();
	}

	@Override
	public FormValue<?> getFormValue( String name ) {

		return connectorValues.get( name );
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
	public List<String> validate() {

		LinkedList<String> result = new LinkedList<String>();

		for( FormValue<?> value : connectorValues.values() ) {

			List<String> r = value.validate();

			LinkedList<String> messages = new LinkedList<String>();

			if( r == null || r.size() == 0 )
				continue;

			if( textProvider != null ) {

				for( String text : r ) {

					text = textProvider.translate( text );

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

		FormValue<?> value = this.getFormValue( name );
		
		if( value == null )
			throw new WebTemplateException( "Cannot find: " + name );
		
		return value.getHtmlInput();
	}

}
