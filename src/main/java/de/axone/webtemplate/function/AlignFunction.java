package de.axone.webtemplate.function;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.tools.Text;
import de.axone.tools.Text.Align;
import de.axone.webtemplate.AttributeMap;
import de.axone.webtemplate.DataHolder;
import de.axone.webtemplate.DataHolder.DataHolderItem;
import de.axone.webtemplate.Renderer.ContentCache;
import de.axone.webtemplate.form.Translator;

/**
 * Changes a variables alignment and printing
 * 
 * The variable is not printed but formated. It has to be included
 * directly somewhere else to be printed
 * 
 * <h4>Parameters</h4>
 * 	
 * <dl>
 * 	<dt>align
 *  <dd>left or right
 *  
 * 	<dt>var
 *  <dd>the variable's name as it would be used between underscores
 *  
 * 	<dt>width
 *  <dd>the width of the variable. not truncated but expanded
 * </dl>
 * 
 * <h4>Examples</h4>
 * 
 * <pre>
 * __align var="identifier" align="left" width="10"__
 * 
 * somehwere else:
 * __identifier__
 * 
 * </pre>
 * 
 * @author flo
 *
 */
public class AlignFunction implements Function {
	
	public static final String ATTRIBUTE_ALIGN = "align";
	public static final String ATTRIBUTE_VAR = "var";
	public static final String ATTRIBUTE_WIDTH = "width";
	
	private AlignFunction(){}
	private static AlignFunction instance = new AlignFunction();
	public static AlignFunction instance(){ return instance; }

	@Override
	public void render( String name , DataHolder holder , 
			PrintWriter out , HttpServletRequest request ,
			HttpServletResponse response , AttributeMap attributes , Object value , Translator translator , ContentCache cache 
	) throws Exception {
		
		String varName = attributes.getAsStringRequired( ATTRIBUTE_VAR ).trim();
		
		String typeName = attributes.getAsString( ATTRIBUTE_ALIGN, "left" ).trim().toUpperCase();
		Align type = Align.valueOf( typeName );
		
		String widthName = attributes.getAsStringRequired( ATTRIBUTE_WIDTH ).trim();
		int width = Integer.parseInt( widthName );
		
		DataHolderItem item = holder.getItem( varName );
		if( item == null ) return;
		Object v = item.getValue();
		if( v == null ) return;
		
		String s = Text.align( type, v.toString(), width, ' ' );
		
		holder.setValue( varName, s );
	}

}
