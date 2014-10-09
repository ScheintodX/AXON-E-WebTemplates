package de.axone.webtemplate.function;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.tools.EasyParser;
import de.axone.webtemplate.AttributeMap;
import de.axone.webtemplate.DataHolder;
import de.axone.webtemplate.Renderer.ContentCache;
import de.axone.webtemplate.form.Translator;

/**
 * Creates a condition for the rendering of following code
 * 
 * <em>Parameters</em>
 * 	
 * <dl>
 * 	<dt>condition
 *  <dd>The condition to check agains. Conditions are set programmatically but they are implemented as Parameters of the holder
 *  so they can be set via @Parameter in the template, too.
 * </dl>
 * 
 * <em>Examples</em>
 * 
 * <pre>
 * __if condition="showList"__
 *   __article_list id="123"__
 * __endif__
 * </pre>
 * 
 * @author flo
 *
 */
public class IfFunction implements Function {
	
	public static final String ATTRIBUTE_CONDITION = "condition";
	
	private IfFunction(){}
	private static IfFunction instance = new IfFunction();
	public static IfFunction instance(){ return instance; }

	@Override
	public void render( String name , DataHolder holder , 
			PrintWriter out , HttpServletRequest request ,
			HttpServletResponse response , AttributeMap attributes , Object value , Translator translator , ContentCache cache 
	) throws Exception {
		
		if( "if".equals( name ) ){
			
			boolean not=true;
			String conditionName = attributes.getRequired( ATTRIBUTE_CONDITION ).trim();
			if( conditionName.length() > 0 && conditionName.charAt( 0 ) == '!' ){
				not=false;
				conditionName = conditionName.substring( 1 ).trim();
			}
			String condition = holder.getParameter( conditionName );
			
			if( EasyParser.isYes( condition ) ){
				holder.setRendering( not );
			} else {
				holder.setRendering( !not );
			}
		} else if( "endif".equals( name ) ){
			holder.setRendering( true );
		} else if( "else".equals( name ) ){
			holder.setRendering( ! holder.isRendering() );
		}
	}

}
