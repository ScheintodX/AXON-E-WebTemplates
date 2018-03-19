package de.axone.webtemplate.form;


@FunctionalInterface
public interface TranslationKey extends Translatable {
	
	public String name();
	
	@Override
	public default String translated( Translator t ) {
		
		return t.translate( this );
	}
}
