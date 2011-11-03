package de.axone.webtemplate.form;

import static org.testng.Assert.*;

import java.util.List;
import java.util.Locale;

import org.testng.annotations.Test;

import de.axone.tools.E;
import de.axone.web.TestHttpServletRequest;
import de.axone.webtemplate.converter.Converter;
import de.axone.webtemplate.converter.impl.IntegerConverter;
import de.axone.webtemplate.converter.impl.StringConverter;
import de.axone.webtemplate.elements.impl.HtmlInputElement;
import de.axone.webtemplate.elements.impl.HtmlInputElement.InputType;
import de.axone.webtemplate.validator.Validator;
import de.axone.webtemplate.validator.impl.LengthValidator;
import de.axone.webtemplate.validator.impl.NotNullValidator;

@Test( groups="webtemplate.webform" )
public class WebFormTest {

	private static final String VALIDATOR_IS_EMPTY = "VALIDATOR_IS_EMPTY";

	public void testCompleteWebConnector() throws Exception {

		WebForm webForm = new WebFormImpl();

		FormValue<String> valueHidden1 = new FormValueImpl<String>();
		HtmlInputElement inputHidden1 = new HtmlInputElement( InputType.HIDDEN, "name1", "value1" );
		Converter<String> converterHidden1 = new StringConverter();
		valueHidden1.setHtmlInput( inputHidden1 );
		valueHidden1.setConverter( converterHidden1 );
		webForm.addFormValue( "name1", valueHidden1 );

		FormValue<String> valueText2 = new FormValueImpl<String>();
		HtmlInputElement inputText2 = new HtmlInputElement( InputType.TEXT, "name2", "vlaue2" );
		Converter<String> converterText2 = new StringConverter();
		Validator<Object> notNullValidator = new NotNullValidator();
		Validator<String> length10Validator = new LengthValidator( 10 );
		valueText2.setHtmlInput( inputText2 );
		valueText2.setConverter( converterText2 );
		valueText2.addValidator( notNullValidator );
		valueText2.addValidator( length10Validator );
		webForm.addFormValue( "name2", valueText2 );

		// Validation
		assertTrue( webForm.isValid() );
		assertNull( webForm.validate() );

		inputText2.setValue( "" );

		assertFalse( webForm.isValid() );
		List<String> result = webForm.validate();
		assertNotNull( result );
		assertEquals( result.size(), 1 );
		assertEquals( result.get( 0 ), VALIDATOR_IS_EMPTY );

		// Translation
		webForm.setTranslationProvider( new TestTextProvider() );

		result = webForm.validate();
		assertNotNull( result );
		assertEquals( result.size(), 1 );
		assertEquals( result.get( 0 ), "valisempty" );

		// HttpServletRequest
		TestHttpServletRequest request = new TestHttpServletRequest();
		request.setParameter( "name1", "newvalue1" );
		request.setParameter( "name2", "newvalue2" );

		webForm.initialize( request );

		assertEquals( webForm.getPlainValue( "name1" ), "newvalue1" );
		assertEquals( webForm.getPlainValue( "name2" ), "newvalue2" );

	}

	public void testConverters() throws Exception {

		FormValue<Integer> intValue = new FormValueImpl<Integer>();
		HtmlInputElement input = new HtmlInputElement( InputType.TEXT, "name", "1.234" );
		intValue.setHtmlInput( input );
		Validator<Object> notNullValidator = new NotNullValidator();
		intValue.addValidator( notNullValidator );
		Converter<Integer> intConverter = new IntegerConverter( Locale.GERMAN );
		intValue.setConverter( intConverter );

		Integer value = intValue.getValue();
		assertEquals( value, new Integer( 1234 ) );

	}

	private static class TestTextProvider extends AbstractTranslator {

		@Override
		public boolean has( String text ) {
			return VALIDATOR_IS_EMPTY.equals( text );
		}

		@Override
		protected String getPlainTranslation( String text ) {
			String result = null;
			
			if( VALIDATOR_IS_EMPTY.equals( text ) ) result = "valisempty";
			
			E.rr( text + "-> " + result );
			
			return result;
		}
	}

}
