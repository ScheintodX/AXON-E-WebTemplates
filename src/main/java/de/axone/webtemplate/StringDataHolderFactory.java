package de.axone.webtemplate;

import java.io.IOException;

import de.axone.webtemplate.AbstractFileWebTemplate.ParserException;

public class StringDataHolderFactory extends AbstractDataHolderFactory {

	public static DataHolder instantiate( String string ) throws ParserException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		
		DataHolder result = instantiate( "From STRING: " + string.substring( 20 ) + "...", string );
		
		result.fixValues();;
		
		return result;
	}

}
