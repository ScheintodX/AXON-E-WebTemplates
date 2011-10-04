package de.axone.webtemplate;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.form.Translator;

/**
 * This class is an empty implementation of WebTemplate.
 * 
 * This can be used to return WebTemplates which doesn't render anything.
 * 
 * A singleton instance is available to vie <code>INSTANCE</code>
 * 
 * @author flo
 *
 */
public class EmptyWebTemplate implements WebTemplate {
	
	public static final EmptyWebTemplate INSTANCE = new EmptyWebTemplate();
	
	@Override
	public void render( Object object, HttpServletRequest request,
			HttpServletResponse response, Translator translator )
			throws IOException, WebTemplateException, Exception {
	}

	@Override
	public void setParameter( String name, Object object ) {
	}

	@Override
	public Object getParameter( String name ) {
		return null;
	}

	@Override
	public void reset() {}

}
