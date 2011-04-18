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
 * 
 * @author flo
 */

public class AttributeMap extends HashMap<String,Object>{

	public String getAsString( String key ){
		return (String)get( key );
	}
	public Integer getAsInteger( String key ){
		return (Integer)get( key );
	}
	
	public String getAsString( String key, String defaultValue ){
		if( containsKey( key ) ){
			return (String)get( key );
		} else {
			return defaultValue;
		}
	}
	public Integer getAsInteger( String key, Integer defaultValue ){
		if( containsKey( key ) ){
			return (Integer)get( key );
		} else {
			return defaultValue;
		}
	}
	
	public String getAsStringRequired( String key ) throws MissingAttributeException { 
		return getAsStringRequired( key, null );
	}
	public int getAsIntRequired( String key ) throws MissingAttributeException { 
		return getAsIntRequired( key, null );
	}
	
	public String getAsStringRequired( String key, String defaultValue ) throws MissingAttributeException { 
		if( containsKey( key ) ){
			return (String)get( key );
		} else if( defaultValue != null ){
			return defaultValue;
		} else {
			throw new MissingAttributeException( key );
		}
	}
	
	public int getAsIntRequired( String key, Integer defaultValue ) throws MissingAttributeException { 
		if( containsKey( key ) ){
			return (Integer)get( key );
		} else if( defaultValue != null ){
			return defaultValue;
		} else {
			throw new MissingAttributeException( key );
		}
	}
}
