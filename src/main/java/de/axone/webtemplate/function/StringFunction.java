package de.axone.webtemplate.function;

import java.io.PrintWriter;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.AttributeMap;
import de.axone.webtemplate.DataHolder;
import de.axone.webtemplate.Renderer.ContentCache;
import de.axone.webtemplate.form.Translator;

public class StringFunction implements Function {
	
	private final Supplier<String> supplier;
	
	public StringFunction( Supplier<String> supplier ) {
		
		this.supplier = supplier;
	}

	@Override
	public void render( String name, DataHolder holder, PrintWriter out,
			HttpServletRequest request, HttpServletResponse response,
			AttributeMap attributes, Object value, Translator translator,
			ContentCache cache ) throws Exception {
		
		out.print( supplier.get() );
	}

}
