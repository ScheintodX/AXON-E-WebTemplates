package de.axone.webtemplate;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.axone.cache.ng.CacheNG;
import de.axone.cache.ng.CacheNoCache;
import de.axone.cache.ng.RealmImpl;
import de.axone.exception.Assert;
import de.axone.refactor.Refactor;
import de.axone.tools.watcher.FileDataWatcher;
import de.axone.tools.watcher.HttpDataWatcher;
import de.axone.web.SuperURL;
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
 * @see AbstractWebTemplate
 *
 * @author flo
 */
public class WebTemplateFactory {

	private static final String P_CLASS = "class";

	public static final Logger log =
			LoggerFactory.getLogger( WebTemplateFactory.class );

	private final FileDataHolderFactory fileDataHolderFactory;
	private final HttpDataHolderFactory httpDataHolderFactory;

	public WebTemplateFactory(){
		this( null );
	}

	public WebTemplateFactory( SlicerFactory slicerFactory ){

		this( new CacheNoCache<File,FileDataWatcher<DataHolder>>( new RealmImpl<>( "WTF:FileCache" ) ),
				new CacheNoCache<SuperURL,HttpDataWatcher<DataHolder>>( new RealmImpl<>( "WTF:HttpCache" ) ), slicerFactory );

	}

	public WebTemplateFactory(
			CacheNG.Cache<File,FileDataWatcher<DataHolder>> fileCache,
			CacheNG.Cache<SuperURL,HttpDataWatcher<DataHolder>> httpCache,
			SlicerFactory slicerFactory ){

		assert fileCache != null;
		assert httpCache != null;

		fileDataHolderFactory =
				new FileDataHolderFactory( fileCache, slicerFactory, null );

		httpDataHolderFactory =
				new HttpDataHolderFactory( httpCache );

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
	public WebTemplate templateFor( String className ) throws WebTemplateException {

		Assert.notNull( className, "className" );

		try {
			return instantiateClassByName( className );

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

	public WebTemplate templateFor( File file ) throws WebTemplateException {

		return templateFor( file, null );
	}

	public WebTemplate templateFor( SuperURL url ) throws WebTemplateException {

		return templateFor( url, null );
	}

	public WebTemplate templateFor( File file, String className ) throws WebTemplateException {

		Assert.notNull( file, "file" );

		try {
			WebTemplate result = instantiateFile( file, className );
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

	public WebTemplate templateFor( SuperURL url, String className ) throws WebTemplateException {

		Assert.notNull( url, "url" );

		try {
			WebTemplate result = instantiateURL( url, className );
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

	private WebTemplate instantiateFile( File file, String className )
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, ClassCastException, IOException,
			KeyException, WebTemplateException {

		// Get Holder
		DataHolder holder;
		try {
    		holder = fileDataHolderFactory.holderFor( file );
		} catch( WebTemplateException e ){
			throw new WebTemplateException( "In file: " + file.getPath(), e );
		}

		// First try to get classname from holder
		String classNameFromHolder = holder.getParameter( P_CLASS );

		if( classNameFromHolder != null ){
			className = classNameFromHolder;
		}

		if( className == null ){
			throw new WebTemplateException( "No @Class spezified in template and no default given: " + file.getPath() );
		}

		AbstractWebTemplate template;
		try {
    		template = (AbstractWebTemplate) instantiateClassByName( className );
		} catch( Exception e ){
			throw new WebTemplateException( "Cannot instantiate '" + className + "' in file: " + file.getPath(), e );
		}

		template.setHolder( holder );

		return template;
	}

	private WebTemplate instantiateURL( SuperURL url, String className )
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

			String classNameFromHolder = holder.getParameter( P_CLASS );

			if( classNameFromHolder != null ){
				className = classNameFromHolder;
			}

			if( className == null ){
				className = "de.emogul.TemplatePlain";
				log.warn( "@Class missing in: " + url );
			}

			AbstractWebTemplate template;
			try {
	    		template = (AbstractWebTemplate) instantiateClassByName( className );
			} catch( Exception e ){
				throw new WebTemplateException( "Cannot instantiate '" + className + "' in file: " + url, e );
			}

			template.setHolder( holder );

			return template;
		} else {
			return null;
		}
	}


	/*
	private String lastClazzName;
	private Class<?> lastClazz;
	private Lock lock = new ReentrantLock();
	*/

	@Refactor( action="Make faster", reason="Single method which takes the most time" )
	private WebTemplate instantiateClassByName( String className )
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, ClassCastException {

		Class<?> clazz;

		// IdentityMap wäre auch noch eine Möglichkeit...
		// pretty simple cache but has about 60% hit rate and is fast
		/*
		lock.lock();
		try {

			if( lastClazzName == className ){

				clazz = lastClazz;

			} else {

				clazz = Class.forName( className );
				lastClazz = clazz;
				lastClazzName = className;
			}
		} finally {
			lock.unlock();
		}
		*/
		clazz = Class.forName( className );

		Object object = clazz.newInstance();

		return (WebTemplate) object;
	}
}
