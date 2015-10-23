package de.axone.webtemplate;

import java.io.IOException;

public class StringDataHolderFactory extends AbstractDataHolderFactory {

	public static DataHolder instantiate( String string ) throws AttributeParserByHand.ParserException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		
		String name;
		if( string.length() <= 20 ) name = string;
		else name = string.substring( 0, 20 ) + '…';
		
		DataHolder result = instantiate( "From STRING: " + name, string );
		
		result.fixValues();;
		
		return result;
	}

}