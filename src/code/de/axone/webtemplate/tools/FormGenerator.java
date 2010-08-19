package de.axone.webtemplate.tools;

import java.io.PrintStream;
import java.util.List;

import de.axone.webtemplate.form.FormParser;
import de.axone.webtemplate.form.Form.On;
import de.axone.webtemplate.form.FormParser.FormField;

public class FormGenerator {

	public static void main( String [] args ) throws Exception {
		
		if( args.length < 2 || args[ 0 ].equals( "--help" ) ){
			System.out.println( "USAGE: tool POJOCLASS ANY|CREATE|EDIT [DECORATORCLASS]" );
			System.exit( 1 );
		}
		
		PrintStream out = System.out;
		
		String className = args[ 0 ];
		
		String onStr = args[ 1 ];
		
		String decoratorClass = FormDecoratorImpl.class.getName();
		if( args.length > 2 ) decoratorClass = args[ 2 ];
		
		On on = On.valueOf( onStr );
		
		Class<?> cls = Class.forName( className );
		
		FormDecorator deco = (FormDecorator) 
				Class.forName( decoratorClass ).newInstance();
		
		List<FormField> fields = FormParser.fields( cls, on );
		
		StringBuilder result = new StringBuilder();
		for( FormField field : fields ){
			
			result.append( deco.formatInput( field ) );
			
		}
		
		out.println( deco.formatPage( result ) );
		
	}
}
