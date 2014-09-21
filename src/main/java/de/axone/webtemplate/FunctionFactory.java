package de.axone.webtemplate;

import de.axone.webtemplate.function.Function;

/**
 * A factory for functions
 * 
 * The contract for this factory is little: Function objects don't have state.
 * So the returned function for get can be a new object, sometimes a used object
 * or even every time the same object.
 * 
 * Nevertheless Functions can be parametrised so that in a different context
 * it is most likely to get another function object.
 * 
 * "add" inserts a Function. If the function is inserted this way it is the very
 * same function which is returned for get.
 * 
 * Implementations could throw UnsupportedOperationException if they don't want
 * Functions to be added.
 * 
 * @author flo
 */
public interface FunctionFactory {

	/**
	 * @return true if a function of this name is available
	 * 
	 * @param name
	 */
	public boolean has( String name );
	
	/**
	 * @return the function of this name
	 * 
	 * @param name
	 */
	public Function get( String name );
	
	/**
	 * Inserts a function
	 * 
	 * @param name
	 * @param function
	 * @throws UnsupportedOperationException if not implemented
	 * 
	 */
	public void add( String name, Function function );
}
