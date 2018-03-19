package de.axone.webtemplate.list;

public interface Listable<T extends Listable<T>> {
	
	public enum Position {
		TOP, MIDDLE, BOTTOM;
	}

	public T setIndexInList( int index );
	public T setPositionInList( Position position );
	public T setHighlight( boolean highlight );
	public T setListName( String name );
}
