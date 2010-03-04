package de.axone.webtemplate.validator.impl;

import de.axone.tools.E;


public class PostalcodeValidatorFactory {
	
	private static PostalcodePatternProvider provider
		= new PostalcodePatternProviderIntern();
	
	public static PostalcodeValidator validatorFor( String iso2 ){
		
		E.rr( iso2 );
		
		String pattern = provider.forCode( iso2 );
		
		E.rr( pattern );
		
		if( pattern == null ){
			return new PostalcodeValidator_Empty();
		} else if( pattern.startsWith( "de" ) ){
			return new PostalcodeValidator_De( pattern );
		} else if( pattern.startsWith( "us" ) ){
			return new PostalcodeValidator_Us( pattern );
		} else if( pattern.startsWith( "fr" ) ){
			return new PostalcodeValidator_Fr( pattern );
		} else if( pattern.startsWith( "uk" ) ){
			return new PostalcodeValidator_Uk( pattern );
		} else {
			return new PostalcodeValidator( pattern );
		}
	}
	public static void setPatternProvider( PostalcodePatternProvider provider ){
		
		PostalcodeValidatorFactory.provider = provider;
	}
}
