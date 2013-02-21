package de.axone.webtemplate;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.axone.cache.CacheNoCache;
import de.axone.data.Pair;
import de.axone.tools.FileWatcher;

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
				new CacheNoCache<File, Pair<FileWatcher, DataHolder>>(),
				null, null
		).holderFor( file, null ) );
	}
	
	public AbstractFileWebTemplate( DataHolder holder ) {
		
		super( holder );
	}

	@Override
	public String toString(){
		
		return getHolder().getParameter( DataHolder.PARAM_FILE ) + " (" + getClass().toString() + ")";
	}

	public static class ParserException extends WebTemplateException {
		
		public ParserException( String message, Throwable t ){ super( message, t ); }
		public ParserException( Throwable t ){ super( t ); }
		public ParserException( String message ){ super( message ); }
	}
}
