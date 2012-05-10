package de.axone.webtemplate.form;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.axone.tools.CamelCase;
import de.axone.webtemplate.DataHolder;
import de.axone.webtemplate.KeyException;
import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.converter.ConverterException;
import de.axone.webtemplate.form.Form.On;

public class FormParser<T> {
	
	public static final Logger log =
			LoggerFactory.getLogger( FormParser.class );
	
	private static final HashSet<Method> COMMON_METHODS 
			= new HashSet<Method>( Arrays.asList( Object.class.getMethods() ) );
	
	//private static final Log log = Logging.getLog( FormParser.class );
	
	private T pojo;
	private List<FormField> parsed;
	
	public FormParser( T pojo, On event ) throws FormParserException {
		this.pojo = pojo;
		this.parsed = fields( pojo.getClass(), event );
	}
	
	private static boolean isFormable( Form formable, On event ) throws FormParserException{
		
			boolean isFormable = formable.use();
			List<On> on = Arrays.asList( formable.on() );
			
			if( !isFormable ) return false;
			
			if( on.size() == 0 ) return false;
			
			if( on.contains( On.NEVER ) ){
				if( on.size() == 1 ) return false;
				else throw new FormParserException( "'On' contains 'NEVER' but other Fields, too." );
			}
			if( on.contains( On.ANY ) ){
				if( on.size() == 1 ) return true;
				else throw new FormParserException( "'On' contains 'ANY' but other Fields, too" );
			}
			
			if( on.contains( event ) ){
				return true;
			} else {
				return false;
			}
	}
	
	private static boolean isFormable( boolean defaultFormable, Field o, On event ) throws FormParserException{
		
		boolean hasAnnotation = o.isAnnotationPresent( Form.class );
		if( hasAnnotation ){
			Form formable = o.getAnnotation( Form.class );
			return isFormable( formable, event );
		} else {
			return defaultFormable;
		}
	}
	
	private static boolean isFormable( boolean defaultFormable, Class<?> cls, Method o, On event ) throws FormParserException{
		
		boolean hasAnnotation = o.isAnnotationPresent( Form.class );
		
		String err = hasSupportedGetterAndSetter( cls, o );
		if( hasAnnotation ){
			if( err != null ) throw new FormParserException( err );
			Form formable = o.getAnnotation( Form.class );
			return isFormable( formable, event );
		} else {
			if( err != null ){
				return false;
			} else {
				return defaultFormable;
			}
		}
			
	}
	
	private static int isIsOrGet( String name ){
		
		int len = name.length();
		if( len < 3 ) return -1;
		if( len > 3 && name.startsWith( "get" ) ) return 3;
		if( name.startsWith( "is" ) ) return 2;
		return -1;
	}
	
	private static Method findSetter( Class<?> cls, Method getter ) {
		
		String getterName = getter.getName();
		int isIsOrGet = isIsOrGet( getterName );
		
		String setterName = "set" + getterName.substring( isIsOrGet );
		Class<?> getterReturn = getter.getReturnType();
		
		try {
			return cls.getMethod( setterName, getterReturn );
		}catch( NoSuchMethodException e ){ return null; }
	}
	
	private static String hasSupportedGetterAndSetter( Class<?> cls, Method getter ){
		
		String getterName = getter.getName();
		
		int isIsOrGet = isIsOrGet( getterName );
		if( isIsOrGet < 0 ) return "Not a getter: " + getter.getName();
		
		Class<?>[] getterParams = getter.getParameterTypes();
		if( getterParams.length != 0 ) return "Getter has parameters";
		Class<?> getterReturn = getter.getReturnType();
		if( getterReturn == void.class ) return "Getter returns void";
		if( isIsOrGet==2 && (getterReturn != boolean.class && getterReturn != Boolean.class ) ) return "Is getter doesn't return boolean";
		
		String setterName = "set" + getterName.substring( isIsOrGet );
		
		Method setter = null;
		try {
			setter = cls.getMethod( setterName, getterReturn );
		}catch( NoSuchMethodException e ){ return "No matching setter"; };
		Class<?> setterReturn = setter.getReturnType();
		if( setterReturn != void.class ) return "Setter is not void";
		
		return null;
	}
	
	private static String makePojoName( Field o ){
		
		String name = o.getName();
		
		return name;
	}
	private static String makePojoName( Method o ){
		
		String name = o.getName();
		
		int isIsOrGet = isIsOrGet( name );
		if( isIsOrGet < 0 )
			throw new IllegalArgumentException( "Element name: '" + name + "' is not parsable" );
		
		name = (""+name.charAt( isIsOrGet )).toLowerCase() + name.substring( isIsOrGet+1 );
		
		return name;
	}
	private static String makeFormKey( String name ){
		
		return CamelCase.toUnderscored( name );
	
	}
	/*
	private static String makeBasename( String formKey ){
		
		StringBuilder result = new StringBuilder();
		
		boolean got_ = false;
		for( int i = 0; i < formKey.length(); i++ ){
			
			char c = formKey.charAt( 0 );
			
			if( c == '_' ) got_ = true;
			else {
				
				if( got_ ) result.append( Character.toUpperCase( c ) );
				else result.append( c );
			}
		}
		
		return result.toString();
	}
	*/
	
