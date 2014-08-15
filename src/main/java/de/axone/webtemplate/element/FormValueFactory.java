package de.axone.webtemplate.element;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.axone.webtemplate.Decorator;
import de.axone.webtemplate.DefaultDecorator;
import de.axone.webtemplate.converter.Converter;
import de.axone.webtemplate.converter.impl.BigDecimalConverter;
import de.axone.webtemplate.converter.impl.BooleanCheckboxConverter;
import de.axone.webtemplate.converter.impl.BooleanConverter;
import de.axone.webtemplate.converter.impl.DateConverter;
import de.axone.webtemplate.converter.impl.IntegerConverter;
import de.axone.webtemplate.converter.impl.LongConverter;
import de.axone.webtemplate.converter.impl.StringConverter;
import de.axone.webtemplate.elements.impl.HtmlCheckboxElement;
import de.axone.webtemplate.elements.impl.HtmlInputElement;
import de.axone.webtemplate.elements.impl.HtmlRadioElement;
import de.axone.webtemplate.elements.impl.HtmlSelectElement;
import de.axone.webtemplate.elements.impl.HtmlSelectElement.OptionComparator;
import de.axone.webtemplate.elements.impl.HtmlTextAreaElement;
import de.axone.webtemplate.elements.impl.Option;
import de.axone.webtemplate.elements.impl.OptionImpl;
import de.axone.webtemplate.elements.impl.OptionList;
import de.axone.webtemplate.form.Form;
import de.axone.webtemplate.form.FormValue;
import de.axone.webtemplate.form.FormValueImpl;
import de.axone.webtemplate.validator.impl.CountryValidator;
import de.axone.webtemplate.validator.impl.EMailValidator;
import de.axone.webtemplate.validator.impl.EqualsValidator;
import de.axone.webtemplate.validator.impl.InCollectionValidator;
import de.axone.webtemplate.validator.impl.LanguageValidator;
import de.axone.webtemplate.validator.impl.LengthValidator;
import de.axone.webtemplate.validator.impl.MinMaxValidator;
import de.axone.webtemplate.validator.impl.NotNullValidator;
import de.axone.webtemplate.validator.impl.PhoneValidator;
import de.axone.webtemplate.validator.impl.PostalcodeValidator_Dynamic;
import de.axone.webtemplate.validator.impl.PostalcodeValidator_Dynamic.CountryProvider;
import de.axone.webtemplate.validator.impl.UrlValidator;

public class FormValueFactory {

	private boolean useHtml5Input;
	private Decorator decorator = new DefaultDecorator();
	private Locale defaultLocale = Locale.GERMANY;
	private String defaultAjaxValidate;
	
	public FormValueFactory(){
		setUseHtml5Input( false );
	}

	public Decorator getDecorator() {
		return decorator;
	}
	public void setDecorator( Decorator decorator ) {
		this.decorator = decorator;
	}

	public String getStandardClass(){
		return decorator.getStandardClass();
	}
	
	public void setLocale( Locale locale ){
		this.defaultLocale = locale;
	}
	public Locale getLocale(){
		return defaultLocale;
	}
	
	public void setUseHtml5Input( boolean useHtml5Input ){
		this.useHtml5Input = useHtml5Input;
	}
	public boolean isUseHtml5Input(){
		return this.useHtml5Input;
	}
	
	public void setDefaultAjaxValidate( String defaultAjaxValidate ){
		this.defaultAjaxValidate = defaultAjaxValidate;
	}
	public String getDefaultAjaxValidate(){
		return this.defaultAjaxValidate;
	}
	
	protected HtmlInputElement.InputType DATE_INPUT_TYPE(){
		return useHtml5Input ? HtmlInputElement.InputType.DATE : HtmlInputElement.InputType.TEXT;
	}
	protected HtmlInputElement.InputType NUMBER_INPUT_TYPE(){
		return useHtml5Input ? HtmlInputElement.InputType.NUMBER : HtmlInputElement.InputType.TEXT;
	}
	
