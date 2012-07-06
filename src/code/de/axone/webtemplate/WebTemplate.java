package de.axone.webtemplate;

import java.util.Set;


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
	 * Return list of parameters' names
	 * 
	 * @return
	 */
	public Set<String> getParameterNames();

	/**
	 * Reset the WebTemplate
	 *
	 * Since WebTemplates are cached and exist only once in the application the
	 * <tt>reset</tt> method is called from the Factory whenever a WebTemplate
	 * is delivered to the application.
	 */
	public void reset();
}
