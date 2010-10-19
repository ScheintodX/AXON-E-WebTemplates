package de.axone.webtemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.axone.tools.E;
import de.axone.webtemplate.AbstractFileWebTemplate.ParserException;

/**
 * Parses attribute strings
 * 
 * Input format is <tt>tag att1="stringval" att2=intval att3</tt>
 * where
 * <ul>
 * <li><tt>att1-3</tt> is the attributes name</li>
 * <li><tt>stringval</tt> is a string value surrounded by either " or ' but is not allowd to contain these
 * <li><tt>intval</tt> is an integer whose only allowed characters are -, 0-9
 * </ul>
 * 
 * @author flo
 */
public abstract class AttributeParser {
	
	public static final String TAG_NAME = "TAG";
	
	static final String NAMECHAR = "[a-zA-Z0-9äöüÄÖÜ_]";
	static final String TAGSTARTCHAR = "[a-zA-ZäöüÄÖÜ]";
	static final String STRINGVALCHAR = "[^ ]";
	static final String INTVALCHAR = "[0-9]";
	static final String QUOT = "\"";
	static final String APOS = "'";
	static final String STRINGVALCHAR_IN_QUOT = "[^"+ QUOT +"]";
	static final String STRINGVALCHAR_IN_APOS = "[^"+ APOS +"]";
	
	static final String WSreq = "\\s+";
	static final String WSopt = "\\s*";
	static final String EQUAL = WSopt + "=" + WSopt;

	static final String NAME = NAMECHAR + "+";
	static final String INTVAL = "(-?" + INTVALCHAR + "+)";
	static final String STRINGVAL_IN_QUOT = QUOT + "(" + STRINGVALCHAR_IN_QUOT + "*)" + QUOT;
	static final String STRINGVAL_IN_APOS = APOS + "(" + STRINGVALCHAR_IN_APOS + "*)" + APOS;
	
	static final String TAGNAME = TAGSTARTCHAR + NAMECHAR + "*";
	static final String ATTRIBUTE = "(" +NAME + ")(?:" + EQUAL + "("+ INTVAL + "|" + STRINGVAL_IN_APOS + "|" + STRINGVAL_IN_QUOT + "))?";
	
	static final String TAG = WSopt + "("+ TAGNAME +")(?:" + WSreq + "("+ ATTRIBUTE + "))*" + WSopt;
	
	static final Pattern tagPattern = Pattern.compile( TAG );
	static final Pattern attributePattern = Pattern.compile( ATTRIBUTE );
	
	static final String testtag = "tag att1 att2=123 att3=\"abc\" att4='abc' att5='a\"b c' att6 = 'abc'";
	
	public static void main( String [] args ){
		
		E.rr( attributePattern );
	}
	
	public static AttributeMap parse( String value ) throws ParserException {
		
		Matcher matcher = tagPattern.matcher( value );
		
		if( matcher.matches() ){
			
			Matcher attMatcher = attributePattern.matcher( value );
			
			AttributeMap map = new AttributeMap();
			
			int c=0;
			while( attMatcher.find() ){
				//E.rr( attMatcher.groupCount() );
				//E.rr( attMatcher.group() );
				//E.rr( "0:" + attMatcher.group(0) );
				//E.rr( "1:" + attMatcher.group(1) );
				//E.rr( "2:" + attMatcher.group(2) );
				//E.rr( "3:" + attMatcher.group(3) );
				//E.rr( "4:" + attMatcher.group(4) );
				//E.rr( "5:" + attMatcher.group(5) );
				//E.rr();
				
				String attName = attMatcher.group(1);
				if( c == 0 ){
					map.put( TAG_NAME, attName );
				} else {
					/*
					String attValue = attMatcher.group(2);
					if( attValue != null ){
    					if( attValue.charAt( 0 ) == '\'' || attValue.charAt( 0 ) == '"' ){
    						attValue = attValue.substring( 1, attValue.length()-1 );
        					map.put( attName, attValue );
    					} else {
    						map.put( attName, Integer.parseInt( attValue ) );
    					}
					} else {
    						map.put( attName, null );
					}
					*/
					if( attMatcher.group( 3 ) != null ){
    					map.put( attName, Integer.parseInt( attMatcher.group(3) ) );
					} else if( attMatcher.group( 4 ) != null ){
    					map.put( attName, attMatcher.group(4) );
					} else if( attMatcher.group( 5 ) != null ){
    					map.put( attName, attMatcher.group(5) );
					} else {
						map.put( attName, null );
					}
				}
				
				c++;
			}
			
			return map;
		}
		
		throw new ParserException( "Not valid: " + value );
	}
	

}