	public FormValue<?> byType( String type, String name, Form form ){
		
		List<String> options = null;
		boolean nullable=true;
		boolean lineBreak=false;
		if( form != null ) {
			options = Arrays.asList( form.options() );
			if( options.contains( "notnull" ) ) nullable = false;
			if( options.contains( "linebreak" ) ) lineBreak = true;
		}
		
		switch( type ){
		case "short":
		case "int":
		case "long":
			nullable=false;
			//$FALL-THROUGH$
		case "java.lang.Short":
		case "java.lang.Integer":
		case "java.lang.Long":
		case "java.math.BigDecimal":
			return this.createInputIntegerValue( name, nullable );
		case "java.lang.String":
			return this.createInputTextValue( name, 255, nullable );
		case "java.util.Date":
			return this.createInputDateValue( name, nullable );
		case "boolean":
			nullable=false;
			//$FALL-THROUGH$
		case "java.lang.Boolean":
			String trueVal = "ja", falseVal="nein";
			if( options != null && options.size() >= 2 ){
				trueVal = options.get( 0 );
				falseVal = options.get( 1 );
			}
			return this.createRadioBooleanValue( name, trueVal, falseVal, nullable, lineBreak );
		default:
			throw new IllegalArgumentException( "unknown type: " + type );
		}
	}
	
	/*
	 * Checkbox
	 */
	public FormValue<Integer> createCheckboxIntegerValue(
			HtmlCheckboxElement.InputType type, Locale locale, String name,
			Integer min, Integer max ) {

		FormValueImpl<Integer> result = new FormValueImpl<Integer>();
		
		HtmlCheckboxElement element = new HtmlCheckboxElement( name );
		element.setDecorator( decorator );
		if( getStandardClass() != null ) element.addClassAttribute( getStandardClass() );
		result.setHtmlInput( element );
		
		IntegerConverter converter = IntegerConverter.instance( locale );
		result.setConverter( converter );
		
		if( min != null || max != null ) {
			result.addValidator( new MinMaxValidator( min, max ) );
		}
		return result;
	}

	public FormValue<Integer> createCheckboxIntegerValue( String name,
			Locale locale, Integer min, Integer max ) {
		return createCheckboxIntegerValue(
				HtmlCheckboxElement.InputType.CHECKBOX, locale, name, min, max );
	}

	public FormValue<Integer> createCheckboxIntegerValue( String name,
			Locale locale ) {
		return createCheckboxIntegerValue( name, locale, Integer.MIN_VALUE,
				Integer.MAX_VALUE );
	}

	public FormValue<Boolean> createCheckboxBooleanValue( 
			HtmlCheckboxElement.InputType type, String name
			) {

		FormValueImpl<Boolean> result = new FormValueImpl<Boolean>();
		
		HtmlCheckboxElement element = new HtmlCheckboxElement( name );
		element.setDecorator( decorator );
		if( getStandardClass() != null ) element.addClassAttribute( getStandardClass() );
		result.setHtmlInput( element );
		
		BooleanCheckboxConverter converter = new BooleanCheckboxConverter();
		result.setConverter( converter );

		return result;
	}
	public FormValue<Boolean> createCheckboxBooleanValue( String name ) {
		
		return createCheckboxBooleanValue( HtmlCheckboxElement.InputType.CHECKBOX, name );
	}
	
	public <T> FormValue<T> createInputTextValue(
			Converter<T> converter,
			HtmlInputElement.InputType type,
			String name, int length,
			boolean nullable ){
				FormValueImpl<T> result = new FormValueImpl<T>();

		// HtmlInputElement
		HtmlInputElement element = new HtmlInputElement( type, name );
		element.setDecorator( decorator );
		if( getStandardClass() != null ) element.addClassAttribute( getStandardClass() );
		result.setHtmlInput( element );
		
		// Converter
		result.setConverter( converter );
		
		return result;
	}

	/*
	 * Input
	 */
	public FormValue<String> createInputTextValue(
			HtmlInputElement.InputType type, String name, int length,
			boolean nullable, AjaxValidate validate ) {

		FormValueImpl<String> result = new FormValueImpl<String>();

		// HtmlInputElement
		HtmlInputElement element = new HtmlInputElement( type, name );
		element.setDecorator( decorator );
		if( getStandardClass() != null ) element.addClassAttribute( getStandardClass() );
		result.setHtmlInput( element );
		
		// Converter
		StringConverter converter = new StringConverter();
		result.setConverter( converter );
		
		// Validator
		if( length >= 0 ) {
			result.addValidator( new LengthValidator( length ) );
			validate.add( "length[0," + length + "]" );
		}
		if( !nullable ) {
			result.addValidator( new NotNullValidator() );
			validate.add( "required" );
		}
		element.addClassAttribute( validate.text() );

		return result;
	}

