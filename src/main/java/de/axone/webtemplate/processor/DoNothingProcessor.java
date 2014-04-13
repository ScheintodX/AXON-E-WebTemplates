package de.axone.webtemplate.processor;

import de.axone.webtemplate.DataHolder;

public class DoNothingProcessor implements WebTemplateProcessor {

	@Override
	public String preProcess( String data ) {
		return data;
	}

	@Override
	public DataHolder postProcess( DataHolder holder ) {
		return holder;
	}

}
