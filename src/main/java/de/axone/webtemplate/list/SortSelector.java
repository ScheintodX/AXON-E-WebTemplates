package de.axone.webtemplate.list;

import de.axone.webtemplate.Renderer;

public interface SortSelector<T> extends Renderer {
	
	public void setNameBase( String nameBase );
	
	public void setSelectedSort( Sorting<T> sort );
	public Sorting<T> getSelectedSort();
}