	public FormValue<String> createInputTextValue(
			HtmlInputElement.InputType type, String name, int length,
			boolean nullable ) {

		return createInputTextValue( type, name, length, nullable,
				new AjaxValidate() );
	}

	public FormValue<String> createInputTextValue( String name, int length,
			boolean nullable, AjaxValidate ajaxValidate ) {

		return createInputTextValue( HtmlInputElement.InputType.TEXT, name,
				length, nullable, ajaxValidate );
	}

	public FormValue<String> createInputTextValue( String name, int length,
			boolean nullable ) {

		return createInputTextValue( HtmlInputElement.InputType.TEXT, name,
				length, nullable );
	}

	public FormValue<String> createInputPasswordValue( String name, int length,
			boolean nullable ) {

		return createInputTextValue( HtmlInputElement.InputType.PASSWORD, name,
				length, nullable );
	}

	public FormValue<String> createInputPostalcodeValue( String name, boolean nullable, final String countryCode ) {

		FormValue<String> result = createInputTextValue( name, 12, nullable );
		result.addValidator( new PostalcodeValidator_Dynamic(
				new CountryProvider(){
					@Override public String getCode(){
						return countryCode;
					}
				}
		) );
		return result;
	}
	public FormValue<String> createInputPostalcodeValue( String name, boolean nullable, final FormValue<String> countryProvider ) {

		FormValue<String> result = createInputTextValue( name, 12, nullable );
		result.addValidator( new PostalcodeValidator_Dynamic(
				new CountryProvider(){
					@Override public String getCode(){
						return countryProvider.getPlainValue();
					}
				}
		) );
		return result;
	}

	public FormValue<String> createInputEMailValue( String name, int length,
			boolean nullable ) {

		AjaxValidate ajaxValidate = new AjaxValidate();
		ajaxValidate.add( "email" );
		
		FormValue<String> result = createInputTextValue( name, length, nullable, ajaxValidate );
		result.addValidator( new EMailValidator() );
		return result;
	}
	public FormValue<String> createInputRepeatValue( String name, int length,
			boolean nullable, FormValue<?> other ) {
		
		FormValue<String> result = createInputTextValue( name, length, nullable );
		result.addValidator( new EqualsValidator( other ) );
		return result;
	}

	public FormValue<String> createInputUrlValue( String name, int length,
			boolean nullable ) {

		AjaxValidate ajaxValidate = new AjaxValidate();
		ajaxValidate.add( "url" );
		
		FormValue<String> result = createInputTextValue( name, length, nullable, ajaxValidate );
		result.addValidator( new UrlValidator() );
		return result;
	}

	public FormValue<String> createInputPhoneValue( String name, int length,
			boolean nullable ) {

		AjaxValidate ajaxValidate = new AjaxValidate();
		ajaxValidate.add( "phone" );

		FormValue<String> result = createInputTextValue( name, length,
				nullable, ajaxValidate );
		
		result.addValidator( new PhoneValidator() );
		
		return result;
	}

	public FormValue<String> createInputCountryValue( String name,
			boolean nullable ) {

		FormValue<String> result = createInputTextValue( name, 2, nullable );
		result.addValidator( new CountryValidator() );
		return result;
	}

	public FormValue<String> createInputLanguageValue( String name,
			boolean nullable ) {

		FormValue<String> result = createInputTextValue( name, 2, nullable );
		result.addValidator( new LanguageValidator() );
		return result;
	}
	
	public FormValue<Date> createInputDateValue(
			HtmlInputElement.InputType type, Locale locale, String name,
			DateConverter converter, boolean nullable,
			AjaxValidate ajaxValidate, String validateString ){
		
		FormValueImpl<Date> result = new FormValueImpl<Date>();
		
		HtmlInputElement element = new HtmlInputElement( type, name );
		element.setDecorator( decorator );
		if( getStandardClass() != null ) element.addClassAttribute( getStandardClass() );
		result.setHtmlInput( element );
		
		result.setConverter( converter );
		
		if( validateString != null )
			ajaxValidate.add( validateString );
		
		if( !nullable ) {
			result.addValidator( new NotNullValidator() );
			ajaxValidate.add( "required" );
		}
		
		if( defaultAjaxValidate != null )
			ajaxValidate.add( defaultAjaxValidate );
		
		element.addClassAttribute( ajaxValidate.text() );
		
		return result;
	}
	
