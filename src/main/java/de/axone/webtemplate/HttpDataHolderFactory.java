package de.axone.webtemplate;

import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.axone.cache.Cache;
import de.axone.data.Pair;
import de.axone.tools.HttpUtil.HttpUtilResponse;
import de.axone.tools.HttpWatcher;
import de.axone.webtemplate.AbstractFileWebTemplate.ParserException;

public class HttpDataHolderFactory extends AbstractDataHolderFactory {

	public static final Logger log =
			LoggerFactory.getLogger( HttpDataHolderFactory.class );

	final Cache.Direct<URL, Pair<HttpWatcher, DataHolder>> storage;
	static int reloadCount=0;
	
	public HttpDataHolderFactory( Cache.Direct<URL, Pair<HttpWatcher, DataHolder>> storage ){
		this.storage =  storage;
	}

	synchronized public DataHolder holderFor( URL url )
			throws IOException, ParserException, ClassNotFoundException, InstantiationException, IllegalAccessException {

		log.debug( url.toString() );
		
		HttpWatcher watcher;
		DataHolder result=null;
		HttpUtilResponse r;
		
		Pair<HttpWatcher, DataHolder> cached = storage.get( url );
		if( cached == null ) {
			
			watcher = new HttpWatcher( url );
			r=watcher.hasChanged();
			if( r != null ){
				result = instantiate( url, r );
			}
			storage.put( url, new Pair<HttpWatcher, DataHolder>( watcher, result ) );
			
		} else {
			
			watcher = cached.getLeft();
			
			r=watcher.hasChanged();
			if( r == null ) {
				result = cached.getRight();
			} else {
				result = instantiate( url, r );
				storage.put( url, new Pair<HttpWatcher, DataHolder>( watcher, result ) );
			}
		}
		
		if( result != null ){
			return result.clone();
		} else {
			return null;
		}
	}
	
	static DataHolder instantiate( URL url, HttpUtilResponse response ) throws IOException,
			ParserException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		
		reloadCount++;

		String encoding = response.encoding;
		if( encoding == null ) encoding = "iso-8859-1";
		
		String data = new String( response.content, encoding );
		
		DataHolder holder = instantiate( data, null );
		holder.setParameter( "url", url.toString() );
		
		log.trace( "DataHolder for " + url.toString() + " created" );

		return holder;

	}
}