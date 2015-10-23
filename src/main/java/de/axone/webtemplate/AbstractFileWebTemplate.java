package de.axone.webtemplate;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.axone.cache.ng.CacheNoCache;
import de.axone.cache.ng.RealmImpl;
import de.axone.tools.watcher.FileDataWatcher;

/**
 * Abstract Base Template for file based WebTemplates
 * 
 * @author flo
 */
public abstract class AbstractFileWebTemplate extends AbstractWebTemplate {
	
	public static final Logger log = LoggerFactory.getLogger( AbstractFileWebTemplate.class );

	public AbstractFileWebTemplate() {}
	
	protected AbstractFileWebTemplate( File file ) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, WebTemplateException{
		
		this( new FileDataHolderFactory( 
				new CacheNoCache<File, FileDataWatcher<DataHolder>>( new RealmImpl<>( "testnocache" ) ),
				null, null
		).holderFor( file ) );
	}
	
	public AbstractFileWebTemplate( DataHolder holder ) {
		
		super( holder );
	}

	@Override
	public String toString(){
		
		return getHolder().getParameter( DataHolder.P_FILE ) + " (" + getClass().toString() + ")";
	}
}
