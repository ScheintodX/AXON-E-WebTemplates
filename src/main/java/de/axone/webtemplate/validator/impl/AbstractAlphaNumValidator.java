package de.axone.webtemplate.validator.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import de.axone.webtemplate.form.TKeysCommon;
import de.axone.webtemplate.form.Translator;
import de.axone.webtemplate.validator.AbstractValidator;

public abstract class AbstractAlphaNumValidator extends AbstractValidator<String> {
	
	final Pattern pattern;
	final boolean ignoreWhitespace;
	final String originalPattern;

	public AbstractAlphaNumValidator( String pattern ){
		this( pattern, false );
	}
		
	public AbstractAlphaNumValidator( String pattern, boolean ignoreWhitespace ){
		
		this.ignoreWhitespace = ignoreWhitespace;
		this.originalPattern = pattern;
		
		// Allow for empty validators which will accept everything
		// in order to check empty zip fields
		if( pattern == null ){
			this.pattern = null;
			return;
		}
		
		pattern = pattern.trim();
		pattern = pattern.replaceAll( " ", " ?" );
		pattern = pattern.replaceAll( "a", "[a-zA-Z]" );
		pattern = pattern.replaceAll( "n", "[0-9]" );
		pattern = pattern.replaceAll( "x", "[a-zA-Z0-9]" );
		if( ignoreWhitespace ){
			pattern = pattern.replaceAll( " ", "\\\\s?" );
		}
		
		this.pattern = Pattern.compile( pattern );
	}
	
	@Override
	public String validate( String check, @Nullable Translator t ){
		
		if( check == null ) return null;
		if( pattern == null ) return null;
		
		Matcher matcher = pattern.matcher( check.toUpperCase() );
		if( ! matcher.matches() ) {
			return error( originalPattern, t );
		}
		
		return null;
	}
	
	protected abstract String error( String originalPattern, @Nullable Translator t );

	protected String examplify( String format, Translator t ){
		
		StringBuilder result = new StringBuilder();
		
		char a='A', u='U';
		char n='1';
		
		for( char c : format.toCharArray() ){
			
			if( c=='a' ) result.append( a++ );
			else if( c=='n' ) result.append( n++ );
			else if( c=='x' ) result.append( u++ );
			else if( c=='|' ) result.append( ' ' ).append( t != null ? t.translate( TKeysCommon.or ) : "|" ).append( ' ' );
			else result.append( c );
		}
		
		return result.toString();
	}

	@Override
	public String toString() {
		return originalPattern + "->" + pattern.toString();
	}

}
