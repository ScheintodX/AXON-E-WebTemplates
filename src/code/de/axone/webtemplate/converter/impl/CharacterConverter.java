package de.axone.webtemplate.converter.impl;

import de.axone.webtemplate.converter.AbstractConverter;
import de.axone.webtemplate.converter.ConverterException;

public class CharacterConverter extends AbstractConverter<Character> {
	
	@Override
	public Character convertFromString( String value )
		throws ConverterException {
		
		if( value == null )
			return null;
		
		if( value.trim().length() == 0 )
			return null;
		
		return value.charAt( 0 );
	}
	
	@Override
	public String convertToString( Character character )
		throws ConverterException {
		
		if( character == null ) return null;
		
		return character.toString();
	}

}
