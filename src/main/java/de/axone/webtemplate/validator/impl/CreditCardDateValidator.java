package de.axone.webtemplate.validator.impl;

import java.util.Date;

import javax.annotation.Nullable;

import de.axone.webtemplate.form.Translator;
import de.axone.webtemplate.validator.AbstractValidator;

public class CreditCardDateValidator extends AbstractValidator<Date> {

	//TODO: Implementierung fehlt. Vor allem Durchblick und eine Idee.

	private static final String TOO_OLD = "VALIDATOR_TOO_OLD";

	@Override
	public String validate( Date value, @Nullable Translator t ) {

		Date now = new Date();

		if( now.getTime() > value.getTime() ) return TOO_OLD;

		return null;
	}

	/*
	private YearValidator yearValidator = new YearValidator();
	private MonthValidator monthValidator = new MonthValidator( yearValidator );
	public Validator<Integer> getYearValidator(){

		return yearValidator;
	}
	public Validator<Integer> getMonthValidator(){

		return monthValidator;
	}

	class YearValidator implements Validator<Integer> {

		@Override
		public boolean isValid( Integer value ) {
			return false;
		}

		@Override
		public String validate( Integer value ) {
			return null;
		}

	}

	class MonthValidator implements Validator<Integer> {

		YearValidator yearValidator;
		MonthValidator( YearValidator yearValidator ){
		}

		@Override
		public boolean isValid( Integer value ) {
			return false;
		}

		@Override
		public String validate( Integer value ) {
			return null;
		}

	}
	*/

}
