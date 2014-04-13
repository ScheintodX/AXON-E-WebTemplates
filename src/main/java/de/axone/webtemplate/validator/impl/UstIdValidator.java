package de.axone.webtemplate.validator.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.axone.webtemplate.validator.Validator;

public class UstIdValidator implements Validator<String> {
	
	private static final UstId [] UST_IDS = {
		
		// s. http://de.wikipedia.org/wiki/Umsatzsteuer-Identifikationsnummer
		
		new UstId( "AT", "Österreich", "ATU99999999" ),
			//	Block mit 9 Zeichen, deren erstes Zeichen nach dem Präfix immer ein 'U' ist[11].	Umsatzsteuer-Identifikationsnummer, ATU-Nummer oder UID-Nummer	UID
		new UstId( "BE", "Belgien", "BE0999999999", "BE9999999999" ),
			//	Block mit 10 Ziffern, alte neunstellige USt-IdNrn. werden durch Voranstellen der Ziffer 0 ergänzt.[12]	Numéro de TVA / BTW-nummer / Mehrwertsteuernummer	N° TVA / BTW-nr. / MwSt.-Nr.
		new UstId( "BG", "Bulgarien", "BG999999999", "BG9999999999" ),
			// (DE)	Block mit 9 Ziffern oder Block mit 10 Ziffern		DDS
		new UstId( "CH", "Schweiz", "CHE-123.456.789", "CHE-123.456.789MWST", "CHE-123.456.789HR/MWST" ),
			// Block mit 9 Ziffern, die letzte Stelle ist eine Prüfziffer (Modulo 11). Ist eine UID-Einheit (Unternehmung etc.) im Handelsregister eingetragen, so kann durch die Unternehmung optional die UID-Ergänzung „HR“ angegeben werden. Wenn die Unternehmung gleichzeitig MWST-pflichtig ist und dieses „HR“ verwenden möchte, wird jene des Handelsregisters zuerst genannt und durch einen Schrägstrich von der UID-Ergänzung „MWST“ abgetrennt. Fällt eine Voraussetzung für die UID-Ergänzung weg, so wird diese nicht mehr geführt. Es können zudem nur UID-Ergänzungen gleicher Sprache der UID nachgestellt werden. Der Zusatz MWST kann landessprachabhängig auch als „IVA“ (franz.) oder „TVA“ (ital.) dargestellt werden. Gleiches gilt für den Zusatz HR, der als „RC“ dargestellt werden kann. Die Interpunktion kann weggelassen werden.		
		new UstId( "CY", "Zypern", "CY99999999x" ),
			//	Block mit 9 Zeichen, die letzte Stelle muss ein Buchstabe sein.[13]		
		new UstId( "CZ", "Tschechische Republik", "CZ99999999", "CZ999999999", "CZ9999999999" ),
			// (DE)	Block mit entweder 8, 9 oder 10 Ziffern	daňové identifikační číslo	DIČ
		new UstId( "DE", "Deutschland", "DE999999999" ),
			//	Block mit 9 Ziffern[14]	Umsatzsteuer-Identifikationsnummer	USt-IdNr.
		new UstId( "DK", "Dänemark", "DK99 99 99 99" ),
			//	4 Blöcke mit je 2 Ziffern		SE-Nr.
		new UstId( "EE", "Estland", "EE999999999" ),
			//	Block mit 9 Ziffern		KMKR nr
		new UstId( "GR", "Griechenland", "EL999999999" ),
			//	Block mit 9 Ziffern	Αριθμός Φορολογικού Μητρώου [Arithmós Phorologikoú Mētrṓou]	ΑΦΜ
		new UstId( "ES", "Spanien", "ESX9999999X" ),
			//	Block mit 9 Zeichen. Das erste und letzte Zeichen können entweder numerisch oder alphanumerisch sein, aber sie dürfen nicht beide numerisch sein. Gebildet aus "ES" gefolgt von der CIF (Código de Identificación Fiscal).	Número IVA	
		new UstId( "FI", "Finnland", "FI99999999" ),
			//	Block mit 8 Ziffern	Y-tunnus	ALV-NRO
		new UstId( "FR", "Frankreich", "FRXX 999999999" ),
			//	Block mit 2 Zeichen, die entweder numerisch oder alphanumerisch sein können, gefolgt von einem Block mit 9 Ziffern	TVA intracommunautaire	ID. TVA
		new UstId( "GB", "Vereinigtes Königreich", "GB999 9999 99", "GB999 9999 99 999", "GBGD999", "GBHA999" ),
			// (DE)	Ein Block mit 3 Ziffern, ein Block mit 4 Ziffern und ein Block mit 2 Ziffern; oder das obige gefolgt von einem Block mit 3 Ziffern oder einem Block mit 5 Zeichen	VAT Registration Number	VAT Reg No
		new UstId( "HU", "Ungarn", "HU99999999" ),
			//	Block mit 8 Ziffern	Közösségi adószám	
		new UstId( "IE", "Irland", "IE9S99999L" ),
			//	Block mit 8 Zeichen, die zweite Stelle kann und die letzte Stelle muss ein Buchstabe sein	Value added tax identification no.	VAT-No
		new UstId( "IT", "Italien", "IT99999999999" ),
			//	Block mit 11 Ziffern	Numero Partita I.V.A. (MwSt.Nr.)	P. IVA, Part. IVA
		new UstId( "LT", "Litauen", "LT999999999", "LT999999999999" ),
			// (DE)	Block mit 9 Ziffern oder Block mit 12 Ziffern	PVM mokėtojo kodas	PVM MK
		new UstId( "LU", "Luxemburg", "LU99999999" ),
			//	Block mit 8 Ziffern	le numéro d’identification à la taxe sur la valeur ajoutée	IBLC Nr.
		new UstId( "LV", "Lettland", "LV99999999999" ),
			//	Block mit 11 Ziffern		
		new UstId( "MT", "Malta", "MT99999999" ),
			//	Block mit 8 Ziffern		
		new UstId( "NL", "Niederlande", "NL999999999B99" ),
			//	Block mit 12 Zeichen. Die 10. Stelle nach dem Präfix ist immer "B".	BTW-identificatienummer	BTW-nummer
		new UstId( "PL", "Polen", "PL9999999999" ),
			//	Block mit 10 Ziffern	Numer Identyfikacji Podatkowej (Steuer-Identifikationsnummer)	NIP
		new UstId( "PT", "Portugal", "PT999999999" ),
			//	Block mit 9 Ziffern	Numero de identificaçao fiscal	NIPC
		new UstId( "RO", "Rumänien", "RO999999999" ),
			//	Block mit mindestens 2 und maximal 10 Ziffern	Cod Unic Identificare	CUI
		new UstId( "SE", "Schweden", "SE999999999999" ),
			//	Block mit 12 Ziffern	Momsregistreringsnummer	MomsNr.
		new UstId( "SI", "Slowenien", "SI99999999" ),	
			//	Block mit 8 Ziffern	Davčna številka	ID st. za DDV
		new UstId( "SK", "Slowakei", "SK9999999999" )
			//	Block mit 10 Ziffern	identifikačné číslo pre daň z pridanej hodnoty	IČ DPH
	};

