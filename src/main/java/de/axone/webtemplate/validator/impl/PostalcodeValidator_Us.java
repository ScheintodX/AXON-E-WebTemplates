package de.axone.webtemplate.validator.impl;

public class PostalcodeValidator_Us extends PostalcodeValidator {
	
	public PostalcodeValidator_Us( String pattern ){
		super( "nnnnn(-nnnn)?" );
	}
}
