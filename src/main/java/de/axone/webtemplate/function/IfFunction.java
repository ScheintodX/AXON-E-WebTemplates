package de.axone.webtemplate.function;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.tools.EasyParser;
import de.axone.webtemplate.AttributeMap;
import de.axone.webtemplate.DataHolder;
import de.axone.webtemplate.DataHolder.DataHolderItem;
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
	
	public static final String ATTRIBUTE_CONDITION = "condition",
	                          ATTRIBUTE_HAS = "has";
	
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
			
			// TODO: check if this is working
			String hasName = attributes.get( ATTRIBUTE_HAS );
			if( hasName != null ) {
				
				if( hasName.length() > 0 && hasName.charAt( 0 ) == '!' ){
					not=false;
					hasName = hasName.substring( 1 ).trim();
				}
				
				DataHolderItem item = holder.getItem( hasName );
				Object iVal = null;
				String iStr = null;
				if( item != null ) iVal = item.getValue();
				if( iVal != null && iVal instanceof String ) iStr = (String)iVal;
				
				if( iVal != null && iVal != DataHolder.NOVAL && ( iStr == null || iStr.length() > 0 ) ){
					holder.pushRendering( not );
				} else {
					holder.pushRendering( !not );
				}
			
			} else {
			
				String conditionName = attributes.getRequired( ATTRIBUTE_CONDITION ).trim();
				
				if( conditionName.length() > 0 && conditionName.charAt( 0 ) == '!' ){
					not=false;
					conditionName = conditionName.substring( 1 ).trim();
				}
				
				String condition = holder.getParameter( conditionName );
				
				if( EasyParser.isYes( condition ) ){
					holder.pushRendering( not );
				} else {
					holder.pushRendering( !not );
				}
			}
			
		} else if( "endif".equals( name ) ){
			
			holder.popRendering();
			
		} else if( "else".equals( name ) ){
			
			holder.toggleRendering();
		}
	}

}
