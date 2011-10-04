package de.axone.webtemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	public static void main( String [] args ) throws Exception {
		
		(new AttributeParserTest()).testRegexes();
		(new AttributeParserTest()).testParser();
		/*
		E.rr( attributePattern );
		E.rr( parse( testtag ) );
		E.rr( parse( "" ) );
		*/
	}
	
	public static AttributeMap parse( String value ) throws ParserException {
		
		Matcher matcher = tagPattern.matcher( value );
		
		if( matcher.matches() ){
			
			Matcher attMatcher = attributePattern.matcher( value );
			
			AttributeMap map = new AttributeMap();
			
			int c=0;
			while( attMatcher.find() ){
				
				String attName = attMatcher.group(1);
				if( c == 0 ){
					map.put( TAG_NAME, new Attribute( attName, false ) );
				} else {

					if( attMatcher.group( 3 ) != null ){
    					map.put( attName, new Attribute( attMatcher.group(3), true ) );
					} else if( attMatcher.group( 4 ) != null ){
    					map.put( attName, new Attribute( attMatcher.group(4), false ) );
					} else if( attMatcher.group( 5 ) != null ){
    					map.put( attName, new Attribute( attMatcher.group(5), false ) );
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
