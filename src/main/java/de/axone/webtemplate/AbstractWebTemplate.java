package de.axone.webtemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.axone.webtemplate.DataHolder.DataHolderItem;
import de.axone.webtemplate.DataHolder.DataHolderItemType;

public abstract class AbstractWebTemplate implements WebTemplate {
	
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
	
	/*
	@Override
	public void reset() {

		parameters.clear();
		holder.clear();
	}
	*/

	/**
	 * Fill templates from parameters
	 */
	protected void autofill() {

		for( String key : holder.getKeys() ) {

			try {
				DataHolderItem item = holder.getItem( key );

				if( item.getType() == DataHolderItemType.VAR ) {

					if( parameters.containsKey( key ) ) {

						item.setValue( parameters.get( key ) );
					}
				}

			} catch( KeyException e ) {
				e.printStackTrace(); // Never happens
			}
		}
		
	}

}
