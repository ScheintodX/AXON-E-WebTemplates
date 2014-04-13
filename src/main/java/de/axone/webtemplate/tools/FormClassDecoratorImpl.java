package de.axone.webtemplate.tools;

import de.axone.webtemplate.form.FormParser.FormField;

public class FormClassDecoratorImpl implements FormClassDecorator {

	private String className = "UnknownClass";
	private Class<?> dbo = Object.class;

	@Override
	public void setClassName( String className ) {
		this.className = className;
	}

	@Override
	public void setDbo( Class<?> dbo ) {
		this.dbo = dbo;
	}
	
	/*
	private String dboPath(){
		return dbo.getName();
	}
	*/
	private String dbo(){
		return dbo.getSimpleName();
	}
	private String clz(){
		return className;
	}

	private static CharSequence asConst( FormField field ) {

		return field.formName().toUpperCase();
	}
	private static CharSequence asField( FormField field ) {

		return field.formName();
	}

	@Override
	public StringBuilder formatPage( CharSequence consts, CharSequence create ){
		
		
		StringBuilder result = new StringBuilder();
		
		result
			.append( 
				"package de.axone.hvm.form;\n" + 
				"\n" +
				"import de.axone.hvm.model."+dbo()+";\n" + 
				"import de.axone.webtemplate.WebTemplateException;\n" + 
				"import de.axone.webtemplate.element.FormValueFactory;\n" + 
				"import de.axone.webtemplate.elements.impl.HtmlSelectElement.Option;\n" + 
				"import de.axone.webtemplate.elements.impl.HtmlSelectElement.OptionImpl;\n" + 
				"import de.axone.webtemplate.form.FormParser;\n" + 
				"import de.axone.webtemplate.form.WebFormImpl;\n" + 
				"import de.axone.webtemplate.form.Form.On;\n" + 
				"\n" +
				"public class "+clz()+" extends WebFormImpl {\n" + 
				"\n" )
		
			.append( consts )
			
			.append(
				"public "+clz()+"() throws WebTemplateException {\n" +
				"\n" +
				"	this( \"\" );\n" +
				"}\n" +
				"\n" +
				"public "+clz()+"( String prefix ) throws WebTemplateException {\n" +
				"\n" +
				"	FormValueFactory fvf = new FormValueFactory();\n" +
				"	fvf.setDecorator( new HVMDecorator() );\n"
			)
			
			.append( create )
		
			.append( 

				"}\n" +
				"\n" +
				"public void read( "+dbo()+" address, On on ) throws WebTemplateException{\n" +
				"\n" +
				"	FormParser<"+dbo()+"> parser = new FormParser<"+dbo()+">( address, on );\n" +
				"		parser.putInForm( this );\n" +
				"	}\n" +
				"\n" +
				"	public void fill( "+dbo()+" address, On on ) throws WebTemplateException{\n" +
				"\n" +
				"		FormParser<"+dbo()+"> parser = new FormParser<"+dbo()+">( address, on );\n" +
				"		parser.putInForm( this );\n" +
				"	}\n" +
				"\n" +
				"}\n"
			)
			
		;
		
		return result;
	}

	@Override
	public StringBuilder formatConst( FormField field ) {
		
		StringBuilder result = new StringBuilder();

		result.append( "public static final String " + asConst( field ) + " = \"" + asField( field ) + "\";\n" );

		return result;
	}

	@Override
	public StringBuilder formatCreate( FormField field ) {
		
		StringBuilder result = new StringBuilder();

		result.append( "	addFormValue( "+ asConst( field ) + ", fvf.createInputTextValue( prefix + " + asConst( field ) + ", 255, true ) );\n" );

		return result;
	}
}
