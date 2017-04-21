package de.axone.webtemplate.validator.impl;

import de.axone.i18n.StaticCountries;

public class TinPatternProviderIntern implements PatternProvider{
	
	@Override
	public String forCode( String iso2 ) {
		
		StaticCountries country = StaticCountries.forIso2( iso2 );
		if( country == null )
			throw new IllegalArgumentException( "No country for " + iso2 );
		
		return country.getTin();
	}

}
