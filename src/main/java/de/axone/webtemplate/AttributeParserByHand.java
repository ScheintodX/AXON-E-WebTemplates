package de.axone.webtemplate;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import de.axone.web.SuperURL;
import de.axone.web.SuperURLBuilders;
import de.axone.webtemplate.AbstractFileWebTemplate.ParserException;



/**
 * Parses attribute Strings
 * 
 * Input format is <tt>tag att1="stringval" att2="intval" att3</tt>
 * 
 * This class is much faster than the Regex based one. But it contains
 * some strange errors and should be rewritten in terms of code clearity
 * before use.
 * 
 * TODO: delim ist unnötig. Müssen aber die Templates auch alle überarbeitet werden.
 * 
 * @author flo
 */
public abstract class AttributeParserByHand {
	
	public static AttributeMap empty() {
		
		return new AttributeMap();
	}
	
	public static AttributeMap from( HttpServletRequest req ) {
		
		return from( SuperURLBuilders.fromRequest().build( req ) );
	}
	
	public static AttributeMap from( SuperURL url ) {
		
		SuperURL.Query query = url.getQuery();
		
		Map<String,String[]> parts = query.toParameterMap();
		AttributeMap result = new AttributeMap();
		
		// throw away additional parameters with same name
		for( Map.Entry<String,String[]> entry : parts.entrySet() ) {
			
			String name = entry.getKey();
			String [] values = entry.getValue();
			
			if( values.length > 0 )
					result.putString( name, values[ 0 ] );
		}
		
		return result;
	}
	
	public static AttributeMap parse( String term ) throws ParserException {
		
		AttributeMap result = new AttributeMap();
		
		int len = term.length();

		// Skip WS
		int i = 0;
		for( ; i < len && isWhiteSpace( term.charAt( i ) ); i++ );
		
		// Tag name
		StringBuilder name = new StringBuilder();
		for( ; i < len ; i++ ) {

			char ch = term.charAt( i );

			if( ! isValidChar( ch ) ) break;
			name.append( ch );
		}
		
		result.putString( TAG_NAME, name.toString() );
		
		// Attribute Name=Value pairs
		StringBuilder attrName = null, attrValue = null;
		while( i < len ){ //Key-Val pair
			
			for( ; i < len; i++ ){ // Whitespace
    			if( !isWhiteSpace( term.charAt( i ) ) ) break;
			}
			if( i == len ) break;
			
			attrName = new StringBuilder();
			
			for( ; i < len; i++ ){ // Name
    			char ch = term.charAt( i );
				if( !isNameChar( ch ) ) break;
				attrName.append( ch );
			}
			
			if( attrName.length() < 1 )
				throw new ParserException( "Wrong name: \"" + attrName.toString() + "\" last char was: " );
			
			boolean foundSep = false; // Sep+WS
			for( ; i < len; i++ ){
    			char ch = term.charAt( i );
				if( ch == '=' ) foundSep = true;
    			if( !isNameValSep( ch ) ) break;
			}
			
			boolean foundDel = false;
			char delimiterType = 'x';
			if( foundSep ){
				
				if( isValueDelimiter( term.charAt( i ) ) ){ // "
					foundDel = true;
					delimiterType = term.charAt( i );
					i++;
				}
				
				attrValue = new StringBuilder();
				for( ; i < len; i++ ){ // Value
					
        			char ch = term.charAt( i );
        			if( (foundDel && ch == delimiterType) || (!foundDel && !isValidInteger( ch ) ) ) break;
        			attrValue.append( ch );
				}
				
				// Skip closing delimiter
				if( foundDel && term.charAt( i ) == delimiterType ) i++;
				else if( foundDel ) throw new ParserException( "Missing closing delimiter" );
			}
			
			String attrNameStr = attrName.toString();
			
			if( attrNameStr.length() > 0 ){
				
				if( result.containsKey( attrNameStr ) )
					throw new ParserException( "Duplicate attribute: " + attrNameStr );
				
    			if( attrValue == null ){
    				result.putString( attrNameStr, null );
    			} else {
        			String attrValueStr = attrValue.toString();
    				if( foundDel ){
    					result.putString( attrNameStr, attrValueStr );
    				} else {
    					result.putInteger( attrNameStr, attrValueStr );
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
			ch == '_' || ch == '.' ||
			ch == '@' 
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

	public static final String TAG_NAME = "TAG";
	
	

}
