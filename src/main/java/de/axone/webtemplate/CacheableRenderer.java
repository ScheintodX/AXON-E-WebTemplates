package de.axone.webtemplate;


/**
 * 
 * Only Templates which implement this interface and return <code>true</code>
 * for <code>cacheable()</code> are cached.
 * 
 * @author flo
 *
 */
public interface CacheableRenderer extends Renderer {

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
	 * TODO: Invalidation is done via invalidate( "article" ) or invalidate( "article.12345" ) usw. in the frontends/backends.
	 * 
	 * @return a cache key
	 */
	public String cacheKey();
}
