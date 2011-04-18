package de.axone.webtemplate.list;

public interface Listable {
	
	public enum Position {
		TOP, MIDDLE, BOTTOM;
	}

	public void setIndexInList( int index );
	public void setPositionInList( Position position );
	public void setHighlight( boolean highlight );
}
