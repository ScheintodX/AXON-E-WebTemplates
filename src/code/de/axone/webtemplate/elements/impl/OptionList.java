package de.axone.webtemplate.elements.impl;

import java.util.LinkedList;

public class OptionList extends LinkedList<Option> {

	public OptionList(){}
	public OptionList( Enum<?> ... enums ){
		
		for( Enum<?> e : enums ){
			
			String name = e.toString();
			int ordinal = e.ordinal();
			
			OptionImpl option = new OptionImpl( ""+ordinal, name );
			add( option );
		}
		
	}
	
}
