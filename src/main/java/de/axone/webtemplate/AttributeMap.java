package de.axone.webtemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.axone.tools.OA;
import de.axone.webtemplate.function.MissingAttributeException;

/**
 * Map of attributes for functions
 * Attributes are either int or String. They have to be accessed
 * in the correct way.
 * 
 * @author flo
 */

public class AttributeMap {
	
	private final Map<String,Object> m;
	
	public static final AttributeMap EMPTY = new AttributeMap( Collections.emptyMap() );
	
	AttributeMap(){
		this( new HashMap<>() );
	}
	AttributeMap( Map<String,Object> backingMap ){
		this.m = backingMap;
	}
	
	/*
	 * Integers can be accessed as string. so they need to be converted first.
	 */
	private String asString( String key ){
		Object value = m.get( key );
		if( value == null ) return null;
		return value.toString();
	}
	
	public String getAsString( String key ){
		return OA.getString( asString( key ), key );
	}
	public String getAsString( String key, String defaultValue ){
		return OA.getString( asString( key ), key, defaultValue );
	}
	public String getAsStringRequired( String key ) throws MissingAttributeException { 
		return OA.getStringRequired( asString( key ), key );
	}
	public String getAsStringRequired( String key, String defaultValue ) throws MissingAttributeException { 
		return OA.getStringRequired( asString( key ), key, defaultValue );
	}
	
	private Object asInt( String key ){
		return m.get( key );
	}
	
	public Integer getAsInteger( String key ){
		return OA.getInteger( asInt( key ), key );
	}
	public Integer getAsInteger( String key, int defaultValue ){
		return OA.getInteger( asInt( key ), key, defaultValue );
	}
	public int getAsIntRequired( String key ) throws MissingAttributeException { 
		return OA.getIntegerRequired( asInt( key ), key );
	}
	public int getAsIntRequired( String key, Integer defaultValue ) throws MissingAttributeException { 
		return OA.getIntegerRequired( asInt( key ), key, defaultValue );
	}
	
	void putString( String key, String value ){
		m.put( key, value );
	}
	void putInt( String key, String value ){
		m.put( key, Integer.valueOf( value ) );
	}
	boolean containsKey( String key ){
		return m.containsKey( key );
	}
	int size(){
		return m.size();
	}
}