	public FormValue<Date> createInputDateValue(
			HtmlInputElement.InputType type, Locale locale, String name,
			boolean nullable,
			AjaxValidate ajaxValidate ){
		
		return createInputDateValue( type, locale, name,
				DateConverter.YMDForLocale( locale ), nullable,
				ajaxValidate, "%check_date"
		);
	}
	
	public FormValue<Date> createInputDateValue(
			String name, Locale locale,
			boolean nullable
			){
		
		return createInputDateValue(
				DATE_INPUT_TYPE(),
				locale, name, nullable, new AjaxValidate() );
	}
		
	public FormValue<Date> createInputDateValue(
			String name, Locale locale,
			DateConverter converter, boolean nullable, String validateString
			){
		
		return createInputDateValue(
				DATE_INPUT_TYPE(),
				locale, name,
				converter, nullable,
				new AjaxValidate(), validateString );
	}
		
	public FormValue<Date> createInputDateValue(
			String name,
			boolean nullable
			){
		
		return createInputDateValue(
				DATE_INPUT_TYPE(),
				defaultLocale, name, nullable, new AjaxValidate() );
	}
		

	public FormValue<BigDecimal> createInputBigDecimalPriceValue(
			HtmlInputElement.InputType type, Locale locale, String name,
			BigDecimal min, BigDecimal max, boolean nullable, 
			AjaxValidate ajaxValidate ){
		
		return createInputBigDecimalPriceValue( type, locale, name,
				min, max, nullable, false, ajaxValidate );
		
	}
	
	public FormValue<BigDecimal> createInputBigDecimalPriceValue(
			HtmlInputElement.InputType type, Locale locale, String name,
			BigDecimal min, BigDecimal max, boolean nullable, boolean readonly,
			AjaxValidate ajaxValidate ){
		
		FormValueImpl<BigDecimal> result = new FormValueImpl<BigDecimal>();
		
		HtmlInputElement element = new HtmlInputElement( type, name );
		
		if( type == HtmlInputElement.InputType.NUMBER )
			element.addAttribute( "step", "any" ); // Make floating point possible for Chrome
		
		element.setReadonly( readonly );
		element.setDecorator( decorator );
		if( getStandardClass() != null ) element.addClassAttribute( getStandardClass() );
		result.setHtmlInput( element );
		
		BigDecimalConverter converter = BigDecimalConverter.forLocale( locale );
		result.setConverter( converter );
		
		ajaxValidate.add( "%check_price_de" );
		
		if( defaultAjaxValidate != null )
			ajaxValidate.add( defaultAjaxValidate );
		
		if( min != null || max != null ) {
			result.addValidator( new MinMaxValidator( min, max ) );
			// No ajax available
		}
		
		if( !nullable ) {
			result.addValidator( new NotNullValidator() );
			ajaxValidate.add( "required" );
		}
		
		element.addClassAttribute( ajaxValidate.text() );
		
		return result;
	}
	public FormValue<BigDecimal> createInputBigDecimalPriceValue(
			String name, Locale locale, boolean nullable ) {
			
		return createInputBigDecimalPriceValue( NUMBER_INPUT_TYPE(),
				locale, name, null, null, nullable, new AjaxValidate() );
				
	}
	public FormValue<BigDecimal> createInputBigDecimalPriceValue(
			String name, boolean nullable ) {
			
		return createInputBigDecimalPriceValue( NUMBER_INPUT_TYPE(),
				defaultLocale, name, null, null, nullable, new AjaxValidate() );
				
	}

	public FormValue<BigDecimal> createInputBigDecimalPriceValue(
			String name, boolean nullable, boolean readonly ) {
			
		return createInputBigDecimalPriceValue( NUMBER_INPUT_TYPE(),
				defaultLocale, name, null, null, nullable, readonly, new AjaxValidate() );
				
	}

