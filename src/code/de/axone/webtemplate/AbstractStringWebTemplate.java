package de.axone.webtemplate;

import java.io.IOException;

import de.axone.webtemplate.AbstractFileWebTemplate.ParserException;

public abstract class AbstractStringWebTemplate extends AbstractWebTemplate {
	
	public AbstractStringWebTemplate( String string ) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, ParserException{
		
		super( StringDataHolderFactory.instantiate( string, null ) );
	}

}