	@Override
	public boolean isValid( String value ) {
		
		return validate( value ) == null;
		
	}

	@Override
	public String validate( String value ) {
		
		if( value == null ) return null;
		value = value.trim();
		if( value.length() == 0 ) return null;
		
		if( value.length() < 2 ){
			return "ERROR_UST_INVALID_TOO_SHORT";
		}
		
		// All checks are done upper case
		value = value.toUpperCase();
		
		for( UstId ustId : UST_IDS ){
			
			Boolean result = ustId.isValid( value );
			
			if( result == false ){
				return "ERROR_UST_INVALID_WRONG_FORMAT:" + ustId.iso2;
			} else if( result == true ){
				return null;
			}
			// else proceed
		}
		
		return "ERROR_UST_INVALID_NO_MATCHING_COUNTRY:" + value.substring( 0,1 );
	}
	
	private static class UstId {
		
		private final String iso2;
		
		//private final String [] patternCodes;
		private final Pattern [] patterns;
		
		private UstId( String iso2, String countryName, String ... patternCodes ){
			
			this.iso2 = iso2;
			//this.patternCodes = patternCodes;
			
			this.patterns = new Pattern[ patternCodes.length ];
			
			for( int i = 0; i < patternCodes.length; i++ ){
				
				String patternStr = patternCodes[ i ];
				
				this.patterns[ i ] = buildPattern( patternStr );
			}
		}
		
		private Pattern buildPattern( String patternStr ){
			
			// Trim first 2 letters because they are checked differently in advance
			patternStr = patternStr.substring( 2 );
			
			patternStr = patternStr.replace( "0", "0?" );
			patternStr = patternStr.replace( "9", "[0-9]" );
			patternStr = patternStr.replace( ".", "\\.?" );
			patternStr = patternStr.replace( " ", "\\s?" );
			patternStr = patternStr.replace( "x", "[A-Z]" );
			
			Pattern pattern = Pattern.compile( patternStr );
				
			return pattern;
		}

		/**
		 * return true/false/null
		 * true: is valid
		 * null: not applicable (bacause 2 first letters don't match)
		 * false: applicable but doesn't match (=ERROR)
		 * @param value
		 * @return
		 */
		private Boolean isValid( String value ) {
			
			// Skip if first 2 iso2 doesn't match
			if( ! value.substring( 0, 1 ).equals( iso2 ) ) return null;
			
			for( Pattern pattern : patterns ){
				
				Matcher matcher = pattern.matcher( value );
				if( matcher.matches() ){
					return true;
				}
			}
			return false;
		}

	}

}
