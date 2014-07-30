package de.axone.webtemplate.form;

import de.axone.exception.Assert;
import de.axone.tools.Str;

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
	public static TranslationKey param( TranslationKey base, String ... params ){
		if( params == null || params.length == 0 ) return base;
		return dynamic( base.name() + ":" + Str.join( ":", params ) );
	}
	public static TranslationKey build( String prefix, Class<?> cls, String suffix ){
		
		if( prefix == null ) prefix = "";
		if( suffix == null ) suffix = "";
		
		String name = cls.getSimpleName();
		name = name.replaceAll( "([a-z])([A-Z])", "$1_$2" );
		name = name.toUpperCase();
		
		name = prefix + name + suffix;
		
		name = name.replaceAll( "__+", "_" );
		
		return dynamic( name );
	}
	
}
