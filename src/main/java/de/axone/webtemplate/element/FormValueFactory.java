package de.axone.webtemplate.element;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.axone.tools.Str;
import de.axone.webtemplate.Decorator;
import de.axone.webtemplate.converter.Converter;
import de.axone.webtemplate.converter.impl.DateConverter;
import de.axone.webtemplate.elements.impl.HtmlCheckboxElement;
import de.axone.webtemplate.elements.impl.HtmlInputElement;
import de.axone.webtemplate.elements.impl.Option;
import de.axone.webtemplate.form.Form;
import de.axone.webtemplate.form.FormValue;

public interface FormValueFactory {

	public abstract Decorator getDecorator();

	public abstract void setDecorator( Decorator decorator );

	public abstract String getStandardClass();

	public abstract void setLocale( Locale locale );

	public abstract Locale getLocale();

	public abstract void setUseHtml5Input( boolean useHtml5Input );

	public abstract boolean isUseHtml5Input();

	public abstract void setDefaultAjaxValidate( String defaultAjaxValidate );

	public abstract String getDefaultAjaxValidate();

	public abstract FormValue<?> byType( String type, String name, Form form );

	/*
	 * Checkbox
	 */
	public abstract FormValue<Integer> createCheckboxIntegerValue(
			HtmlCheckboxElement.InputType type, Locale locale, String name,
			Integer min, Integer max );

	public abstract FormValue<Integer> createCheckboxIntegerValue( String name,
			Locale locale, Integer min, Integer max );

	public abstract FormValue<Integer> createCheckboxIntegerValue( String name,
			Locale locale );

	public abstract FormValue<Boolean> createCheckboxBooleanValue(
			HtmlCheckboxElement.InputType type, String name );

	public abstract FormValue<Boolean> createCheckboxBooleanValue( String name );

	public abstract <T> FormValue<T> createInputTextValue(
			Converter<T> converter, HtmlInputElement.InputType type,
			String name, int length, boolean nullable );

	/*
	 * Input
	 */
	public abstract FormValue<String> createInputTextValue(
			HtmlInputElement.InputType type, String name, int length,
			boolean nullable, AjaxValidate validate );

	public abstract FormValue<String> createInputTextValue(
			HtmlInputElement.InputType type, String name, int length,
			boolean nullable );

	public abstract FormValue<String> createInputTextValue( String name,
			int length, boolean nullable, AjaxValidate ajaxValidate );

	public abstract FormValue<String> createInputTextValue( String name,
			int length, boolean nullable );

	public abstract FormValue<String> createInputPasswordValue( String name,
			int length, boolean nullable );

	public abstract FormValue<String> createInputPostalcodeValue( String name,
			boolean nullable, String countryCode );

	public abstract FormValue<String> createInputPostalcodeValue( String name,
			boolean nullable, FormValue<String> countryProvider );

	public abstract FormValue<String> createInputEMailValue( String name,
			int length, boolean nullable );

	public abstract FormValue<String> createInputRepeatValue( String name,
			int length, boolean nullable, FormValue<?> other );
	
	public abstract FormValue<String> createInputRepeatEMailValue( String name,
			int length, boolean nullable, FormValue<?> other );

	public abstract FormValue<String> createInputUrlValue( String name,
			int length, boolean nullable );

	public abstract FormValue<String> createInputPhoneValue( String name,
			int length, boolean nullable );

	public abstract FormValue<String> createInputCountryValue( String name,
			boolean nullable );

	public abstract FormValue<String> createInputLanguageValue( String name,
			boolean nullable );

	public abstract FormValue<Date> createInputDateValue(
			HtmlInputElement.InputType type, Locale locale, String name,
			DateConverter converter, boolean nullable,
			AjaxValidate ajaxValidate, String validateString );

	public abstract FormValue<Date> createInputDateValue(
			HtmlInputElement.InputType type, Locale locale, String name,
			boolean nullable, AjaxValidate ajaxValidate );

	public abstract FormValue<Date> createInputDateValue( String name,
			Locale locale, boolean nullable );

	public abstract FormValue<Date> createInputDateValue( String name,
			Locale locale, DateConverter converter, boolean nullable,
			String validateString );

	public abstract FormValue<Date> createInputDateValue( String name,
			boolean nullable );

