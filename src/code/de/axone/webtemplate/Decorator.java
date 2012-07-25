package de.axone.webtemplate;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.element.HtmlInput;
import de.axone.webtemplate.form.Translator;

public interface Decorator {

	public void render( HtmlInput element, Object object, PrintWriter out, HttpServletRequest request,
			HttpServletResponse response, Translator translator,
			boolean isValid, List<String> messages ) throws Exception;

	public String getStandardClass();
}
