package de.axone.webtemplate.function;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.AttributeMap;
import de.axone.webtemplate.form.Translator;

public interface Function {

	public void render( HttpServletRequest request, HttpServletResponse response, AttributeMap attributes, Object value, Translator translator ) throws Exception;
}
