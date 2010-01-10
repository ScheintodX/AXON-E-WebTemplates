package de.axone.webtemplate;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractWebTemplate implements WebTemplate {

	protected Map<String,Object> parameters = new HashMap<String,Object>();

	@Override
	public void reset() {
		parameters.clear();
	}

	@Override
	public void setParameter( String name, Object object ) {
		parameters.put( name, object );
	}

	@Override
	public Object getParameter( String name ){
		return parameters.get( name );
	}

}
