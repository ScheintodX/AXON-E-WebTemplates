package de.axone.webtemplate;

import java.util.HashMap;

import de.axone.webtemplate.function.MissingAttributeException;

/**
 * Map of attributes for functions
 * Attributes are either int or String. They have to be accessd
 * in the correct way.
 * 
 * Todo: Das hier auf ein einfacheres System (Vermutlich mit dem StringAccessor/SA)
 * umstellen.
 * Das ist aber nicht so einfach, da hier als perfomance-optimierung (brint das überhaupt
 * etwas?) Integers beim parsen in integer gewandelt werden und so nicht jedes mal geparst
 * werden müssen.
 * 
 * @author flo
 */

public class AttributeMap extends HashMap<String,Attribute>{
	
	private static final long serialVersionUID = 1L;
	
	public String getAsString( String key ){
		return getAsString( key, null );
	}
	public String getAsString( String key, String defaultValue ){
		if( containsKey( key ) ){
			return get( key ).asString();
		} else {
			return defaultValue;
		}
	}
	public String getAsStringRequired( String key ) throws MissingAttributeException { 
		return getAsStringRequired( key, null );
	}
	public String getAsStringRequired( String key, String defaultValue ) throws MissingAttributeException { 
		if( containsKey( key ) ){
			return get( key ).asString();
		} else if( defaultValue != null ){
			return defaultValue;
		} else {
			throw new MissingAttributeException( key );
		}
	}
	
	
	public Integer getAsInteger( String key ){
		return getAsInteger( key, null );
	}
	public Integer getAsInteger( String key, Integer defaultValue ){
		if( containsKey( key ) ){
			return get( key ).asInteger();
		} else {
			return defaultValue;
		}
	}
	public int getAsIntRequired( String key ) throws MissingAttributeException { 
		return getAsIntRequired( key, null );
	}
	public int getAsIntRequired( String key, Integer defaultValue ) throws MissingAttributeException { 
		if( containsKey( key ) ){
			return get( key ).asInteger();
		} else if( defaultValue != null ){
			return defaultValue;
		} else {
			throw new MissingAttributeException( key );
		}
	}
	
}
