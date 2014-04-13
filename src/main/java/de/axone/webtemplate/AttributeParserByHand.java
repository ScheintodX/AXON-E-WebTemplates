package de.axone.webtemplate;

import de.axone.webtemplate.AbstractFileWebTemplate.ParserException;



/**
 * Parses atribute strings
 * 
 * Input format is <tt>tag att1="stringval" att2=intval att3</tt>
 * 
 * This class is much faster than the Regex based one. But it contains
 * some strange errors and should be rewritten in terms of code clearity
 * before use.
 * 
 * TODO: Check. evtl. mit JavaCC nochmal probieren?
 * 
 * @author flo
 */
public abstract class AttributeParserByHand {
	
	public static AttributeMap parse( String tag ) throws ParserException {
		
		//E.rr( tag );

		AttributeMap result = new AttributeMap();
		
		int len = tag.length();

		// Skip WS
		int i =0;
		for( ; i < len && isWhiteSpace( tag.charAt( i ) ); i++ );
		
		// Tag name
		StringBuilder name = new StringBuilder();
		for( ; i < len ; i++ ) {

			char ch = tag.charAt( i );

			if( ! isValidChar( ch ) ) break;
			name.append( ch );
		}
		
		result.put( AttributeParser.TAG_NAME, new Attribute( name.toString(), false ) );
		
		// Attribute Name=Value pairs
		StringBuilder attrName=null, attrValue=null;
		while( i < len ){ //Key-Val pair
			
			for( ; i < len; i++ ){ // Whitespace
    			if( !isWhiteSpace( tag.charAt( i ) ) ) break;
			}
			if( i == len ) break;
			
			attrName = new StringBuilder();
			
			for( ; i < len; i++ ){ // Name
    			char ch = tag.charAt( i );
				if( !isNameChar( ch ) ) break;
				attrName.append( ch );
			}
			
			if( attrName.length() < 1 )
				throw new ParserException( "Wrong name: \"" + attrName.toString() + "\" last char was: " );
			
			boolean foundSep = false; // Sep+WS
			for( ; i < len; i++ ){
    			char ch = tag.charAt( i );
				if( ch == '=' ) foundSep = true;
    			if( !isNameValSep( ch ) ) break;
			}
			
			boolean foundDel = false;
			char delimiterType = 'x';
			if( foundSep ){
				
				if( isValueDelimiter( tag.charAt( i ) ) ){ // "
					foundDel = true;
					delimiterType = tag.charAt( i );
					i++;
				}
				
				attrValue = new StringBuilder();
				for( ; i < len; i++ ){ // Value
					
        			char ch = tag.charAt( i );
        			if( (foundDel && ch == delimiterType) || (!foundDel && !isValidInteger( ch ) ) ) break;
        			attrValue.append( ch );
				}
				
				// Skip closing delimiter
				if( foundDel && tag.charAt( i ) == delimiterType ) i++;
				else if( foundDel ) throw new ParserException( "Missing closing delimiter" );
			}
			
			String attrNameStr = attrName.toString();
			
			if( attrNameStr.length() > 0 ){
				
				if( result.containsKey( attrNameStr ) )
					throw new ParserException( "Duplicate attribute: " + attrNameStr );
				
    			if( attrValue == null ){
    				result.put( attrNameStr, null );
    			} else {
        			String attrValueStr = attrValue.toString();
    				if( foundDel ){
    					result.put( attrNameStr, new Attribute( attrValueStr, false ) );
    				} else {
    					result.put( attrNameStr, new Attribute( attrValueStr, true ) );
    				}
    				attrValue=null;
    			}
    		}
			attrName = null;
			
		}

		return result;
	}
	
	protected static boolean isValidChar( char ch ){
		
		return
			ch >= 'a' && ch <= 'z' ||
			ch >= 'A' && ch <= 'Z' ||
			ch >= '0' && ch <= '9' ||
			ch == '_' || ch == '.'
			;
	}
	protected static boolean isValidInteger( char ch ){
		return ch >= '0' && ch <= '9';
	}
	
	protected static boolean isWhiteSpace( char ch ){
		return ch <= ' ';
	}
	
	protected static boolean isNameValSep( char ch ){
		return ch == '=' || isWhiteSpace( ch );
	}
	
	protected static boolean isNameChar( char ch ){
		return isValidChar( ch );
	}
	
	protected static boolean isStringValueChar( char ch ){
		return ch == ' ' || isValidChar( ch );
	}
	
	protected static boolean isValueDelimiter( char ch ){
		return ch == '"' || ch == '\'';
	}
	
	

}
