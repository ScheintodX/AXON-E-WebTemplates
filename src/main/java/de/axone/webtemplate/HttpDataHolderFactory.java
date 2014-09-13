package de.axone.webtemplate;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.axone.cache.ng.CacheNG;
import de.axone.tools.HttpDataWatcher;
import de.axone.tools.HttpUtil.HttpUtilResponse;
import de.axone.web.SuperURL;
import de.axone.webtemplate.AbstractFileWebTemplate.ParserException;

public class HttpDataHolderFactory extends AbstractDataHolderFactory {

	public static final Logger log =
			LoggerFactory.getLogger( HttpDataHolderFactory.class );

	final CacheNG.Cache<SuperURL, HttpDataWatcher<DataHolder>> storage;
	static int reloadCount=0;
	
	public HttpDataHolderFactory( CacheNG.Cache<SuperURL, HttpDataWatcher<DataHolder>> storage ){
		this.storage =  storage;
	}

	synchronized public DataHolder holderFor( SuperURL url )
			throws IOException, ParserException, ClassNotFoundException, InstantiationException, IllegalAccessException {

		log.debug( url.toDebug() );
		
		DataHolder result=null;
		HttpUtilResponse r;
		
		HttpDataWatcher<DataHolder> watcher = storage.fetch( url );
		if( watcher == null ) {
			
			watcher = new HttpDataWatcher<>( url );
			r=watcher.hasChanged();
			if( r != null ){
				result = instantiate( url, r );
			}
			watcher.setData( result );
			storage.put( url, watcher );
			
		} else {
			
			r=watcher.hasChanged();
			if( r == null ) {
				result = watcher.getData();
			} else {
				result = instantiate( url, r );
				watcher.setData( result );
				storage.put( url, watcher );
			}
		}
		
		if( result != null ){
			return result.freshCopy();
		} else {
			return null;
		}
	}
	
	static DataHolder instantiate( SuperURL url, HttpUtilResponse response ) throws IOException,
			ParserException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		
		reloadCount++;

		String encoding = response.encoding;
		if( encoding == null ) encoding = "iso-8859-1";
		
		String data = new String( response.content, encoding );
		
		DataHolder holder = instantiate( data, null );
		holder.setParameter( "url", url.toValue() );
		
		log.trace( "DataHolder for " + url.toDebug() + " created" );
		
		return holder;

	}
}
