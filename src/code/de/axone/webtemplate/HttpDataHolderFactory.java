package de.axone.webtemplate;

import java.io.IOException;
import java.net.URL;

import de.axone.data.LRUCache;
import de.axone.data.Pair;
import de.axone.logging.Log;
import de.axone.logging.Logging;
import de.axone.tools.HttpWatcher;
import de.axone.tools.HttpUtil.HttpUtilResponse;
import de.axone.webtemplate.AbstractFileWebTemplate.ParserException;

public class HttpDataHolderFactory extends AbstractDataHolderFactory {

	private static final String END_TEMPLATE = "__END_TEMPLATE__";
	private static final String BEGIN_TEMPLATE = "__BEGIN_TEMPLATE__";

	private static Log log = Logging.getLog( HttpDataHolderFactory.class );

	static LRUCache<URL, Pair<HttpWatcher, DataHolder>> storage = new LRUCache<URL, Pair<HttpWatcher,DataHolder>>( 10000 );
	static int reloadCount=0;

	synchronized public static DataHolder holderFor( URL url )
			throws KeyException, IOException, ParserException {

		
		HttpWatcher watcher;
		DataHolder result=null;
		HttpUtilResponse r;
		if( !storage.containsKey( url ) ) {

			watcher = new HttpWatcher( url );
			r=watcher.hasChanged();
			if( r != null ){
				result = instantiate( url, r );
			}
				
			storage.put( url, new Pair<HttpWatcher, DataHolder>( watcher, result ) );
		} else {
			watcher = storage.get( url ).getLeft();
			
			if( ( r=watcher.hasChanged() ) == null ) {
				result = storage.get( url ).getRight();
			} else {
				if( r != null ){
					result = instantiate( url, r );
				}
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
			KeyException, ParserException {
		
		reloadCount++;

		String encoding = response.encoding;
		if( encoding == null ) encoding = "iso-8859-1";
		
		String data = removeMarker( new String( response.content, encoding ) );
		
		DataHolder holder = instantiate( data );
		holder.putParameter( "url", url.toString() );
		
		log.trace( "DataHolder for " + url.toString() + " created" );

		return holder;

	}
	
	private static String removeMarker( String data ){
		
		int begin = data.indexOf( BEGIN_TEMPLATE );
		int end = data.indexOf( END_TEMPLATE );
		
		begin = begin > 0 ? begin + BEGIN_TEMPLATE.length() : 0;
		end = end > 0 ? end : data.length()-1;
		
		return data.substring( begin, end ).trim();
	}
	
}
