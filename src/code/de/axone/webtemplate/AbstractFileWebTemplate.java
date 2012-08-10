package de.axone.webtemplate;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.axone.cache.CacheNoCache;
import de.axone.data.Pair;
import de.axone.tools.FileWatcher;
import de.axone.webtemplate.DataHolder.DataHolderItem;
import de.axone.webtemplate.DataHolder.DataHolderItemType;

public abstract class AbstractFileWebTemplate extends AbstractWebTemplate {

	public static final Logger log = LoggerFactory.getLogger( AbstractFileWebTemplate.class );

	public AbstractFileWebTemplate() {}
	
	protected AbstractFileWebTemplate( File file ) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, WebTemplateException{
		
		this( new FileDataHolderFactory( 
				new CacheNoCache<File, Pair<FileWatcher, DataHolder>>(),
				null, null
		).holderFor( file, null ) );
	}
	
	public AbstractFileWebTemplate( DataHolder holder ) {
		super( holder );
	}

	@Override
	public String toString(){
		return getParameter( "path" ) + " (" + getClass().toString() + ")";
	}

	/**
	 * Fill templates from parameters
	 */
	protected void autofill() {

		for( String key : holder.getKeys() ) {

			try {
				DataHolderItem item = holder.getItem( key );

				if( item.getType() == DataHolderItemType.VAR ) {

					if( parameters.containsKey( key ) ) {

						item.setValue( parameters.get( key ) );
					}
				}

			} catch( KeyException e ) {
				e.printStackTrace(); // Never happens
			}
		}
	}

	public static class ParserException extends WebTemplateException {
		public ParserException( String message, Throwable t ){ super( message, t ); }
		public ParserException( Throwable t ){ super( t ); }
		public ParserException( String message ){ super( message ); }
	}
}
