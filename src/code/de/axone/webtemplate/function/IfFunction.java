package de.axone.webtemplate.function;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.tools.E;
import de.axone.tools.EasyParser;
import de.axone.webtemplate.AttributeMap;
import de.axone.webtemplate.DataHolder;
import de.axone.webtemplate.form.Translator;

public class IfFunction implements Function {
	
	public static final String ATTRIBUTE_CONDITION = "condition";

	@Override
	public Boolean render( String name,
			DataHolder holder, boolean render,
			HttpServletRequest request, HttpServletResponse response, AttributeMap attributes, Object value, Translator translator ) throws Exception {
		
		
		if( "if".equals( name ) ){
			
			String conditionName = attributes.getAsStringRequired( ATTRIBUTE_CONDITION );
			E.rr( conditionName );
			String condition = holder.getParameter( conditionName );
			E.rr( condition );
			
			if( EasyParser.isYes( condition ) ){
				return true;
			} else {
				return false;
			}
		} else if( "endif".equals( name ) ){
			return true;
		} else if( "else".equals( name ) ){
			return ! render;
		}
		return null;
	}

}
