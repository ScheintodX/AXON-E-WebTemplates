package de.axone.webtemplate.tools;

import de.axone.webtemplate.form.FormParser.FormField;

public interface FormClassDecorator {

	public void setClassName( String className );
	public void setDbo( Class dbo );
	
	public StringBuilder formatPage( CharSequence consts, CharSequence create );
	public StringBuilder formatConst( FormField name );
	public StringBuilder formatCreate( FormField creates );
	
}