	public abstract FormValue<BigDecimal> createInputBigDecimalPriceValue(
			HtmlInputElement.InputType type, Locale locale, String name,
			BigDecimal min, BigDecimal max, boolean nullable,
			AjaxValidate ajaxValidate );

	public abstract FormValue<BigDecimal> createInputBigDecimalPriceValue(
			HtmlInputElement.InputType type, Locale locale, String name,
			BigDecimal min, BigDecimal max, boolean nullable, boolean readonly,
			AjaxValidate ajaxValidate );

	public abstract FormValue<BigDecimal> createInputBigDecimalPriceValue(
			String name, Locale locale, boolean nullable );

	public abstract FormValue<BigDecimal> createInputBigDecimalPriceValue(
			String name, boolean nullable );

	public abstract FormValue<BigDecimal> createInputBigDecimalPriceValue(
			String name, boolean nullable, boolean readonly );

	public abstract FormValue<Integer> createInputIntegerValue(
			HtmlInputElement.InputType type, Locale locale, String name,
			Integer min, Integer max, boolean useThousandsSeparator,
			boolean nullable, AjaxValidate ajaxValidate );

	public abstract FormValue<Integer> createInputIntegerValue(
			HtmlInputElement.InputType type, Locale locale, String name,
			Integer min, Integer max, boolean useThousandsSeparator,
			boolean nullable );

	public abstract FormValue<Integer> createInputIntegerValue( String name,
			Locale locale, Integer min, Integer max,
			boolean useThousandsSeparator, boolean nullable );

	public abstract FormValue<Integer> createInputIntegerValue( String name,
			Locale locale, Integer min, Integer max, boolean nullable );

	public abstract FormValue<Integer> createInputIntegerValue( String name,
			Locale locale, boolean useThousandsSeparator, boolean nullable );

	public abstract FormValue<Integer> createInputIntegerValue( String name,
			Locale locale, boolean nullable );

	public abstract FormValue<Integer> createInputIntegerValue( String name,
			boolean nullable );

	public abstract FormValue<Integer> createInputIntegerValue( String name,
			boolean useThousandsSeparator, boolean nullable );

	public abstract FormValue<String> createInputHiddenValue( String name );

	public abstract FormValue<Long> createInputHiddenLongValue( String name,
			Locale locale );

	public abstract FormValue<Long> createInputHiddenLongValue( String name );

	public abstract FormValue<Boolean> createRadioBooleanValue( String name,
			String trueValue, String falseValue, boolean nullable,
			boolean lineBreak );

	public abstract FormValue<String> createRadioValue( String name,
			List<Option> options, boolean nullable );

	/*
	 * Select
	 */
	public abstract FormValue<String> createSelectValue( String name,
			List<Option> options, boolean nullable );

	public abstract FormValue<String> createSelectValue( String name,
			Map<String, String> options, boolean nullable );

	/*
	 * TextArea
	 */
	public abstract FormValue<String> createTextareaTextValue( String name,
			int length, int cols, int rows, boolean nullable );

	public abstract FormValue<String> createTextareaTextValue( String name,
			int length, int cols, int rows, boolean nullable,
			AjaxValidate ajaxValidate );

	
	public static class AjaxValidate {
	
		private final LinkedList<String> values = new LinkedList<String>();
	
		protected AjaxValidate() {}
	
		void add( String value ) {
			values.addLast( value );
		}
	
		String text() {
			if( size() == 0 )
				return null;
			return toString();
		}
	
		int size() {
			return values.size();
		}
	
		@Override
		public String toString() {
	
			StringBuilder result = new StringBuilder();
	
			result.append( "validate[" )
			      .append( Str.join( (val,i) -> "'"+val+"'", values ) )
			      .append( "]" );
	
			return result.toString();
		}
		
		public void required(){
			add( "required" );
		}
		public void lengthMax( int max ){
			add( "length[0," + max + ']' );
		}
		public void phone() {
			add( "phone" );
		}
		public void email() {
			add( "email" );
		}
		public void url() {
			add( "url" );
		}
		public void radio() {
			add( "radio" );
		}
		public void length( int max ){
			add( "length[0," + max + "]" );
		}
		public void isPrice(){
			add( "%check_price_de" );
		}
		public void isNumberDe() {
			add( "%check_number_de" );
		}
	}

	public interface SelectableOption {
		public String key();
		public String title();
	}

}