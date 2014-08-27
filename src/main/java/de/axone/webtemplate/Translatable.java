package de.axone.webtemplate;

import de.axone.webtemplate.form.Translator;

public interface Translatable {

	public String translated( Translator t );
	public String plain();
}