	public static List<FormField> fields( Class<?> cls, On event ) throws FormParserException{
		
		LinkedList<FormField> result = new LinkedList<FormField>();
		
		boolean defaultFormable = false;
		
		if( cls.isAnnotationPresent( Form.class ) ){
			defaultFormable = cls.getAnnotation( Form.class ).use();
		}
			
		for( Field field : cls.getFields() ){
			
			if( isFormable( defaultFormable, field, event ) ){
				
				String pojoName = makePojoName( field );
				String formName = makeFormKey( pojoName );
				FormField ff = new FormField( formName, pojoName, field );
				result.add( ff );
			}
		}
		for( Method method : cls.getMethods() ){
			
			if( COMMON_METHODS.contains( method ) ) continue;
			
			if( isFormable( defaultFormable, cls, method, event ) ){
				String pojoName = makePojoName( method );
				String formName = makeFormKey( pojoName );
				Method setter = findSetter( cls, method );
				FormField ff = new FormField( pojoName, formName, method, setter );
				result.add( ff );
			}
		}
		
		if( result.size() == 0 )
			throw new FormParserException( "No parsable fields in " + cls );
		
		return result;
	}
	
	public List<FormField> fields(){
		return parsed;
	}
	
	public static class FormField {
		
		private Method getter;
		private Method setter;
		private Field field;
		private String formName;
		private String pojoName;
		
		FormField( String pojoName, String formName, Method getter, Method setter ){
			this.formName = formName;
			this.pojoName = pojoName;
			this.getter = getter;
			this.setter = setter;
		}
		
		FormField( String formName, String pojoName, Field field ){
			this.formName = formName;
			this.pojoName = pojoName;
			this.field = field;
		}
		
		public Method getter() { return getter; }
		public Method setter() { return setter; }
		public Field field() { return field; }
		public String formName() { return formName; }
		public String pojoName() { return pojoName; }
		
		@Override public String toString(){
			StringBuilder result = new StringBuilder();
			if( getter != null ) result.append( "G: " ).append( getter.getName() );
			if( setter != null ) result.append( ", S: " ).append( setter.getName() );
			result
				.append( ", F: " ).append( field )
				.append( ", Form: " ).append( formName )
				.append( ", Pojo: " ).append( pojoName )
			;
			return result.toString();
		}
	}
	
	private void putInPojoByMethod( Method setter, Object value ) throws FormParserException {
		
		log.trace( "setMethod: " + setter.getName() + " to " + value );
		
		try {
			setter.invoke( pojo, value );
		} catch( Exception e ){
			throw new FormParserException( "Error calling " + setter, e );
		}
	}
	private Object getFromPojoByMethod( Method getter ) throws FormParserException {
		
		Object value;
		
		try {
			value = getter.invoke( pojo );
		} catch( Exception e ){
			throw new FormParserException( "Error calling " + getter, e );
		}
		
		return value;
	}
	
	private void putInPojoByField( Field field, Object value ) throws FormParserException {
		
		log.trace( "setField: " + field.getName() + " to " + value );
		
		try {
			field.set( pojo, value );
		} catch( Exception e ){
			throw new FormParserException( "Error accessing " + field, e );
		}
	}
	private Object getFromPojoByField( Field field ) throws FormParserException {
		
		Object value;
		
		try {
			value = field.get( pojo );
		} catch( Exception e ){
			throw new FormParserException( "Error accessing " + field, e );
		}
		
		return value;
	}
	
	public void putInPojo( WebForm form ) throws WebTemplateException {
		
		for( FormField field : parsed ){
			
			String formName = field.formName;
			FormValue<?> formValue = form.getFormValue( formName );
			if( formValue != null ){
				Object value = formValue.getValue();
				
				if( field.getter != null ){
					putInPojoByMethod( field.setter, value );
				} else {
					putInPojoByField( field.field, value );
				}
			} else {
				log.warn( "Don't find formValue: " + formName );
			}
		}
	}
	
	public void putInForm( WebForm form ) throws WebTemplateException {
		
		for( FormField field : parsed ){
			
			Object value;
			if( field.getter != null ){
				value = getFromPojoByMethod( field.getter );
			} else {
				value = getFromPojoByField( field.field );
			}
			
			FormValue<?> fVal = form.getFormValue( field.formName );
			
			if( fVal == null ){
				log.debug( "Cannot find: {}", field.formName );
			} else {
				forceInto( fVal, value ); //fVal.setValue( value );
			}
		}
	}
	
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	private void forceInto( FormValue val, Object x ) throws ConverterException{
		
		// This is stupid. But needed.
		try {
			val.setValue( x );
		} catch( ClassCastException e ){
			throw new ConverterException( "Error casting " + x + " in " + val.getHtmlInput().getName(), e );
		}
	}
	
	public static void putInputsInHolder( DataHolder holder, String prefix, WebForm form ) throws KeyException, WebTemplateException {
		
		for( String name : form.getFormValueNames() ){
			
			String holderName;
			if( prefix != null ) holderName = prefix+name;
			else holderName = name;
			
			holder.setValue( holderName, form.getHtmlInput( name ) );
		}
		
	}
	
	public static void putInHolder( DataHolder holder, String prefix, WebForm form ) {
		
		for( String name : form.getFormValueNames() ){
			
			String holderName;
			if( prefix != null ) holderName = prefix+name;
			else holderName = name;
			
			holder.setValue( holderName, form.getFormValue( name ).getPlainValue() );
		}
	}
	
	public static class FormParserException extends WebTemplateException {
		public FormParserException( String message ){
			super( message );
		}
		public FormParserException( String message, Throwable cause ){
			super( message, cause );
		}
	}
}
