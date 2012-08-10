package de.axone.webtemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.form.Translator;

public abstract class AbstractWebTemplate implements WebTemplate {
	
	protected Map<String,Object> parameters = new HashMap<String,Object>();

	protected DataHolder holder;

	protected AbstractWebTemplate() {}
	
	protected AbstractWebTemplate( DataHolder holder ) {

		setHolder( holder );
	}

	/**
	 * Overwrite here
	 *
	 * USAGE: Write your own manipulation on <tt>holder</tt>. Then add its
	 * output to the <tt>response</tt>
	 *
	 * @param object
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	protected abstract void doRender( Object object, PrintWriter out,
			HttpServletRequest request, HttpServletResponse response, Translator translator )
			throws IOException, WebTemplateException, Exception  ;

	@Override
	public void render( Object object, PrintWriter out, HttpServletRequest request,
			HttpServletResponse response, Translator translator ) throws WebTemplateException, IOException, Exception {
		
		doRender( object, out, request, response, translator );
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
