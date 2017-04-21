package de.axone.webtemplate.validator.impl;



/**
 * Get matching postal code validator for given country
 * 
 * @author flo
 */
public class TinValidatorFactory {
	
	private static PatternProvider provider
		= new PostalcodePatternProviderIntern();
	
	public static TinValidator validatorFor( String iso2 ){
		
		String pattern = provider.forCode( iso2 );
		
		return new TinValidator( pattern );
	}
	public static void setPatternProvider( PatternProvider provider ){
		
		TinValidatorFactory.provider = provider;
	}
}
