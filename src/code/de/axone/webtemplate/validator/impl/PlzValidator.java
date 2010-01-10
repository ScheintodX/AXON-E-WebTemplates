package de.axone.webtemplate.validator.impl;



/**
 * Checks a given e-mail address for validity
 * 
 * @author flo
 */
public class PlzValidator extends PatternValidator {
	
	private static final String NO_EMAIL = "VALIDATOR_NO_PLZ";
	
	
	public PlzValidator( int length ){
		
		super( "[0-9]{" + length + "}" );
	}

	@Override
	protected String check( String value ) {
		
		if( super.check( value ) != null )
			return NO_EMAIL;
		
		return null;
	}

}
