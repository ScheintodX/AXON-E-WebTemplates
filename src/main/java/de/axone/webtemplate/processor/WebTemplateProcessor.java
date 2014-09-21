package de.axone.webtemplate.processor;

import de.axone.webtemplate.DataHolder;

public interface WebTemplateProcessor {
	
	/**
	 * This method is called before processing the template's data
	 * 
	 * @param data
	 * @return the processed data. or the original if no processing is needed
	 */
	public String preProcess( String data );
	
	/**
	 * This method is called after all Processing is done
	 * 
	 * @param holder
	 * @return the processed DataHolder or the original one if no processing is needed
	 */
	public DataHolder postProcess( DataHolder holder );
}
