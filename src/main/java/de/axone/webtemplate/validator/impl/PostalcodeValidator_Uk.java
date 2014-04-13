package de.axone.webtemplate.validator.impl;


public class PostalcodeValidator_Uk extends PostalcodeValidator {
	
	private static final String _A = "a";
	private static final String _9 = "n";
	private static final String _ = " ";

	public static final String DEC5 = _9 + "{5}";
	public static final String DEC4 = _9 + "{4}";
	
	public static final String DE = DEC5;
	public static final String AT = DEC4;
	
	private static final String _A9 = _A+_9;			//A9 9AA
	private static final String _A99 = _A+_9+_9;		//A99 9AA
	private static final String _AA9 = _A+_A+_9;		//AA9 9AA
	private static final String _AA99 = _A+_A+_9+_9;	//AA99 9AA
	private static final String _A9A = _A+_9+_A;		//A9A 9AA
	private static final String _AA9A = _A+_A+_9+_A;	//AA9A 9AA
	private static final String __9AA = _+_9+_A+_A;
	public static final String GB =  
		"(" + _A9+__9AA + ")|(" + _A99+__9AA + ")|(" + _AA9+__9AA + ")|(" + 
		_AA99+__9AA + ")|(" + _A9A+__9AA + ")|(" + _AA9A+__9AA + ")" ;
	public static final String GB_PREFIX = 
		"(" + _AA9+__9AA + ")|(" + _AA99+__9AA + ")|(" + _AA9A+__9AA + ")" ;
	
	private static String makePattern( String pattern ){
		
		if( pattern.startsWith( "uk" ) ){
			
			if( pattern.length() == 2 ){
				return GB;
			} else if( 
					pattern.length() == 6 &&
					pattern.charAt( 2 ) == '{' && 
					pattern.charAt( 5 ) == '}' 
			){
				String prefix = pattern.substring( 3,5 );
				prefix = prefix.toUpperCase();
				
				String result = GB_PREFIX.replaceAll( _AA9, prefix+_9 );
				return result;
			}
		}
		
		throw new IllegalArgumentException( "Wrong format: " + pattern );
	}
	
	public PostalcodeValidator_Uk( String pattern ){
		
		super( makePattern( pattern ) );
	}
}
