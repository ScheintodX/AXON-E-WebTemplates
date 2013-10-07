package de.axone.webtemplate;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.web.encoding.HtmlEncoder;
import de.axone.webtemplate.element.HtmlInput;
import de.axone.webtemplate.form.TKey;
import de.axone.webtemplate.form.Translator;

public class DefaultDecorator implements Decorator {

	@Override
	public void render( HtmlInput element, Object object,
			HttpServletRequest request, HttpServletResponse response,
			Translator translator, boolean isValid, List<String> messages ) throws Exception {

		PrintWriter out = response.getWriter();

		if( isValid ){
			out.write( "<div class=\"valid\">" );
		} else {
			out.write( "<div class=\"invalid\">" );
		}

		element.renderElement( object, request, response, translator );

		if( messages != null ) for( String message : messages ){

			out.write( "\n<p>" );
			out.write( HtmlEncoder.ENCODE(
					translator.translate( TKey.dynamic( message ) ) ) );
			out.write( "</p>" );
		}

		out.write( "</div>" );
	}

	@Override
	public String getStandardClass() {
		// No default
		return null;
	}
}
