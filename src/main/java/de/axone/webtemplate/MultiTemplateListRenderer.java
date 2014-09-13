package de.axone.webtemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.form.Form.On;
import de.axone.webtemplate.form.Translator;

public abstract class MultiTemplateListRenderer<D> implements Renderer {
	
	final String name;
	final String title;
	final List<D> list;
	final On on;
	
	public MultiTemplateListRenderer( String name, String title, List<D> list, On on ){
		
		this.name = name;
		this.title = title;
		this.list = list;
		this.on = on;
	}
	
	public abstract WebTemplate templateFor( D data, int index ) throws Exception, WebTemplateException;

	public abstract void setTemplateValues( WebTemplate template, D data, int index );
	
	@Override
	public void render( Object object , PrintWriter out , HttpServletRequest request ,
			HttpServletResponse response , Translator translator , ContentCache cache  )
			throws IOException, WebTemplateException, Exception {
		
		int i=0;
		for( D data : list ){
			
			WebTemplate template = templateFor( data, i );
			
			setTemplateValues( template, data, i );
			
			template.render( object, out, request, response, translator, cache );
			
			i++;
		}
		
	}
	
}