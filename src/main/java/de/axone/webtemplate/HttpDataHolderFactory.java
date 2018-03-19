package de.axone.webtemplate;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.axone.cache.ng.CacheNG;
import de.axone.tools.HttpUtil.HttpUtilResponse;
import de.axone.tools.watcher.HttpDataWatcher;
import de.axone.web.SuperURL;

public class HttpDataHolderFactory extends AbstractDataHolderFactory {

	public static final Logger log =
			LoggerFactory.getLogger( HttpDataHolderFactory.class );

	final CacheNG.Cache<SuperURL, HttpDataWatcher<DataHolder>> storage;
	static int reloadCount=0;
	
	public HttpDataHolderFactory( CacheNG.Cache<SuperURL, HttpDataWatcher<DataHolder>> storage ){
		this.storage =  storage;
	}

	synchronized public DataHolder holderFor( SuperURL url )
			throws IOException, AttributeParserByHand.ParserException, ClassNotFoundException, InstantiationException, IllegalAccessException {

		log.debug( "holderFor", url.toDebug() );
		
		DataHolder holder=null;
		HttpUtilResponse resp;
		
		HttpDataWatcher<DataHolder> watcher = storage.fetch( url );
		if( watcher == null ) {
			
			watcher = new HttpDataWatcher<>( url );
			resp=watcher.hasChanged();
			if( resp != null ){
				holder = instantiate( url, resp );
			}
			watcher.setData( holder );
			storage.put( url, watcher );
			
		} else {
			
			resp=watcher.hasChanged();
			if( resp == null ) {
				holder = watcher.getData();
			} else {
				holder = instantiate( url, resp );
				watcher.setData( holder );
				storage.put( url, watcher );
			}
		}
		
		if( holder != null ){
			return holder.freshCopy();
		} else {
			return null;
		}
	}
	
	static DataHolder instantiate( SuperURL url, HttpUtilResponse response ) throws IOException,
			AttributeParserByHand.ParserException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		
		reloadCount++;

		String encoding = response.encoding;
		if( encoding == null ) encoding = "iso-8859-1";
		
		String data = new String( response.content, encoding );
		
		DataHolder holder = instantiate( url.toText(), data );
		holder.setSystemParameter( "url", url.toValue() );
		
		log.trace( "DataHolder for " + url.toDebug() + " created" );
		
		holder.fixValues();
		
		return holder;

	}
}
