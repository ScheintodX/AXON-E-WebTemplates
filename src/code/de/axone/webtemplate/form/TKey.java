package de.axone.webtemplate.form;

import de.axone.exception.Assert;

public class TKey implements TranslationKey {

	private final String name;
	
	private TKey( String name ){
		Assert.notNull( name, "name" );
		this.name = name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals( Object obj ) {
		if( obj == null ) return false;
		if( !( obj instanceof TranslationKey ) ) return false;
		return name.equals( ((TranslationKey)obj).name() );
	}

	@Override
	public String toString() {
		return name.toString();
	}

	@Override
	public String name() {
		return name;
	}

	public static TranslationKey dynamic( String name ) {
		
		return new TKey( name );
	}
	
}
