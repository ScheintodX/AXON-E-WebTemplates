package de.axone.webtemplate.form;

import static org.testng.Assert.*;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.testng.annotations.Test;

import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.converter.impl.CharacterConverter;
import de.axone.webtemplate.converter.impl.DoubleConverter;
import de.axone.webtemplate.converter.impl.FloatConverter;
import de.axone.webtemplate.converter.impl.IntegerConverter;
import de.axone.webtemplate.converter.impl.LongConverter;
import de.axone.webtemplate.converter.impl.ShortConverter;
import de.axone.webtemplate.element.FormValueFactoryHeadshop;
import de.axone.webtemplate.elements.impl.HtmlInputElement;
import de.axone.webtemplate.form.Form.On;
import de.axone.webtemplate.form.FormParser.FormField;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Test( groups="webtemplate.webform" )
public class FormParserTest {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	
	// ==== PARSER FIELD NAMES ====================

	public static void testParserFieldNames() throws Exception{
		
		TestClassForFieldNames testClassForNames = new TestClassForFieldNames();
		Class<?> cls = testClassForNames.getClass();
		
		List<FormField> fields = FormParser.fields( testClassForNames.getClass(), On.EDIT );
		assertEquals( fields.size(), cls.getFields().length );
		
		for( FormField field : fields ){
			assertEquals( field.formName(), field.field().get( testClassForNames ) );
			assertEquals( field.pojoName(), field.field().getName() );
		}
	}
	
	@Form
	@SuppressWarnings( "unused" )
	@SuppressFBWarnings( { "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD", "URF_UNREAD_FIELD" } )
	private static class TestClassForFieldNames {
		
		public String aaaaaa = "aaaaaa";
		public String Bbbbbb = "bbbbbb";
		public String cccccC = "ccccc_c";
		public String DdDdDd = "dd_dd_dd";
		public String eEeEeE = "e_ee_ee_e";
		public String fffFFF = "fff_fff";
		public String GGGggg = "gg_gggg";
		public String HHhhHH = "h_hhh_hh";
		public String iiIIii = "ii_i_iii";
		public String jj12jj = "jj_12_jj";
		public String KK12KK = "kk_12_kk";
	}
	
	// ==== PARSER METHOD NAMES ====================

	public static void testParserMethodNames() throws Exception{
		
		TestClassForMethodNames testClassForNames = new TestClassForMethodNames();
		
		List<FormField> fields = FormParser.fields( testClassForNames.getClass(), On.EDIT );
		assertEquals( fields.size(), 11 );
		
		for( FormField field : fields ){
			//E.rr( field );
			assertEquals( field.formName(), field.getter().invoke( testClassForNames ) );
		}
	}
	
	@Form
	@SuppressWarnings( "unused" )
	private static class TestClassForMethodNames {
		
		private String aaaaaa = "aaaaaa";
		private String Bbbbbb = "bbbbbb";
		private String cccccC = "ccccc_c";
		private String DdDdDd = "dd_dd_dd";
		private String eEeEeE = "e_ee_ee_e";
		private String fffFFF = "fff_fff";
		private String GGGggg = "g_g_gggg"; // Note: getGGGggg -> gGGggg field
		private String HHhhHH = "h_hhh_hh";
		private String iiIIii = "ii_i_iii";
		private String jj12jj = "jj_12_jj";
		private String KK12KK = "k_k_12_kk"; // Note: getKK12KK -> kK12KK field
		
		public String getAaaaaa(){ return aaaaaa; }
		public String getBbbbbb(){ return Bbbbbb; }
		public String getCccccC(){ return cccccC; }
		public String getDdDdDd(){ return DdDdDd; }
		public String getEEeEeE(){ return eEeEeE; }
		public String getFffFFF(){ return fffFFF; }
		public String getGGGggg(){ return GGGggg; }
		public String getHHhhHH(){ return HHhhHH; }
		public String getIiIIii(){ return iiIIii; }
		public String getJj12jj(){ return jj12jj; }
		public String getKK12KK(){ return KK12KK; }
		
