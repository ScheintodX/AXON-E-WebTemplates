package de.axone.webtemplate.tools;

import java.io.PrintStream;
import java.util.List;

import de.axone.webtemplate.form.FormParser;
import de.axone.webtemplate.form.Form.On;
import de.axone.webtemplate.form.FormParser.FormField;

public class FormClassGenerator {

	public static void main( String [] args ) throws Exception {
		
		if( args.length < 3 || args[ 0 ].equals( "--help" ) ){
			System.out.println( "USAGE: tool POJOCLASS ANY|CREATE|EDIT FormClass [DECORATORCLASS]" );
			System.exit( 1 );
		}
		
		PrintStream out = System.out;
		
		String className = args[ 0 ];
		
		String onStr = args[ 1 ];
		
		String formClass = args[ 2 ];
		
		String decoratorClass = FormClassDecoratorImpl.class.getName();
		if( args.length > 3 ) decoratorClass = args[ 3 ];
		
		On on = On.valueOf( onStr );
		
		Class<?> cls = Class.forName( className );
		
		FormClassDecorator deco = (FormClassDecorator) 
				Class.forName( decoratorClass ).newInstance();
		
		deco.setDbo( cls );
		deco.setClassName( formClass );
		
		List<FormField> fields = FormParser.fields( cls, on );
		
		StringBuilder consts = new StringBuilder();
		StringBuilder creates = new StringBuilder();
		for( FormField field : fields ){
			
			consts.append( deco.formatConst( field ) );
			creates.append( deco.formatCreate( field ) );
			
		}
		
		out.println( deco.formatPage( consts, creates ) );
		
	}
}
