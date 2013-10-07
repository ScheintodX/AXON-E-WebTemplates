package de.axone.webtemplate.function;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.AttributeMap;
import de.axone.webtemplate.DataHolder;
import de.axone.webtemplate.form.Translator;

/**
 * Accesses a variable / value and preforms operations on it
 * 
 * <h4>Parameters</h4>
 * 	
 * <dl>
 * 	<dt>name
 *  <dd>The variables name
 * 	<dt>op
 *  <dd>operation. optional. space separated:
 *  	<ul>
 *  		<li>UC: to upper case
 *  		<li>LC: to lower case
 *  	</ul>
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
public class VarFunction implements Function {
	
	public static final String ATTRIBUTE_NAME = "name";
	public static final String ATTRIBUTE_OP = "op";
	
	private VarFunction(){}
	private static VarFunction instance = new VarFunction();
	public static VarFunction instance(){ return instance; }
	
	@Override
	public void render( String name, DataHolder holder, PrintWriter out, HttpServletRequest request,
			HttpServletResponse response, AttributeMap attributes, Object value,
			Translator translator ) throws Exception {
		
		String varName = attributes.getAsStringRequired( ATTRIBUTE_NAME ).trim();
		String opStr = attributes.getAsStringRequired( ATTRIBUTE_OP ).trim();
		
		String var = holder.getParameter( varName );
		
		if( opStr != null && opStr.length() > 0 ){
			
			String [] opNames = opStr.split( "\\s+" );
			
			for( String opName : opNames ){
				
				Operations op;
				try {
					op = Operations.valueOf( opName.toUpperCase() );
				} catch( IllegalArgumentException e ){
					throw new UnknownOperationException( opName );
				}
				var = op.operate( var );
			}
		}
		out.print( var );
	}
	
	private static final class UnknownOperationException extends FunctionException {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1973003599724214625L;

		UnknownOperationException( String op ){
			super( "Unknown operation: " + op );
		}
	}

	private enum Operations {
		
		UC( new Op(){
			@Override public String operate( String value ) {
				return ( value == null ) ? null : value.toUpperCase();
		} } ),
		LC( new Op(){
			@Override public String operate( String value ) {
				return ( value == null ) ? null : value.toLowerCase();
		} } )
		;
		
		private Op op;
		
		Operations( Op op ){
			this.op = op;
		}
		
		public String operate( String value ){
			return op.operate( value );
		}
		
		private interface Op {
			String operate( String value );
		}
	
	}
	

}
