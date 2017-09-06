package de.axone.webtemplate;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.form.Translator;

public class WebTemplateList implements WebTemplate {
	
	List<WebTemplate> templates = new LinkedList<>();
	
	public void add( WebTemplate template ) {
		templates.add( template );
	}

	@Override
	public void render( Object object, PrintWriter out,
			HttpServletRequest request, HttpServletResponse response,
			Translator translator, ContentCache cache ) throws Exception {
		
		for( WebTemplate template : templates ) {
			
			template.render( object, out, request, response, translator, cache );
		}
	}

	@Override
	public void setValue( String name, Object object ) {
		
		for( WebTemplate template : templates ) {
			template.setValue( name, object );
		}
		
	}

	@Override
	public void setParameter( String name, Object object ) {
		
		for( WebTemplate template : templates ) {
			template.setParameter( name, object );
		}
	}

	@Override
	public Object getParameter( String name ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getParameterNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public WebTemplateInfo getInfo() {
		throw new UnsupportedOperationException();
	}

	@Override
	public WebTemplate print() {
		throw new UnsupportedOperationException();
	}

}
