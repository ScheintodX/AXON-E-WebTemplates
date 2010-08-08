package de.axone.webtemplate;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.form.Translator;

public interface Renderer {

	/**
	 * Render this WebTemplates content to the given HttpServletResponse
	 *
	 * @param object Any object. Depends on the implementation what is done with it.
	 * @param request The HttpServletRequest. Particularilly usefull for accessing the GET/POST parameters
	 * @param response The HttpServletResponse. Implementation my use it's writer to render the content.
	 * @throws IOException is thrown when some error occured accessing the writer
	 * @throws Exception is thrown when some generic shop exception occures
	 */
	public void render( Object object, HttpServletRequest request,
			HttpServletResponse response, Translator translator ) throws IOException, WebTemplateException, Exception;

}
