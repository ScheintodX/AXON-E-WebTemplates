package de.axone.webtemplate;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.web.encoding.Encoder_Html;
import de.axone.webtemplate.Renderer.ContentCache;
import de.axone.webtemplate.element.HtmlInput;
import de.axone.webtemplate.form.TKey;
import de.axone.webtemplate.form.Translator;

public class DefaultDecorator implements Decorator {

	@Override
	public void render( HtmlInput element , Object object , PrintWriter out , 
			HttpServletRequest request , HttpServletResponse response ,
			Translator translator , ContentCache cache , boolean isValid , List<String> messages  ) throws Exception {

		if( isValid ){
			out.write( "<div class=\"valid\">" );
		} else {
			out.write( "<div class=\"invalid\">" );
		}

		element.renderElement( object, out, request, response, translator, cache );

		if( messages != null ) for( String message : messages ){

			out.write( "\n<p>" );
			out.write( Encoder_Html.ENCODE(
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
