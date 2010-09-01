package de.axone.webtemplate.function;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.tools.EasyParser;
import de.axone.webtemplate.AttributeMap;
import de.axone.webtemplate.DataHolder;
import de.axone.webtemplate.form.Translator;

/**
 * Creates a condition for the rendering of following code
 * 
 * <h4>Parameters</h4>
 * 	
 * <dl>
 * 	<dt>condition
 *  <dd>The condition to check agains. Conditions are set programmatically but they are implemented as Parameters of the holder
 *  so they can be set via @Parameter in the template, too.
 * </dl>
 * 
 * <h4>Examples</h4>
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
public class FunctionIf implements Function {
	
	public static final String ATTRIBUTE_CONDITION = "condition";

	@Override
	public void render( String name,
			DataHolder holder, 
			HttpServletRequest request, HttpServletResponse response, AttributeMap attributes, Object value, Translator translator ) throws Exception {
		
		if( "if".equals( name ) ){
			
			String conditionName = attributes.getAsStringRequired( ATTRIBUTE_CONDITION );
			String condition = holder.getParameter( conditionName );
			
			if( EasyParser.isYes( condition ) ){
				holder.setRender( true );
			} else {
				holder.setRender( false );
			}
		} else if( "endif".equals( name ) ){
			holder.setRender( true );
		} else if( "else".equals( name ) ){
			holder.setRender( ! holder.isRender() );
		}
	}

}
