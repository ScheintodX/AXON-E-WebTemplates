package de.axone.webtemplate;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.axone.cache.Cache;
import de.axone.cache.Cache.Direct;
import de.axone.cache.CacheNoCache;
import de.axone.data.Pair;
import de.axone.tools.FileWatcher;
import de.axone.tools.HttpWatcher;
import de.axone.webtemplate.slicer.SlicerFactory;

/**
 * This class is the factory for the webtemplates.
 *
 * It supports creation of WebTemplate instances for a given class name.
 *
 * This factory maintains a cache in which every WebTemplate is stored exactly
 * once and will be reused everytime it is requested
 *
 * Depending on the implementation of the WebTemplate it my have different
 * purposes.
 *
 * @see #templateFor( String )
 * @see AbstractFileWebTemplate
 *
 * @author flo
 */
public class WebTemplateFactory {
	
	public static final Logger log =
			LoggerFactory.getLogger( WebTemplateFactory.class );
	
	private final FileDataHolderFactory fileDataHolderFactory;
	private final HttpDataHolderFactory httpDataHolderFactory;
	
	public WebTemplateFactory(){
		this( null );
	}
	
	@SuppressWarnings( "rawtypes" )
	public WebTemplateFactory( SlicerFactory slicerFactory ){
		this( new CacheNoCache(), new CacheNoCache(), null, slicerFactory );
	}
	
	
	@SuppressWarnings( "unchecked" )
	public WebTemplateFactory( 
			Cache.Direct<?,?> fileCache, 
			Cache.Direct<?,?> httpCache,
			Cache<?,?> dataCache,
			SlicerFactory slicerFactory
	){
		
		assert fileCache != null;
		assert httpCache != null;
		
		fileDataHolderFactory = new FileDataHolderFactory(
				(Direct<File, Pair<FileWatcher, DataHolder>>) fileCache, slicerFactory, null );
		
		httpDataHolderFactory = new HttpDataHolderFactory(
				(Direct<URL, Pair<HttpWatcher, DataHolder>>) httpCache );
		
	}

	/**
	 * Create a WebTemplate for a given class name.
	 *
	 * This method expect either a class name or a filename. In case of a class
	 * name the WebTemplate is instantiated directly from the given class.
	 *
	 * In case of a filename the file is loaded and a DataHolder is created. By
	 * reading the files meta-info a class name for the WebTemplate is
	 * determined and this class is instantiated with the file's data.
	 *
	 * @param className to get the template for
	 * @return WebTemplate instance
	 * @throws WebTemplateException if something goes wrong
	 * @throws KeyException
	 */
	public WebTemplate templateFor( String className, CacheProvider dataCache ) throws WebTemplateException {
		
		if( className == null ) throw new IllegalArgumentException( "'className' is null" );

		try {
			return instantiate( className );

		} catch( ClassCastException e ) {
			throw new WebTemplateException( "Wrong class: " + className, e );
		} catch( ClassNotFoundException e ) {
			throw new WebTemplateException( "Not found: " + className, e );
		} catch( InstantiationException e ) {
			throw new WebTemplateException( "Cannot instantiate: " + className, e );
		} catch( IllegalAccessException e ) {
			throw new WebTemplateException( "Cannot access: " + className, e );
		}
	}

	public WebTemplate templateFor( File file, CacheProvider dataCache ) throws WebTemplateException {
		
		return templateFor( file, null, dataCache );
	}
	
	public WebTemplate templateFor( URL url, CacheProvider dataCache ) throws WebTemplateException {

		return templateFor( url, null, dataCache );
	}

