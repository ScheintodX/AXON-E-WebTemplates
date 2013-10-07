package de.axone.webtemplate.list;

import de.axone.webtemplate.Renderer;

public interface SortSelector extends Renderer {
	
	public void setNameBase( String nameBase );
	public void setSelectedSort( String sort );
	public String getSelectedSort();
}
