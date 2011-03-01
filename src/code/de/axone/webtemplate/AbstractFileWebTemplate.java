package de.axone.webtemplate;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.axone.cache.CacheNoCache;
import de.axone.data.Pair;
import de.axone.tools.FileWatcher;
import de.axone.webtemplate.DataHolder.DataHolderItem;
import de.axone.webtemplate.DataHolder.DataHolderItemType;
import de.axone.webtemplate.form.Translator;

public abstract class AbstractFileWebTemplate extends AbstractWebTemplate {

	public static final Logger log = LoggerFactory.getLogger( AbstractFileWebTemplate.class );

	private DataHolder holder;

	public AbstractFileWebTemplate() {}
	
	protected AbstractFileWebTemplate( File file ) throws KeyException, IOException, ParserException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		
		this( new FileDataHolderFactory( 
				new CacheNoCache<File, Pair<FileWatcher, DataHolder>>()
		).holderFor( file ) );
	}

	protected AbstractFileWebTemplate( DataHolder holder ) throws KeyException, IOException, ParserException, ClassNotFoundException, InstantiationException, IllegalAccessException{

		setHolder( holder );
	}

	public void setHolder( DataHolder holder ) throws KeyException, IOException{

		this.holder = holder;
	}

	public DataHolder getHolder() {
		return holder;
	}

	@Override
	public void reset() {

		super.reset();

		holder.clear();
	}

	/**
	 * Overwrite here
	 *
	 * USAGE: Write your own manipulation on <tt>holder</tt>. Then add its
	 * output to the <tt>response</tt>
	 *
	 * @param object
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	protected abstract void doRender( Object object,
			HttpServletRequest request, HttpServletResponse response, Translator translator )
			throws IOException, WebTemplateException, Exception  ;

	@Override
	public void render( Object object, HttpServletRequest request,
			HttpServletResponse response, Translator translator ) throws WebTemplateException, IOException, Exception {

		doRender( object, request, response, translator );
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
