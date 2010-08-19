package de.axone.webtemplate.tools;

import de.axone.webtemplate.form.FormParser.FormField;

public interface FormDecorator {

	public StringBuilder formatInput( FormField name );
	public StringBuilder formatPage( CharSequence content );
	
}
