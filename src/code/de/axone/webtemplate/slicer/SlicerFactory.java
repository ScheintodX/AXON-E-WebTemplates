package de.axone.webtemplate.slicer;

import de.axone.webtemplate.WebTemplateException;

public interface SlicerFactory {

	public Slicer instance( String master ) throws WebTemplateException;
}
