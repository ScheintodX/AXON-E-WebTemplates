package de.axone.webtemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.form.Translator;


public class RendererList extends LinkedList<Renderer> implements Renderer {

	@Override
	public void render( Object object, PrintWriter out, HttpServletRequest request,
			HttpServletResponse response, Translator translator )
			throws IOException, WebTemplateException, Exception {
		
		for( Renderer renderer : this ){
			
			renderer.render( object, out, request, response, translator );
		}
	}

}
