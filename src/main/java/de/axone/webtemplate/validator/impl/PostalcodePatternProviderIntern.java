package de.axone.webtemplate.validator.impl;

import de.axone.i18n.Country;
import de.axone.tools.E;

public class PostalcodePatternProviderIntern implements PostalcodePatternProvider{
	
	@Override
	public String forCode( String iso2 ) {
		E.rr( iso2 );
		
		Country country = Country.forIso2( iso2 );
		E.rr( country );
		if( country == null )
			throw new IllegalArgumentException( "No country for " + iso2 );
		
		return country.postalcode();
	}

}
