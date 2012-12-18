package de.axone.webtemplate;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.DataHolder.DataHolderItem;
import de.axone.webtemplate.DataHolder.DataHolderItemType;
import de.axone.webtemplate.form.Translator;


public class AutomatedFileWebTemplate extends AbstractFileWebTemplate {
	
	public AutomatedFileWebTemplate() {
		super();
	}
	
	public AutomatedFileWebTemplate( DataHolder holder ) {
		super( holder );
	}
	
	@Override
	public void render( Object object, PrintWriter out,
			HttpServletRequest request, HttpServletResponse response,
			Translator translator ) throws WebTemplateException, IOException, Exception {
		
		for( String key : getHolder().getKeys() ){
			
			DataHolderItem value = getHolder().getItem( key );
			
			if( value.getType() == DataHolderItemType.VAR ){
				
				String parameter = request.getParameter( value.getName() );
    			if( parameter != null ){
			
    				getHolder().setValue( key, parameter );
    			}
			}
		}
		
		//response.getWriter().write( getHolder().render().toString() );
		getHolder().render( object, out, request, response, translator );
		
	}
	
}
