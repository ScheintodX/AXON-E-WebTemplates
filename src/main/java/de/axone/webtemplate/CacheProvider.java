package de.axone.webtemplate;

import de.axone.cache.Cache;

public interface CacheProvider {

	Cache<String,String> getCache() ;
}