	public FormValue<Integer> createInputIntegerValue(
			HtmlInputElement.InputType type, Locale locale, String name,
			Integer min, Integer max, boolean useThousandsSeparator, boolean nullable,
			AjaxValidate ajaxValidate ) {
		
		FormValueImpl<Integer> result = new FormValueImpl<Integer>();
		HtmlInputElement element = new HtmlInputElement( type, name );
		element.setDecorator( decorator );
		if( getStandardClass() != null ) element.addClassAttribute( getStandardClass() );
		IntegerConverter converter = IntegerConverter.instance( locale, useThousandsSeparator );
		result.setHtmlInput( element );
		result.setConverter( converter );
		
		ajaxValidate.add( "%check_number_de" );

		if( min != null || max != null ) {
			result.addValidator( new MinMaxValidator( min, max ) );
			ajaxValidate.add( "digit[" + min + "," + max + "]" );
		}

		if( !nullable ) {
			result.addValidator( new NotNullValidator() );
			ajaxValidate.add( "required" );
		}

		if( defaultAjaxValidate != null )
			ajaxValidate.add( defaultAjaxValidate );
		
		element.addClassAttribute( ajaxValidate.text() );

		return result;
	}

	public FormValue<Integer> createInputIntegerValue(
			HtmlInputElement.InputType type, Locale locale, String name,
			Integer min, Integer max, boolean useThousandsSeparator, boolean nullable ) {

		return createInputIntegerValue( type, locale, name, min, max, useThousandsSeparator, nullable,
				new AjaxValidate() );
	}

	public FormValue<Integer> createInputIntegerValue( String name,
			Locale locale, Integer min, Integer max, boolean useThousandsSeparator, boolean nullable ) {
		return createInputIntegerValue( NUMBER_INPUT_TYPE(),
				locale, name, min, max, useThousandsSeparator, nullable );
	}
	public FormValue<Integer> createInputIntegerValue( String name,
			Locale locale, Integer min, Integer max, boolean nullable ) {
		return createInputIntegerValue( NUMBER_INPUT_TYPE(),
				locale, name, min, max, true, nullable );
	}

	public FormValue<Integer> createInputIntegerValue( String name,
			Locale locale, boolean useThousandsSeparator, boolean nullable ) {
		return createInputIntegerValue( name, locale, null,
				null, useThousandsSeparator, nullable );
	}
	public FormValue<Integer> createInputIntegerValue( String name,
			Locale locale, boolean nullable ) {
		return createInputIntegerValue( name, locale, null,
				null, true, nullable );
	}
	public FormValue<Integer> createInputIntegerValue( String name, boolean nullable ){
		
		return createInputIntegerValue( name, defaultLocale, nullable );
	}
	public FormValue<Integer> createInputIntegerValue( String name, boolean useThousandsSeparator, boolean nullable ){
		
		return createInputIntegerValue( name, defaultLocale, useThousandsSeparator, nullable );
	}

	public FormValue<String> createInputHiddenValue( String name ) {

		FormValueImpl<String> result = new FormValueImpl<String>();
		HtmlInputElement element = new HtmlInputElement(
				HtmlInputElement.InputType.HIDDEN, name );
		StringConverter converter = new StringConverter();
		result.setHtmlInput( element );
		result.setConverter( converter );

		return result;
	}

	public FormValue<Long> createInputHiddenLongValue( String name, Locale locale ) {

		FormValueImpl<Long> result = new FormValueImpl<Long>();
		HtmlInputElement element = new HtmlInputElement(
				HtmlInputElement.InputType.HIDDEN, name );
		result.setHtmlInput( element );
		result.setConverter( new LongConverter( locale ) );

		return result;
	}
	public FormValue<Long> createInputHiddenLongValue( String name ) {

		return createInputHiddenLongValue( name, null );
	}

	public FormValue<Boolean> createRadioBooleanValue( String name,
			String trueValue, String falseValue, boolean nullable, boolean lineBreak ){
		
		LinkedList<Option> options = new LinkedList<Option>();
		Option trueOption = new OptionImpl( "true", trueValue );
		Option falseOption = new OptionImpl( "false", falseValue );
		options.addLast( trueOption );
		options.addLast( falseOption );
		
		FormValue<Boolean> result = new FormValueImpl<Boolean>();
		
		AjaxValidate ajaxValidate = new AjaxValidate();
		
		HtmlRadioElement element = new HtmlRadioElement( name, options );
		element.setDecorator( decorator );
		element.setLineBreak( lineBreak );
		if( getStandardClass() != null ) element.addClassAttribute( getStandardClass() );
		result.setHtmlInput( element );
		
		BooleanConverter converter = new BooleanConverter();
		result.setConverter( converter );
		
		if( !nullable ) {
			result.addValidator( new NotNullValidator() );
			ajaxValidate.add( "radio" );
		}
		//ajaxValidate.add( "target:" + "input_" + name + "_true" );
		
		if( defaultAjaxValidate != null )
			ajaxValidate.add( defaultAjaxValidate );
		
		
		element.addClassAttribute( ajaxValidate.text() );
		
		return result;
	}
	
