package de.axone.webtemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.axone.tools.E;
import de.axone.tools.Stack;

public abstract class AbstractWebTemplate implements WebTemplate {
	
	protected final Throwable callstack;
	
	private WebTemplateInfo info;
	
	protected DataHolder holder;
	
	protected Map<String,Object> parameters = new HashMap<String,Object>();

	protected AbstractWebTemplate() {
		callstack = new Throwable();
	}
	
	protected AbstractWebTemplate( DataHolder holder ) {
		
		this();

		setHolder( holder );
	}
	
	/* Holder */

	protected void setHolder( DataHolder holder ) {

		this.holder = holder;
		
		holder.setValue( DataHolder.P_CALLSTACK, () -> Stack.quick( callstack ) );
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
	
	public StackTraceElement [] getStackTrace() {
		
		return this.callstack.getStackTrace();
	}
	
	@Override
	public WebTemplate print() {
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