		public void setAaaaaa( String v ){ aaaaaa = v; }
		public void setBbbbbb( String v ){ Bbbbbb= v; }
		public void setCccccC( String v ){ cccccC= v; }
		public void setDdDdDd( String v ){ DdDdDd= v; }
		public void setEEeEeE( String v ){ eEeEeE= v; }
		public void setFffFFF( String v ){ fffFFF= v; }
		public void setGGGggg( String v ){ GGGggg= v; }
		public void setHHhhHH( String v ){ HHhhHH= v; }
		public void setIiIIii( String v ){ iiIIii= v; }
		public void setJj12jj( String v ){ jj12jj= v; }
		public void setKK12KK( String v ){ KK12KK= v; }
	}
	
	// ==== FIELD ACCESS OUTSIDE ====================
	
	public static void testFieldAccessOutsideAnnotated() throws Exception{
		
		TestClassFieldsAnnotatedOutside testClassFieldsAnnotatedOutside = new TestClassFieldsAnnotatedOutside ();
		
		List<FormField> fields = FormParser.fields( testClassFieldsAnnotatedOutside.getClass(), On.EDIT );
		assertEquals( fields.size(), 1 );
		
		for( FormField field : fields ){
			assertEquals( field.formName(), field.field().get( testClassFieldsAnnotatedOutside ) );
		}
	}
	
	@Form
	@SuppressWarnings( "unused" )
	@SuppressFBWarnings( { "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD", "URF_UNREAD_FIELD" } )
	private static class TestClassFieldsAnnotatedOutside {
		
		// Only public will be included
		public String myPublicStringField = "my_public_string_field" ;
		
		// Wont be included
		String myPackageStringField = "my_package_string_field" ;
		
		// Wont be included
		private String myPrivateStringField = "my_private_string_field" ;
		
		// Wont be included
		protected String myProtectedStringField = "my_protected_string_field" ;
		
		// Wont be included
		@Form( enabled = false )
		public String myExcludedStringField = "my_excluded_string_field" ;
		
		// Wont be included
		@Form( on = On.NEVER )
		public String myNeverStringField = "my_never_string_field" ;
	}
	
	// ==== FIELD ACCESS INSIDE ====================
	
	public static void testFieldAccessInsideAnnotated() throws Exception{
		
		TestClassFieldsAnnotatedInside testClassFieldsAnnotatedInside = new TestClassFieldsAnnotatedInside ();
		
		List<FormField> fields = FormParser.fields( testClassFieldsAnnotatedInside.getClass(), On.EDIT );
		assertEquals( fields.size(), 1 );
		
		for( FormField field : fields ){
			assertEquals( field.formName(), field.field().get( testClassFieldsAnnotatedInside ) );
		}
	}
	
	@SuppressWarnings( "unused" )
	@SuppressFBWarnings( { "URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD", "URF_UNREAD_FIELD" } )
	private static class TestClassFieldsAnnotatedInside {
		
		// Only public will be included
		@Form
		public String myPublicStringField = "my_public_string_field" ;
		
		// Wont be included
		@Form
		String myPackageStringField = "my_package_string_field" ;
		
		// Wont be included
		@Form
		private String myPrivateStringField = "my_private_string_field" ;
		
		// Wont be included
		@Form
		protected String myProtectedStringField = "my_protected_string_field" ;
		
		// Wont be included
		@Form( enabled = false )
		public String myExcludedStringField = "my_excluded_string_field" ;
		
		// Wont be included
		public String myImplizitlyExcludedStringField = "my_implizitly_excluded_string_field" ;
		
		// Wont be included
		@Form( on = On.NEVER )
		public String myNeverStringField = "my_never_string_field" ;
	}
	
	// ==== FIELD ACCESS TYPES ====================
	
