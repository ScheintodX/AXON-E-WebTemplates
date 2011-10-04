package de.axone.webtemplate;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractWebTemplate implements WebTemplate {
	
	protected Map<String,Object> parameters = new HashMap<String,Object>();

	protected DataHolder holder;

	protected AbstractWebTemplate() {}
	
	protected AbstractWebTemplate( DataHolder holder ) {

		setHolder( holder );
	}

	protected void setHolder( DataHolder holder ) {

		this.holder = holder;
	}

	public DataHolder getHolder() {
		return holder;
	}

	@Override
	public void reset() {

		parameters.clear();
		holder.clear();
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
