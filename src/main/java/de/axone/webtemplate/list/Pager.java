package de.axone.webtemplate.list;

import de.axone.webtemplate.Renderer;

public interface Pager extends Renderer {

	public void setNumPages( int num );
	public void setSelectedPage( int page );
	public void setNameBase( String nameBase );
}