	public void testFieldAccessTypes() throws Exception{
		
		TestClassFieldTypes pojo = new TestClassFieldTypes ();
		
		TestWebForm form = new TestWebForm();
		
		FormParser<TestClassFieldTypes> parser
				= new FormParser<>( TestClassFieldTypes.class, On.EDIT );
		
		// - Put in Form -----------------------
		parser.putPojoInForm( pojo, form );
		
		NumberFormat nf = NumberFormat.getNumberInstance( Locale.GERMANY );
		assertTrue( nf instanceof DecimalFormat );
		DecimalFormat f = (DecimalFormat) nf;
		f.setParseBigDecimal( true );
		DateFormat df = (DateFormat) DATE_FORMAT.clone();
		
		assertEquals( Boolean.parseBoolean( form.getPlainValue( TestWebForm.MY_PUBLIC_BOOLEAN_FIELD ) ), pojo.myPublicBooleanField );
		assertEquals( (form.getPlainValue( TestWebForm.MY_PUBLIC_CHAR_FIELD )).charAt(0), pojo.myPublicCharField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_SHORT_FIELD ) ).shortValue(), pojo.myPublicShortField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_INTEGER_FIELD ) ).intValue(), pojo.myPublicIntegerField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_LONG_FIELD ) ).longValue(), pojo.myPublicLongField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_FLOAT_FIELD ) ).floatValue(), pojo.myPublicFloatField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_DOUBLE_FIELD ) ).doubleValue(), pojo.myPublicDoubleField );
		
		assertEquals( Boolean.valueOf( form.getPlainValue( TestWebForm.MY_PUBLIC_BOOLEAN_OBJECT_FIELD ) ), pojo.myPublicBooleanObjectField );
		assertEquals( (Character)(form.getPlainValue( TestWebForm.MY_PUBLIC_CHAR_OBJECT_FIELD )).charAt(0), pojo.myPublicCharObjectField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_SHORT_OBJECT_FIELD ) ).shortValue(), (short)pojo.myPublicShortObjectField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_INTEGER_OBJECT_FIELD ) ).intValue(), (int)pojo.myPublicIntegerObjectField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_LONG_OBJECT_FIELD ) ).longValue(), (long)pojo.myPublicLongObjectField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_FLOAT_OBJECT_FIELD ) ).floatValue(), pojo.myPublicFloatObjectField, 0.00001f );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_DOUBLE_OBJECT_FIELD ) ).doubleValue(), pojo.myPublicDoubleObjectField, 0.00001d );
		
		assertEquals( form.getPlainValue( TestWebForm.MY_PUBLIC_STRING_FIELD ), pojo.myPublicStringField );
		assertEquals( df.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_DATE_FIELD ) ), pojo.myPublicDateField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_BIG_DECIMAL_FIELD ) ), pojo.myPublicBigDecimalField );
		
		// - Set new values value -----------
		form.getHtmlInput( TestWebForm.MY_PUBLIC_BOOLEAN_FIELD ).setValue( "false" );
		form.getHtmlInput( TestWebForm.MY_PUBLIC_CHAR_FIELD ).setValue( "b" );
		form.getHtmlInput( TestWebForm.MY_PUBLIC_SHORT_FIELD ).setValue( "" + Short.MIN_VALUE );
		form.getHtmlInput( TestWebForm.MY_PUBLIC_INTEGER_FIELD ).setValue( "" + Integer.MIN_VALUE );
		form.getHtmlInput( TestWebForm.MY_PUBLIC_LONG_FIELD ).setValue( "" + Long.MIN_VALUE );
		form.getHtmlInput( TestWebForm.MY_PUBLIC_FLOAT_FIELD ).setValue( "" + Float.MIN_VALUE );
		form.getHtmlInput( TestWebForm.MY_PUBLIC_DOUBLE_FIELD ).setValue( "" + Double.MIN_VALUE );
		
		form.getHtmlInput( TestWebForm.MY_PUBLIC_BOOLEAN_OBJECT_FIELD ).setValue( "false" );
		form.getHtmlInput( TestWebForm.MY_PUBLIC_CHAR_OBJECT_FIELD ).setValue( "B" );
		form.getHtmlInput( TestWebForm.MY_PUBLIC_SHORT_OBJECT_FIELD ).setValue( "" + Short.MAX_VALUE );
		form.getHtmlInput( TestWebForm.MY_PUBLIC_INTEGER_OBJECT_FIELD ).setValue( "" + Integer.MAX_VALUE );
		form.getHtmlInput( TestWebForm.MY_PUBLIC_LONG_OBJECT_FIELD ).setValue( "" + Long.MAX_VALUE );
		form.getHtmlInput( TestWebForm.MY_PUBLIC_FLOAT_OBJECT_FIELD ).setValue( "" + Float.MAX_VALUE );
		form.getHtmlInput( TestWebForm.MY_PUBLIC_DOUBLE_OBJECT_FIELD ).setValue( "" + Double.MAX_VALUE );
		
		form.getHtmlInput( TestWebForm.MY_PUBLIC_STRING_FIELD ).setValue( "another value" );
		form.getHtmlInput( TestWebForm.MY_PUBLIC_BIG_DECIMAL_FIELD ).setValue( "22.25" );
		
		// - Put in pojo -------------
		parser.putFormInPojo( pojo, form );
		
		assertEquals( Boolean.parseBoolean( form.getPlainValue( TestWebForm.MY_PUBLIC_BOOLEAN_FIELD ) ), pojo.myPublicBooleanField );
		assertEquals( (form.getPlainValue( TestWebForm.MY_PUBLIC_CHAR_FIELD )).charAt(0), pojo.myPublicCharField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_SHORT_FIELD ) ).shortValue(), pojo.myPublicShortField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_INTEGER_FIELD ) ).intValue(), pojo.myPublicIntegerField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_LONG_FIELD ) ).longValue(), pojo.myPublicLongField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_FLOAT_FIELD ) ).floatValue(), pojo.myPublicFloatField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_DOUBLE_FIELD ) ).doubleValue(), pojo.myPublicDoubleField );
		
		assertEquals( Boolean.valueOf( form.getPlainValue( TestWebForm.MY_PUBLIC_BOOLEAN_OBJECT_FIELD ) ), pojo.myPublicBooleanObjectField );
		assertEquals( (Character)(form.getPlainValue( TestWebForm.MY_PUBLIC_CHAR_OBJECT_FIELD )).charAt(0), pojo.myPublicCharObjectField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_SHORT_OBJECT_FIELD ) ).shortValue(), (short)pojo.myPublicShortObjectField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_INTEGER_OBJECT_FIELD ) ).intValue(), (int)pojo.myPublicIntegerObjectField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_LONG_OBJECT_FIELD ) ).longValue(), (long)pojo.myPublicLongObjectField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_FLOAT_OBJECT_FIELD ) ).floatValue(), pojo.myPublicFloatObjectField, 0.001f );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_DOUBLE_OBJECT_FIELD ) ).doubleValue(), pojo.myPublicDoubleObjectField, 0.001d );
		
		assertEquals( form.getPlainValue( TestWebForm.MY_PUBLIC_STRING_FIELD ), pojo.myPublicStringField );
		assertEquals( df.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_DATE_FIELD ) ), pojo.myPublicDateField );
		assertEquals( nf.parse( form.getPlainValue( TestWebForm.MY_PUBLIC_BIG_DECIMAL_FIELD ) ), pojo.myPublicBigDecimalField );
		
	}
	
	@Form
	@SuppressWarnings( "unused" )
	private static class TestClassFieldTypes {
		
		private boolean myPublicBooleanField=true;
		private char myPublicCharField = 'a';
		private short myPublicShortField = (short)12345;
		private int myPublicIntegerField = 12345678;
		private long myPublicLongField = 1234567890123L;
		private float myPublicFloatField = 123.45f;
		private double myPublicDoubleField = 123.456;
		
		private Boolean myPublicBooleanObjectField = true;
		private Character myPublicCharObjectField = 'A';
		private Short myPublicShortObjectField = (short)54321;
		private Integer myPublicIntegerObjectField = 87654321;
		private Long myPublicLongObjectField = 3210987654321L;
		private Float myPublicFloatObjectField = 54.321f;
		private Double myPublicDoubleObjectField = (double)654.321;
		
		private String myPublicStringField = "my_public_string_field";
		private Date myPublicDateField;
		private BigDecimal myPublicBigDecimalField = new BigDecimal( "19.97" );
		
		public TestClassFieldTypes() throws Exception{
			// Throws exceptions so we need constructor
			DateFormat df = (DateFormat) DATE_FORMAT.clone();
			myPublicDateField = df.parse( "05.03.1975" );
		}
		
		@Form
		public boolean isMyPublicBooleanField() {
			return myPublicBooleanField;
		}
		public void setMyPublicBooleanField( boolean myPublicBooleanField ) {
			this.myPublicBooleanField = myPublicBooleanField;
		}
		public char getMyPublicCharField() {
			return myPublicCharField;
		}
		public void setMyPublicCharField( char myPublicCharField ) {
			this.myPublicCharField = myPublicCharField;
		}
		public short getMyPublicShortField() {
			return myPublicShortField;
		}
		public void setMyPublicShortField( short myPublicShortField ) {
			this.myPublicShortField = myPublicShortField;
		}
		public int getMyPublicIntegerField() {
			return myPublicIntegerField;
		}
		public void setMyPublicIntegerField( int myPublicIntegerField ) {
			this.myPublicIntegerField = myPublicIntegerField;
		}
		public long getMyPublicLongField() {
			return myPublicLongField;
		}
		public void setMyPublicLongField( long myPublicLongField ) {
			this.myPublicLongField = myPublicLongField;
		}
		public float getMyPublicFloatField() {
			return myPublicFloatField;
		}
		public void setMyPublicFloatField( float myPublicFloatField ) {
			this.myPublicFloatField = myPublicFloatField;
		}
		public double getMyPublicDoubleField() {
			return myPublicDoubleField;
		}
		public void setMyPublicDoubleField( double myPublicDoubleField ) {
			this.myPublicDoubleField = myPublicDoubleField;
		}
		
		
		public Boolean getMyPublicBooleanObjectField() {
			return myPublicBooleanObjectField;
		}
		public void setMyPublicBooleanObjectField( Boolean myPublicBooleanObjectField ) {
			this.myPublicBooleanObjectField = myPublicBooleanObjectField;
		}
		public Character getMyPublicCharObjectField() {
			return myPublicCharObjectField;
		}
		public void setMyPublicCharObjectField( Character myPublicCharObjectField ) {
			this.myPublicCharObjectField = myPublicCharObjectField;
		}
		public Short getMyPublicShortObjectField() {
			return myPublicShortObjectField;
		}
		public void setMyPublicShortObjectField( Short myPublicShortObjectField ) {
			this.myPublicShortObjectField = myPublicShortObjectField;
		}
		public Integer getMyPublicIntegerObjectField() {
			return myPublicIntegerObjectField;
		}
		public void setMyPublicIntegerObjectField( Integer myPublicIntegerObjectField ) {
			this.myPublicIntegerObjectField = myPublicIntegerObjectField;
		}
		public Long getMyPublicLongObjectField() {
			return myPublicLongObjectField;
		}
		public void setMyPublicLongObjectField( Long myPublicLongObjectField ) {
			this.myPublicLongObjectField = myPublicLongObjectField;
		}
		public Float getMyPublicFloatObjectField() {
			return myPublicFloatObjectField;
		}
		public void setMyPublicFloatObjectField( Float myPublicFloatObjectField ) {
			this.myPublicFloatObjectField = myPublicFloatObjectField;
		}
		public Double getMyPublicDoubleObjectField() {
			return myPublicDoubleObjectField;
		}
		public void setMyPublicDoubleObjectField( Double myPublicDoubleObjectField ) {
			this.myPublicDoubleObjectField = myPublicDoubleObjectField;
		}
		
		public String getMyPublicStringField() {
			return myPublicStringField;
		}
		public void setMyPublicStringField( String myPublicStringField ) {
			this.myPublicStringField = myPublicStringField;
		}
		public Date getMyPublicDateField() {
			return myPublicDateField;
		}
		public void setMyPublicDateField( Date myPublicDateField ) {
			this.myPublicDateField = myPublicDateField;
		}
		public BigDecimal getMyPublicBigDecimalField() {
			return myPublicBigDecimalField;
		}
		public void setMyPublicBigDecimalField( BigDecimal myPublicBigDecimalField ) {
			this.myPublicBigDecimalField = myPublicBigDecimalField;
		}
		
	}
	
	private static class TestWebForm extends WebFormImpl {
		
		static final String MY_PUBLIC_BIG_DECIMAL_FIELD = "my_public_big_decimal_field";
		static final String MY_PUBLIC_DATE_FIELD = "my_public_date_field";
		static final String MY_PUBLIC_STRING_FIELD = "my_public_string_field";
		static final String MY_PUBLIC_DOUBLE_OBJECT_FIELD = "my_public_double_object_field";
		static final String MY_PUBLIC_FLOAT_OBJECT_FIELD = "my_public_float_object_field";
		static final String MY_PUBLIC_LONG_OBJECT_FIELD = "my_public_long_object_field";
		static final String MY_PUBLIC_INTEGER_OBJECT_FIELD = "my_public_integer_object_field";
		static final String MY_PUBLIC_SHORT_OBJECT_FIELD = "my_public_short_object_field";
		static final String MY_PUBLIC_CHAR_OBJECT_FIELD = "my_public_char_object_field";
		static final String MY_PUBLIC_BOOLEAN_OBJECT_FIELD = "my_public_boolean_object_field";
		static final String MY_PUBLIC_DOUBLE_FIELD = "my_public_double_field";
		static final String MY_PUBLIC_FLOAT_FIELD = "my_public_float_field";
		static final String MY_PUBLIC_LONG_FIELD = "my_public_long_field";
		static final String MY_PUBLIC_INTEGER_FIELD = "my_public_integer_field";
		static final String MY_PUBLIC_SHORT_FIELD = "my_public_short_field";
		static final String MY_PUBLIC_CHAR_FIELD = "my_public_char_field";
		static final String MY_PUBLIC_BOOLEAN_FIELD = "my_public_boolean_field";
		
		static FormValueFactoryHeadshop fvf = new FormValueFactoryHeadshop();
		
		TestWebForm() throws WebTemplateException{
			
			addBooleanValue( MY_PUBLIC_BOOLEAN_FIELD );
			addCharValue( MY_PUBLIC_CHAR_FIELD );
			addShortValue( MY_PUBLIC_SHORT_FIELD );
			addIntegerValue( MY_PUBLIC_INTEGER_FIELD );
			addLongValue( MY_PUBLIC_LONG_FIELD );
			addFloatValue( MY_PUBLIC_FLOAT_FIELD );
			addDoubleValue( MY_PUBLIC_DOUBLE_FIELD );
		
			addBooleanValue( MY_PUBLIC_BOOLEAN_OBJECT_FIELD );
			addCharValue( MY_PUBLIC_CHAR_OBJECT_FIELD );
			addShortValue( MY_PUBLIC_SHORT_OBJECT_FIELD );
			addIntegerValue( MY_PUBLIC_INTEGER_OBJECT_FIELD );
			addLongValue( MY_PUBLIC_LONG_OBJECT_FIELD );
			addFloatValue( MY_PUBLIC_FLOAT_OBJECT_FIELD );
			addDoubleValue( MY_PUBLIC_DOUBLE_OBJECT_FIELD );
	
			addStringValue( MY_PUBLIC_STRING_FIELD );
			addDateValue( MY_PUBLIC_DATE_FIELD );
			addBigDecimalValue( MY_PUBLIC_BIG_DECIMAL_FIELD );
		}
		
		void addStringValue( String name ) throws WebTemplateException {
			addFormValue( name, fvf.createInputTextValue( name, 255, false ) );
		}
		
		void addBooleanValue( String name ) throws WebTemplateException {
			addFormValue( name, fvf.createCheckboxBooleanValue( name ) );
		}
		
		void addCharValue( String name ) throws WebTemplateException {
			addFormValue( name, fvf.createInputTextValue(
					new CharacterConverter(),
					HtmlInputElement.InputType.NUMBER, name, 16, false ) );
		}
		
		void addShortValue( String name ) throws WebTemplateException {
			addFormValue( name, fvf.createInputTextValue(
					new ShortConverter( Locale.GERMANY ),
					HtmlInputElement.InputType.NUMBER, name, 16, false ) );
		}
		
		void addIntegerValue( String name ) throws WebTemplateException {
			addFormValue( name, fvf.createInputTextValue(
					new IntegerConverter( Locale.GERMANY ),
					HtmlInputElement.InputType.NUMBER, name, 16, false ) );
		}
		
		void addLongValue( String name ) throws WebTemplateException {
			addFormValue( name, fvf.createInputTextValue(
					new LongConverter( Locale.GERMANY),
					HtmlInputElement.InputType.NUMBER, name, 16, false ) );
		}
		
		void addFloatValue( String name ) throws WebTemplateException {
			addFormValue( name, fvf.createInputTextValue(
					new FloatConverter( Locale.GERMANY),
					HtmlInputElement.InputType.NUMBER, name, 16, false ) );
		}
		
		void addDoubleValue( String name ) throws WebTemplateException {
			addFormValue( name, fvf.createInputTextValue(
					new DoubleConverter( Locale.GERMANY),
					HtmlInputElement.InputType.NUMBER, name, 16, false ) );
		}
		
		void addBigDecimalValue( String name ) throws WebTemplateException {
			addFormValue( name, fvf.createInputBigDecimalPriceValue( name, false ) );
		}
		
		void addDateValue( String name ) throws WebTemplateException {
			addFormValue( name, fvf.createInputDateValue( name, false ) );
		}
		
	}
		
}
