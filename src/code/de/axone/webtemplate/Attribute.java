package de.axone.webtemplate;

/**
 * Represents one Attribute.
 * 
 * The attribute can be of integer value. 
 * If it is integer value is determined lazily when needed and accessed.
 * 
 * @author flo
 */
public class Attribute {
	
	private String value;
	
	Boolean isInt;
	private int intValue;

	public Attribute( String value, boolean isInt ){
		this.value = value;
		if( isInt ){
			intValue = Integer.valueOf( value );
		}
	}
	
	public String asString(){
		return value;
	}
	public Integer asInteger(){
		if( value == null ) return null;
		
		if( isInt == null ){
			makeInt();
		}
		
		if( isInt ){
			return intValue;
		} else {
			return null;
		}
	}
	
	private void makeInt(){
		
		try {
			intValue = Integer.valueOf( value );
			isInt = true;
		} catch( NumberFormatException e ){
			isInt = false;
		}
	}
}
