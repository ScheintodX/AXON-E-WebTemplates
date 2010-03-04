package de.axone.webtemplate.validator.impl;

import de.axone.i18n.Country;

public class PostalcodePatternProviderIntern implements PostalcodePatternProvider{
	
	@Override
	public String forCode( String iso2 ) {
		
		Country country = Country.forIso2( iso2 );
		if( country == null )
			throw new IllegalArgumentException( "No country for " + iso2 );
		
		return country.postalcode();
	}

}
