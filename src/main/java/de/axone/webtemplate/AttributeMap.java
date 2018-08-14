package de.axone.webtemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import de.axone.exception.Ex;
import de.axone.tools.StringValueAccessor;

/**
 * Map of attributes for functions
 * Attributes are either int or String. They have to be accessed
 * in the correct way.
 * 
 * @author flo
 */

public class AttributeMap implements StringValueAccessor<String,NoSuchElementException> {
	
	private final Map<String,Object> m;
	
	public static final AttributeMap EMPTY = new AttributeMap( 
			Collections.unmodifiableMap( Collections.emptyMap() ) );
	
	public AttributeMap(){
		this( new HashMap<>() );
	}
	public AttributeMap( Map<String,Object> backingMap ){
		this.m = backingMap;
	}
	
	@Override
	public NoSuchElementException exception( String key ) {
		return Ex.up( new NoSuchElementException( key ) );
	}
	
	@Override
	public String accessChecked( String key ) {
		Object value = m.get( key );
		if( value == null ) return null;
		if( !( value instanceof String ) )
				//throw new IllegalArgumentException( "'" + key + "' is not an string but a '" + value.getClass().getSimpleName() + "'" );
				value = value.toString();
		return (String) value;
	}
	@Override
	public String access( String key ) throws NoSuchElementException {
		if( !m.containsKey( key ) ) throw exception( key );
		return accessChecked( key );
	}
	
	@Override
	public Integer getInteger( String key ) {
		Object value = m.get( key );
		if( value == null ) return null;
		if( !( value instanceof Integer ) )
				throw new IllegalArgumentException( "'" + key + "' is not an integer but a '" + value.getClass().getSimpleName() + "'" );
		return (Integer) value;
	}
	
	public void putString( String key, String value ){
		m.put( key, value );
	}
	public void putInteger( String key, String value ){
		m.put( key, Integer.valueOf( value ) );
	}
	public void putInt( String key, int value ){
		m.put( key, value );
	}
	public boolean containsKey( String key ){
		return m.containsKey( key );
	}
	
	int size(){
		return m.size();
	}
	
	@Override
	public String toString() {
		return m.toString();
	}
}
