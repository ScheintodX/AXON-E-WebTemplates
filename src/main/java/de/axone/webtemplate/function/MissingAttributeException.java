package de.axone.webtemplate.function;


public class MissingAttributeException extends FunctionException {
		/**
	 * 
	 */
	private static final long serialVersionUID = -3432944463811886273L;

		public MissingAttributeException( String attributeName ){
			super( "Missing: " + attributeName );
		}
}
