package de.axone.webtemplate.element;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.Decorator;
import de.axone.webtemplate.Renderer.ContentCache;
import de.axone.webtemplate.form.Translator;

public interface HtmlInput {

	public void setName( String name );
	public String getName();
	
	public void setValue( String value );
	public String getValue();
	
	public void setValid( boolean isValid );
	public boolean isValid();
	public void setMessages( List<String> message );
	public List<String> getMessages();
	
	public void initialize( HttpServletRequest request );
	
	public void setDecorator( Decorator renderer );
	public void render( Object object , PrintWriter out , HttpServletRequest request , HttpServletResponse respone , Translator translator , ContentCache cache ) throws Exception;
	public void renderElement( Object object, PrintWriter out, HttpServletRequest request, HttpServletResponse response, Translator translator, ContentCache cache ) throws Exception;
	
}
