package de.axone.webtemplate;

import java.util.Set;

import de.axone.exception.Ex;
import de.axone.webtemplate.converter.ConverterException;


/**
 * Base class for generic WebTemplates.
 *
 * <h2>Usage:</h2>
 * <pre>
 * 		HttpServletRequest request;
 * 		HttpSErvletRresponse response;
 *
 * 		MyObject myObject;  // some object
 *
 * 		String templatePath = "template/myTemplate.xhtml";  // Put in WebContent
 * 		File templateFile = new File( templatePath );		// Or some other mechnism from the SevletContainer
 * 															// see: ServletContext.getRealPath( String path )
 *
 * 		WebTemplateFactory factory = new WebTemplateFactory();	// Or store globally
 *
 * 		WebTemplate template = factory.templateFor( templateFile );
 *
 * 		template.setParameter( "key", "value" )
 *
 * 		template.render( myObject, request, response );
 *
 * </pre>
 *
 * <h2>myclass.java</h2>
 * <pre>
 * 		public class myclass extends AbstractFileWebTempalte {
 * 			public myclass() {
 * 				super();
 *			}
 *
 *			public void doRender( Object object, HttpServletRequest request, HttpServletResponse response ) throws Exception {
 *
 *				autofill(); // fills holders values with parameters
 *
 *				getHolder().setValue( "otherkey", "othervalue" );
 *
 *				getHolder().render( object, request, response );
 *			}
 *		}
 *
 * </pre>
 *
 * <h2>myTempate.xhtml:</h2>
 * <pre>
 * \@Class: mypackage.myclass
 *
 * TemplateText __variable1__ __variable2__
 * </pre>
 * (\@ is only without \)
 *
 * @author flo
 *
 */
public interface WebTemplate extends Renderer {

	/**
	 * Set value in holder directly
	 *
	 * @param name
	 * @param object
	 */
	public void setValue( String name, Object object );

	/**
	 * Set a named parameter
	 *
	 * This parameters are for the implementation's use.
	 *
	 * @param name The parameters name
	 * @param object The value
	 */
	public void setParameter( String name, Object object );

	/**
	 * Get a named parameter
	 *
	 * @param name
	 * @return the value
	 */
	public Object getParameter( String name );

	/**
	 * @return list of parameters' names
	 */
	public Set<String> getParameterNames();

	/**
	 * @return an info for this template
	 */
	public WebTemplateInfo getInfo();

	/**
	 * Print info
	 * @return this
	 */
	public WebTemplate print();

	/**
	 * Cast template to that class
	 *
	 * This is a replacement for casting by parenteses which
	 * does additional error reporting if it fails
	 *
	 * @param clazz
	 * @return the ca
	 */
	@Override
	public default <T extends WebTemplate> T expectIsInstanceOf( Class<T> clazz ){
		try {
			return clazz.cast( this );
		} catch( ClassCastException e ){
			Object query = this.getParameter( "query" );
			ConverterException w = Ex.up( new ConverterException( "Tried to convert to '" + clazz.getSimpleName() + "' but is '" + this.getClass().getSimpleName() + "' for " + query, e ) );
			w.setInfo( getInfo() );
			throw w;
		}
	}

	public interface WebTemplateInfo {

		public String getPath();
	}
}
