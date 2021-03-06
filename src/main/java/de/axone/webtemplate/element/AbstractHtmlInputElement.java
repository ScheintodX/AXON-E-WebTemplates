package de.axone.webtemplate.element;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.Decorator;
import de.axone.webtemplate.form.Translator;

public abstract class AbstractHtmlInputElement extends HtmlElement implements HtmlInput {
	
	protected boolean valid = true;
	protected List<String> messages;
	private Decorator decorator;
	
	public AbstractHtmlInputElement(String tagName, List<String> attributes) {
		super( tagName, attributes );
	}
	
	@Override
	public void initialize( HttpServletRequest request ) {
		
		String value = request.getParameter( getName() );

		if( value != null ){
			setValue( value );
		}
	}
	
	@Override
	public void initialize( Map<String,String> map ) {
		
		String value = map.get( getName() );

		if( value != null ){
			setValue( value );
		}
	}
	
	@Override
	public void render( Object object , PrintWriter out , HttpServletRequest request ,
			HttpServletResponse response , Translator translator , ContentCache cache  )
			throws Exception {
		
		if( decorator != null ){
			decorator.render( this, object, out, request, response, translator, cache, isValid(), getMessages() );
			
		} else {
			renderElement( object, out, request, response, translator, cache );
		}
	}
	
	@Override
	public void renderElement( Object object, PrintWriter out, HttpServletRequest request,
			HttpServletResponse response, Translator translator, ContentCache cache )
			throws Exception {
		
		super.render( object, out, request, response, translator, cache );
		
	}

	@Override
	public void setValid( boolean isValid ){
		this.valid = isValid;
	}
	@Override
	public boolean isValid(){
		return valid;
	}
	
	@Override
	public void setMessages( List<String> messages ){
		this.messages = messages;
	}
	@Override
	public List<String> getMessages(){
		return messages;
	}
	
	@Override
	public void setDecorator( Decorator decorator ){
		this.decorator = decorator;
	}
	protected Decorator getDecorator(){
		return decorator;
	}
}
