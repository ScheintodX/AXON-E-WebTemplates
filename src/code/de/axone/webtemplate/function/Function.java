package de.axone.webtemplate.function;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.AttributeMap;
import de.axone.webtemplate.DataHolder;
import de.axone.webtemplate.form.Translator;

/**
 * Base for all WebTempatle-Functions
 * 
 * TODO: Common base for all types of functions. Including this and ajax and so on. See uplink fds functions.
 * 
 * @author flo
 */
public interface Function {

	public void render( String name, DataHolder holder, HttpServletRequest request, HttpServletResponse response, AttributeMap attributes, Object value, Translator translator ) throws Exception;
}
