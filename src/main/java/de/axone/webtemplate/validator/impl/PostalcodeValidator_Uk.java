package de.axone.webtemplate.validator.impl;


public class PostalcodeValidator_Uk extends PostalcodeValidator {
	
	public static final String DEC5 = "n{5}";
	public static final String DEC4 = "n{4}";
	
	public static final String DE = DEC5;
	public static final String AT = DEC4;
	
	private static final String _A9 = "an";		//A9 9AA
	private static final String _A99 = "ann";	//A99 9AA
	private static final String _AA9 = "aan";	//AA9 9AA
	private static final String _AA99 = "aann";	//AA99 9AA
	private static final String _A9A = "ana";	//A9A 9AA
	private static final String _AA9A = "aana";	//AA9A 9AA
	private static final String __9AA = " naa";
	
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
				
				String result = GB_PREFIX.replaceAll( _AA9, prefix+"n" );
				return result;
			}
		}
		
		throw new IllegalArgumentException( "Wrong format: " + pattern );
	}
	
	public PostalcodeValidator_Uk( String pattern ){
		
		super( makePattern( pattern ) );
	}
}
