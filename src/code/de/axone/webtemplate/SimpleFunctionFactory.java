package de.axone.webtemplate;

import java.util.Formatter;
import java.util.Map;
import java.util.TreeMap;

import de.axone.webtemplate.function.Function;

public class SimpleFunctionFactory implements FunctionFactory {
	
	private Map<String,Function> functions = new TreeMap<String,Function>();

	@Override
	public boolean has( String name ) {
		return functions.containsKey( name );
	}

	@Override
	public Function get( String name ) {
		return functions.get( name );
	}

	@Override
	public void add( String name, Function function ) {
		functions.put( name, function );
	}

	@Override
	public String toString(){
		
		StringBuilder result = new StringBuilder();
		Formatter formatter = new Formatter( result );
		for( String key : functions.keySet() ){
			
			Function function = functions.get( key );
			formatter.format( "%s: %s\n", key, function );
		}
		return result.toString();
	}

}