	public FormValue<String> createRadioValue( String name,
			List<Option> options, boolean nullable ) {
		
		FormValue<String> result = new FormValueImpl<String>();
		
		HtmlRadioElement element = new HtmlRadioElement( name, options );
		element.setDecorator( decorator );
		if( getStandardClass() != null ) element.addClassAttribute( getStandardClass() );
		result.setHtmlInput( element );
		
		// Converter
		StringConverter converter = new StringConverter();
		result.setConverter( converter );
				// value must be one defined.
		Set<String> keys = new HashSet<String>();
		for( Option option : options ) {
			keys.add( option.getValue() );
		}
		result.addValidator( new InCollectionValidator<String>( keys ) );

		if( !nullable ) {
			result.addValidator( new NotNullValidator() );
		}
		return result;
		
	}
	
	/*
	 * Select
	 */
	public FormValue<String> createSelectValue( String name,
			List<Option> options, boolean nullable ) {

		FormValue<String> result = new FormValueImpl<String>();

		// Element
		HtmlSelectElement element = new HtmlSelectElement( name, options );
		element.setDecorator( decorator );
		if( getStandardClass() != null ) element.addClassAttribute( getStandardClass() );
		result.setHtmlInput( element );

		// Converter
		StringConverter converter = new StringConverter();
		result.setConverter( converter );

		// value must be one defined.
		Set<String> keys = new HashSet<String>();
		for( Option option : options ) {
			keys.add( option.getValue() );
		}
		result.addValidator( new InCollectionValidator<String>( keys ) );

		if( !nullable ) {
			result.addValidator( new NotNullValidator() );
		}
		return result;
	}

	public FormValue<String> createSelectValue( String name,
			Map<String, String> options, boolean nullable ) {

		LinkedList<Option> optionsList = new LinkedList<Option>();

		for( Map.Entry<String,String> entry : options.entrySet() ){

			optionsList.add( new OptionImpl( entry.getKey(), entry.getValue() ) );
		}

		Collections.sort( optionsList, new OptionComparator() );

		return createSelectValue( name, optionsList, nullable );
	}

	/*
	 * TextArea
	 */
	public FormValue<String> createTextareaTextValue( String name,
			int length, int cols, int rows, boolean nullable ) {
		return createTextareaTextValue( name, length, 60, 5, nullable, new AjaxValidate() );
	}
	
	public FormValue<String> createTextareaTextValue( String name, int length,
			int cols, int rows, boolean nullable, AjaxValidate ajaxValidate ) {

		FormValueImpl<String> result = new FormValueImpl<String>();

		HtmlTextAreaElement element = new HtmlTextAreaElement( name );
		element.setDecorator( decorator );
		element.setCols( cols );
		element.setRows( rows );

		result.setHtmlInput( element );
		
		StringConverter converter = new StringConverter();
		result.setConverter( converter );

		if( length >= 0 ){
			result.addValidator( new LengthValidator( length ) );
			ajaxValidate.add( "length[0," + length + "]" );
		}
		
		if( !nullable ) {
			result.addValidator( new NotNullValidator() );
			ajaxValidate.add( "required" );
		}
		
		if( defaultAjaxValidate != null )
			ajaxValidate.add( defaultAjaxValidate );
		
		element.addClassAttribute( ajaxValidate.text() );

		return result;
	}
	
	protected OptionList convert( SelectableOption [] options ){
		
		OptionList list = new OptionList();
		for( SelectableOption option : options ){
			list.add( new OptionImpl( option.key(), option.title() ) );
		}
		return list;
		
	}

	protected static class AjaxValidate {

		LinkedList<String> values = new LinkedList<String>();

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

			result.append( "validate[" );

			boolean first = true;
			for( String val : values ) {

				if( first )
					first = false;
				else
					result.append( "," );

				result.append( '\'' ).append( val ).append( '\'' );
			}
			result.append( "]" );

			return result.toString();
		}
	}
	
	public interface SelectableOption {
		public String key();
		public String title();
	}
	
	
}
