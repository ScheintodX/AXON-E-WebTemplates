package de.axone.webtemplate;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.form.Translator;

public interface CachableRenderer {

	/**
	 * Render this WebTemplates content to the given HttpServletResponse
	 *
	 * @param object Any object. Depends on the implementation what is done with it.
	 * @param out A Writer where the output is to be written to. In order for 'normal' Renderer functionality pass the request's PrintWriter here.
	 * @param request The HttpServletRequest. Particularilly usefull for accessing the GET/POST parameters
	 * @param response The HttpServletResponse. Implementation my use it's writer to render the content.
	 * @throws IOException is thrown when some error occured accessing the writer
	 * @throws Exception is thrown when some generic shop exception occures
	 */
	public void render( Object object, Writer out, HttpServletRequest request,
			HttpServletResponse response, Translator translator ) throws IOException, WebTemplateException, Exception;
	
	/**
	 * Tell if this template is cachable in general
	 * 
	 * @return
	 */
	public boolean cachable();
	
	/**
	 * Return a cache key which is used to store this Renderer's output in a cache
	 * 
	 * For consistency this key shall be of the format aaa[.bbb[.ccc[...]]]
	 * e.q. article.12345.picturelist
	 * 
	 * Invalidation is done via invalidate( "article" ) or invalidate( "article.12345" ) usw.
	 * in the frontends/backends
	 * 
	 * @return a cache key
	 */
	public String cacheKey();
}
