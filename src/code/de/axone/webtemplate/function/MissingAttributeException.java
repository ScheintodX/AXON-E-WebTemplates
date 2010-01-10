package de.axone.webtemplate.function;


public class MissingAttributeException extends FunctionException {
		public MissingAttributeException( String attributeName ){
			super( "Missing: " + attributeName );
		}
}
