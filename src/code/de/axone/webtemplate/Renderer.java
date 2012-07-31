package de.axone.webtemplate;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.form.Translator;

public interface Renderer {

	/**
	 * Render this WebTemplates content to the given HttpServletResponse
	 *
	 * @param object Any object. Depends on the implementation what is done with it.
	 * @param out A PrintWriter which should be used for output. In case of caching this is what gets cached.
	 * @param request The HttpServletRequest. Particularilly usefull for accessing the GET/POST parameters
	 * @param response The HttpServletResponse. Implementation my use it's writer to render the content.
	 * @param out A Writer. Use this to output to. Normally this is the on gotten by <code>response.getWriter()</code>
	 * @throws IOException is thrown when some error occured accessing the writer
	 * @throws Exception is thrown when some generic shop exception occures
	 */
	public void render( Object object, PrintWriter out,
			HttpServletRequest request, HttpServletResponse response, Translator translator ) throws IOException, WebTemplateException, Exception;


}
