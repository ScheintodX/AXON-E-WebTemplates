package de.axone.webtemplate;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.DataHolder.DataHolderItem;
import de.axone.webtemplate.DataHolder.DataHolderItemType;
import de.axone.webtemplate.form.Translator;


public class AutomatedFileWebTemplate extends AbstractFileWebTemplate {
	
	public AutomatedFileWebTemplate() throws KeyException, IOException{
		super();
	}
	
	public AutomatedFileWebTemplate( File file ) throws KeyException, IOException, ParserException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		super( file );
	}
	
	@Override
	protected void doRender( Object object, HttpServletRequest request,
			HttpServletResponse response, Translator translator ) throws WebTemplateException, IOException, Exception {
		
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
		getHolder().render( object, request, response, translator );
	}
	
}
