package de.axone.webtemplate;

import java.io.IOException;

public abstract class AbstractStringWebTemplate extends AbstractWebTemplate {
	
	public AbstractStringWebTemplate( String string ) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, AttributeParserByHand.ParserException{
		
		super( StringDataHolderFactory.instantiate( string ) );
	}

}
