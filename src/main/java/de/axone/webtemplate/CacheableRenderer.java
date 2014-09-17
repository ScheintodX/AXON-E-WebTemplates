package de.axone.webtemplate;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.cache.ng.CacheNG.HasCacheKey;
import de.axone.webtemplate.form.Translator;


/**
 * 
 * Only Templates which implement this interface and return <code>true</code>
 * for <code>cacheable()</code> are cached.
 * 
 * @author flo
 *
 */
public interface CacheableRenderer extends Renderer, HasCacheKey {

	/**
	 * Tell if this template is cachable in general
	 * 
	 * This is usefull for example if the template is only cacheable
	 * in certain conditions.
	 * 
	 * @return
	 */
	public boolean cacheable();
	
	/**
	 * Return a cache key which is used to store this Renderer's output in a cache
	 * 
	 * For consistency this key shall be of the format aaa[.bbb[.ccc[...]]]
	 * e.q. article.12345.picturelist
	 * 
	 * @return a cache key. For requirements {@see CacheNG.CacheKey}
	 */
	@Override
	public Object cacheKey();
	
	default public String renderToString( Object object,
			HttpServletRequest request, HttpServletResponse response,
			Translator translator, ContentCache cache  ) {
		
		StringWriter s = new StringWriter();
		try{
			render( object, new PrintWriter( s ), request, response, translator, cache );
		} catch( Exception e ){
			throw new RuntimeException( e );
		}
		return s.toString();
	}

}