	public WebTemplate templateFor( File file, String className, CacheProvider dataCache ) throws WebTemplateException {
		
		if( file == null ) throw new IllegalArgumentException( "'file' is null" );

		try {
			WebTemplate result = instantiate( file, className, dataCache );
			return result;

		} catch( ClassCastException e ) {
			throw new WebTemplateException( "Cannot cast class: " + file, e );
		} catch( ClassNotFoundException e ) {
			throw new WebTemplateException( "Class not found in: " + file, e );
		} catch( InstantiationException e ) {
			throw new WebTemplateException( "Cannot instantiate: " + file, e );
		} catch( IllegalAccessException e ) {
			throw new WebTemplateException( "Cannot access: " + file, e );
		} catch( IOException e ) {
			throw new WebTemplateException( "Error reading: " + file, e );
		} catch( KeyException e ) {
			throw new WebTemplateException( "Error parsing: " + file, e );
		}
	}
	
	public WebTemplate templateFor( URL url, String className, CacheProvider dataCache ) throws WebTemplateException {

		if( url == null ) throw new IllegalArgumentException( "'url' is null" );
		
		try {
			WebTemplate result = instantiate( url, className );
			return result;

		} catch( ClassCastException e ) {
			throw new WebTemplateException( "Cannot cast class: " + url, e );
		} catch( ClassNotFoundException e ) {
			throw new WebTemplateException( "Class not found in: " + url, e );
		} catch( InstantiationException e ) {
			throw new WebTemplateException( "Cannot instantiate: " + url, e );
		} catch( IllegalAccessException e ) {
			throw new WebTemplateException( "Cannot access: " + url, e );
		} catch( IOException e ) {
			throw new WebTemplateException( "Error reading: " + url, e );
		} catch( KeyException e ) {
			throw new WebTemplateException( "Error parsing: " + url, e );
		}
	}
	public int getReloadCount(){
		return FileDataHolderFactory.reloadCount;
	}

	private WebTemplate instantiate( File file, String className, CacheProvider dataCache )
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, ClassCastException, IOException,
			KeyException, WebTemplateException {

		// Get Holder
		DataHolder holder;
		try {
    		holder = fileDataHolderFactory.holderFor( file, dataCache );
		} catch( WebTemplateException e ){
			throw new WebTemplateException( "In file: " + file.getPath(), e );
		}

		// First try to get classname from holder
		String classNameFromHolder = holder.getParameter( "class" );

		if( classNameFromHolder != null ){
			className = classNameFromHolder;
		}

		if( className == null ){
			throw new WebTemplateException( "No @Class spezified in template and no default given: " + file.getPath() );
		}

		AbstractFileWebTemplate template;
		try {
    		template = (AbstractFileWebTemplate) instantiate( className );
		} catch( Exception e ){
			throw new WebTemplateException( "Cannot instantiate '" + className + "' in file: " + file.getPath(), e );
		}

		template.setHolder( holder );
		
		return template;
	}

	private WebTemplate instantiate( URL url, String className )
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, ClassCastException, IOException,
			KeyException, WebTemplateException {

		// Get Holder
		DataHolder holder;
		try {
    		holder = httpDataHolderFactory.holderFor( url );
		} catch( WebTemplateException e ){
			throw new WebTemplateException( "In url: " + url, e );
		}
		
		// First try to get classname from holder
		if( holder != null ){
			
			String classNameFromHolder = holder.getParameter( "class" );
	
			if( classNameFromHolder != null ){
				className = classNameFromHolder;
			}
	
			if( className == null ){
				//throw new WebTemplateException( "No @Class specified in template and no default given: " + url );
				className = "de.emogul.TemplatePlain";
				log.warn( "Template missing in: " + url );
			}
	
			AbstractFileWebTemplate template;
			try {
	    		template = (AbstractFileWebTemplate) instantiate( className );
			} catch( Exception e ){
				throw new WebTemplateException( "Cannot instantiate '" + className + "' in file: " + url, e );
			}
	
			template.setHolder( holder );
			
			return template;
		} else {
			return null;
		}
	}

	private WebTemplate instantiate( String className )
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, ClassCastException {

		Class<?> clazz = Class.forName( className );

		Object object = clazz.newInstance();

		return (WebTemplate) object;
	}
}
