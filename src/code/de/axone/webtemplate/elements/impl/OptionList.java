package de.axone.webtemplate.elements.impl;

import java.util.LinkedList;
import java.util.List;

public class OptionList extends LinkedList<Option> {

	public OptionList(){}
	public OptionList( List<? extends Option> options ){
		
		for( Option o : options ){
			
			OptionImpl option = new OptionImpl( o.getValue(), o.getText() );
			
			add( option );
		}
		
	}
	public OptionList( Enum<?> ... enums ){
		
		for( Enum<?> e : enums ){
			
			String name = e.toString();
			int ordinal = e.ordinal();
			
			OptionImpl option = new OptionImpl( ""+ordinal, name );
			add( option );
		}
	}
	public OptionList( Option ... options ){
		for( Option o : options ) {
			add( o );
		}
	}
	
}
