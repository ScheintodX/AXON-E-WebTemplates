package de.axone.webtemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.axone.tools.E;

public abstract class AbstractWebTemplate implements WebTemplate {
	
	private WebTemplateInfo info;
	
	protected DataHolder holder;
	
	protected Map<String,Object> parameters = new HashMap<String,Object>();

	protected AbstractWebTemplate() {}
	
	protected AbstractWebTemplate( DataHolder holder ) {

		setHolder( holder );
	}
	
	/* Holder */

	protected void setHolder( DataHolder holder ) {

		this.holder = holder;
	}

	public DataHolder getHolder() {
		
		return holder;
	}

	/* Parameters */
	
	@Override
	public void setValue( String name, Object object ){
		
		holder.setValue( name, object );
	}

	@Override
	public void setParameter( String name, Object object ) {
		
		parameters.put( name, object );
	}

	@Override
	public Object getParameter( String name ){
		
		return parameters.get( name );
	}

	@Override
	public Set<String> getParameterNames(){
		
		return parameters.keySet();
	}
	
	
	@Override
	public WebTemplateInfo getInfo() {
		return info;
	}
	
	@Override
	public WebTemplate print() {
		E.x();
		E.rr( info );
		return this;
	}
	
	public void setInfo( WebTemplateInfo info ){
		this.info = info;
	}

	public void autofill() {
		getHolder().autofill( parameters );
	}
}
