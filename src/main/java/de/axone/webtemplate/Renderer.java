package de.axone.webtemplate;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.cache.ng.CacheNG;
import de.axone.exception.Ex;
import de.axone.webtemplate.converter.ConverterException;
import de.axone.webtemplate.form.Translator;

/**
 * A renderer renders content
 * 
 * This is the base class of rendering hirarchy where the famous {@link WebTemplate} comes from.
 * 
 * Many methods accept Renderer as paramter in order to pass rendereing to other objects.
 * 
 * @author flo
 */
public interface Renderer {
	
	public static class NoType extends Object {};

	/**
	 * Render this WebTemplates content to the given HttpServletResponse
	 *
	 * @param object Any object. Depends on the implementation what is done with it.
	 *        May be passed through or replaced by another one.
	 * @param out A PrintWriter which should be used for output.
	 *        In case of caching this is what gets cached.
	 *        Normally this is the one gotten by <code>response.getWriter()</code>
	 *        Passed through
	 * @param request The HttpServletRequest. Particularilly usefull for accessing the GET/POST parameters
	 *        Passed through
	 * @param response The HttpServletResponse. Implementation my use it's writer to render the content.
	 *        Passed through
	 * @param translator The Translator used for translation.
	 *        Passed through
	 * @param cache for caching content of CachableRenderer or null for no content caching
	 *        Passed through
	 * @throws Exception is thrown when some generic shop exception occures
	 */
	public void render( Object object, PrintWriter out,
			HttpServletRequest request, HttpServletResponse response,
			Translator translator, ContentCache cache ) throws Exception;

	
	/**
	 * Interface used as shortcut and for simpler refactoring
	 * 
	 * @author flo
	 */
	public interface ContentCache extends CacheNG.AutomaticClient<Object,String>{}
	
	/**
	 * Cast template to that class
	 * 
	 * This is a replacement for casting by parenteses which
	 * does additional error reporting if it fails
	 * 
	 * @param clazz
	 * @return the ca
	 */
	public default <T extends WebTemplate> T expectIsInstanceOf( Class<T> clazz ){
		try {
			return clazz.cast( this );
		} catch( ClassCastException e ){
			ConverterException w = Ex.up( new ConverterException( "Tried to convert to '" + clazz.getSimpleName() + "' but is '" + this.getClass().getSimpleName() + "'", e ) );
			throw w;
		}
	}
	
}
