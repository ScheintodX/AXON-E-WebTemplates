package de.axone.webtemplate;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.form.Translator;

public abstract class AbstractWebTemplate implements WebTemplate {
	
	@Override
	public void render( Object object, Writer out, HttpServletRequest request,
			HttpServletResponse response, Translator translator )
			throws IOException, WebTemplateException, Exception {
		
		render( object, request, response, translator );
	}

	@Override
	public boolean cachable() {
		return false;
	}

	protected Map<String,Object> parameters = new HashMap<String,Object>();

	protected DataHolder holder;

	protected AbstractWebTemplate() {}
	
	protected AbstractWebTemplate( DataHolder holder ) {

		setHolder( holder );
	}

	protected void setHolder( DataHolder holder ) {

		this.holder = holder;
	}

	public DataHolder getHolder() {
		return holder;
	}

	@Override
	public void reset() {

		parameters.clear();
		holder.clear();
	}

	@Override
	public void setParameter( String name, Object object ) {
		parameters.put( name, object );
	}

	@Override
	public Object getParameter( String name ){
		return parameters.get( name );
	}

	@Override
	public Set<String> getParameterNames(){
		return parameters.keySet();
	}
	
}
