package de.axone.webtemplate.slicer;

import java.io.File;

import de.axone.webtemplate.WebTemplateException;

public class ConfiguredSlicerFactory implements SlicerFactory {
	
	private final String className;
	private final File masterBase;
	private final File outputBase;
	
	public ConfiguredSlicerFactory( String className, File masterBase, File outputBase ){
		
		this.className = className;
		this.masterBase = masterBase;
		this.outputBase = outputBase;
	}
	
	@Override
	public Slicer instance( String master ) throws WebTemplateException {
		
		Slicer result;
		try {
			result = (Slicer) Class.forName( className ).newInstance();
			result.setMasterBase( masterBase );
			result.setTemplateBase( outputBase );
			
			return result;
			
		} catch( InstantiationException | IllegalAccessException
				| ClassNotFoundException e ) {
			
			throw new WebTemplateException( "Error creating Slicer", e );
		}
		
	}
}
