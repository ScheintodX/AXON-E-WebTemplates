package de.axone.webtemplate.processor;

import de.axone.webtemplate.DataHolder;

public interface WebTemplateProcessor {
	
	/**
	 * This method is called before processing the template's data
	 * 
	 * @param data
	 * @return
	 */
	public String preProcess( String data );
	
	/**
	 * This method is called after all Processing is done
	 * 
	 * @param holder
	 * @return
	 */
	public DataHolder postProcess( DataHolder holder );
}
