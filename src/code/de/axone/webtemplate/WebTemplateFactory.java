package de.axone.webtemplate;

import java.io.File;
import java.io.IOException;

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
 * @see templateFor()
 * @see AbstractFileWebTemplate
 *
 * @author flo
 *
 */
public class WebTemplateFactory {

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
	 * @param The class name
	 * @return An WebTemplate instance
	 * @throws WebTemplateException if something goes wrong
	 * @throws KeyException
	 */
	public WebTemplate templateFor( String className ) throws WebTemplateException {

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
		} catch( IOException e ) {
			throw new WebTemplateException( "Error reading: " + className, e );
		}
	}

	public WebTemplate templateFor( File file ) throws WebTemplateException {

		return templateFor( file, null );
	}

	public WebTemplate templateFor( File file, String className ) throws WebTemplateException {

		try {
			WebTemplate result = instantiate( file, className );
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
	
	public int getReloadCount(){
		return DataHolderFactory.reloadCount;
	}

	private WebTemplate instantiate( File file, String className )
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, ClassCastException, IOException,
			KeyException, WebTemplateException {

		// Get Holder
		DataHolder holder;
		try {
    		holder = DataHolderFactory.holderFor( file );
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

	private WebTemplate instantiate( String className )
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, ClassCastException, IOException {

		@SuppressWarnings("unchecked")
		Class clazz = Class.forName( className );

		Object object = clazz.newInstance();

		return (WebTemplate) object;
	}
}
