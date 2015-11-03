package de.axone.webtemplate.form;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.axone.tools.CamelCase;
import de.axone.tools.Str;
import de.axone.webtemplate.DataHolder;
import de.axone.webtemplate.KeyException;
import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.converter.ConverterException;
import de.axone.webtemplate.element.FormValueFactory;
import de.axone.webtemplate.form.Form.Access;
import de.axone.webtemplate.form.Form.On;

/**
 * Class for automatically parsing @Form Pojos for Form fields
 * and putting something in it which is read from Form classes
 * or putting something in Form classes or generating them
 * 
 * @author flo
 *
 * @param <T>
 */
public class FormParser<T> {
	
	// Only for debugging
	private final Class<?> pojoClass;
	
	public static final Logger log =
			LoggerFactory.getLogger( FormParser.class );
	
	// Methods which are defined in java.lang.Object which 
	// should not be used in parsing
	private static final HashSet<Method> COMMON_METHODS 
			= new HashSet<Method>( Arrays.asList( java.lang.Object.class.getMethods() ) );
	
	// The parsed fields. Available after constructor
	private final List<FormField> parsed;
	
	/**
	 * Initialize the FormParser with a Pojo Class for which we need the Forms
	 * 
	 * @param pojoClass
	 * @param event
	 * @throws FormParserException
	 */
	public FormParser( Class<T> pojoClass, On event ) throws FormParserException {
		this.pojoClass = pojoClass;
		this.parsed = fields( pojoClass, event );
	}
	
	private static boolean isFormable( Form formable, On event ) throws FormParserException{
		
			if( !formable.enabled() ) return false;
			
			List<On> on = Arrays.asList( formable.on() );
			
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
	
	private static boolean isFormable( boolean defaultFormable, Class<?> cls, Method method, On event ) throws FormParserException{
		
		boolean hasAnnotation = method.isAnnotationPresent( Form.class );
		
		String err = hasSupportedGetterAndSetter( cls, method );
		
		if( hasAnnotation ){
			
			Form formable = method.getAnnotation( Form.class );
			
			if( isFormable( formable, event ) ){
				
				if( err != null ) throw new FormParserException( err + ": " + cls.getSimpleName() + '.' + method.getName() );
				
				return true;
			} else {
				return false;
			}
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
		
		boolean ignoreMissingSetter = false;
		Form form;
		form = cls.getAnnotation( Form.class );
		if( form != null ){
			ignoreMissingSetter = form.ignoreMissingSetter();
		}
		form = getter.getAnnotation( Form.class );
		if( form != null ){
			ignoreMissingSetter = form.ignoreMissingSetter();
		}
		
		if( ! ignoreMissingSetter ){
			String setterName = "set" + getterName.substring( isIsOrGet );
			
			Method setter = null;
			try {
				setter = cls.getMethod( setterName, getterReturn );
			} catch( NoSuchMethodException e ){ return "No matching setter"; };
			Class<?> setterReturn = setter.getReturnType();
			if( setterReturn != void.class ) return "Setter is not void";
		}
		
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
	 * Parse the given Pojo and return a list of FormFields
	 * according to the annotated settings there. (if any)
	 */
	public static List<FormField> fields( Class<?> pojo, On event ) throws FormParserException{
		
		log.debug( "Parsing: {}", pojo );
		
		//(new Throwable()).printStackTrace();
		
		LinkedList<FormField> result = new LinkedList<FormField>();
		
		boolean defaultFormable = false;
		Access access = Access.UNDEF;
		
		if( pojo.isAnnotationPresent( Form.class ) ){
			Form aForm = pojo.getAnnotation( Form.class );
			defaultFormable = aForm.enabled();
			access = aForm.access();
		}
		
		if( access == Access.FIELD || access == Access.BOTH || access == Access.UNDEF ){
			
			log.debug( "  has Field access" );
			
			for( Field field : pojo.getFields() ){
				
				boolean myDefaultFormable = defaultFormable || ( access == Access.FIELD || access == Access.BOTH );
				
				boolean isFormable = isFormable( myDefaultFormable, field, event );
				
				log.debug( "{} formable: {}", field, isFormable );
				
				if( isFormable ){
					
					String pojoName = makePojoName( field );
					String formName = makeFormKey( pojoName );
					String formType = null;
					if( field.isAnnotationPresent( Form.class ) ) {
						formType = field.getAnnotation( Form.class ).type();
					}
					if( formType == null || formType.length() == 0 ){
						formType = field.getType().getName();
					}
					FormField ff = new FormField( formName, pojoName, field, formType );
					result.add( ff );
				}
			}
		}
		
		// use methods only if no fields are declared
		if( access == Access.METHOD || access == Access.BOTH || access == Access.UNDEF ){
			
			log.debug( "  has Method access" );
			
			for( Method method : pojo.getMethods() ){
			
				if( COMMON_METHODS.contains( method ) ){
					log.debug( "  skipping: is common method" );
					continue;
				}
				
				boolean myDefaultFormable = defaultFormable || ( access == Access.METHOD || access == Access.BOTH );
				
				boolean isFormable = isFormable( myDefaultFormable, pojo, method, event );
				
				log.debug( "{} formable: {}", method, isFormable );
			
				if( isFormable ){
					
					String pojoName = makePojoName( method );
					String formName = makeFormKey( pojoName );
					String formType = null;
					if( method.isAnnotationPresent( Form.class ) ) {
						formType = method.getAnnotation( Form.class ).type();
					}
					if( formType == null || formType.length() == 0 ){
						formType = method.getReturnType().getName();
					}
					Method setter = findSetter( pojo, method );
					FormField ff = new FormField( pojoName, formName, method, setter, formType );
					result.add( ff );
				}
			}
		}
		
		if( result.size() == 0 )
			throw new FormParserException( "No parsable fields in " + pojo );
		
		return result;
	}
	
	public List<FormField> fields(){
		return parsed;
	}
	
	/**
	 * Represents one form field
	 * 
	 * @author flo
	 */
	public static class FormField {
		
		private Method getter;
		private Method setter;
		private Field field;
		private final String formName;
		// Only needed for toString and debugging if correct name is generated
		private final String pojoName;
		private String formType;
		
		FormField( String pojoName, String formName, Method getter, Method setter, String formType ){
			this.formName = formName;
			this.pojoName = pojoName;
			this.getter = getter;
			this.setter = setter;
			this.formType = formType;
		}
		
		FormField( String formName, String pojoName, Field field, String formType ){
			this.formName = formName;
			this.pojoName = pojoName;
			this.field = field;
			this.formType = formType;
		}
		
		public Method getter() { return getter; }
		public Method setter() { return setter; }
		public Field field() { return field; }
		public String formName() { return formName; }
		public String pojoName() { return pojoName; }
		public String formType() { return formType; }
		public Form form() {
			
			if( field != null && field.isAnnotationPresent( Form.class ) ){
				return field.getAnnotation( Form.class );
			} else if( getter != null && getter.isAnnotationPresent( Form.class )){
				return getter.getAnnotation( Form.class );
			}
			return null;
		}
		
		@Override public String toString(){
			StringBuilder result = new StringBuilder();
			
			result.append( formName ).append( ": " );
			
			if( getter != null ){
				result.append( getter.getName() );
				if( setter != null ){
					result.append( " / " ).append( setter.getName() );
				} else {
					result.append( " - SETTER MISSING - " );
				}
			}
			if( field != null ) result.append( field.getName() + " : " + field.getGenericType() );
			result.append( " (" ).append( pojoName ).append( ')' );
			return result.toString();
		}
	}
	
	private void putInPojoByMethod( T pojo, Method setter, Object value ) throws FormParserException {
		
		log.trace( "setMethod: " + setter.getName() + " to " + value );
		
		try {
			setter.invoke( pojo, value );
		} catch( Exception e ){
			throw new FormParserException( "Error calling " + setter, e );
		}
	}
	private Object getFromPojoByMethod( T pojo, Method getter ) throws FormParserException {
		
		Object value;
		
		try {
			value = getter.invoke( pojo );
		} catch( Exception e ){
			throw new FormParserException( "Error calling " + getter, e );
		}
		
		return value;
	}
	
	private void putInPojoByField( T pojo, Field field, Object value ) throws FormParserException {
		
		log.trace( "setField: " + field.getName() + " to " + value );
		
		try {
			field.set( pojo, value );
		} catch( Exception e ){
			throw new FormParserException( "Error accessing " + field, e );
		}
	}
	private Object getFromPojoByField( T pojo, Field field ) throws FormParserException {
		
		Object value;
		
		try {
			value = field.get( pojo );
			
		} catch( Exception e ){
			throw new FormParserException( "Error accessing " + field, e );
		}
		
		return value;
	}
	
	/**
	 * Take the form and put its' values in the pojo
	 * 
	 * @param pojo
	 * @param form
	 * @throws WebTemplateException
	 */
	public void putFormInPojo( T pojo, WebForm form ) throws WebTemplateException {
		
		for( FormField field : parsed ){
			
			String formName = field.formName;
			FormValue<?> formValue = form.getFormValue( null, formName );
			if( formValue != null ){
				Object value = formValue.getValue();
				
				if( field.setter != null ){
					putInPojoByMethod( pojo, field.setter, value );
				} else if( field.field != null ) {
					putInPojoByField( pojo, field.field, value );
				} else {
					log.warn( "Missing setter for: " + field.pojoName() );
				}
			} else {
				log.warn( "Don't find formValue: " + formName );
			}
		}
	}
	
	/**
	 * Put values from the pojo and put them into the WebForm
	 * 
	 * @param pojo
	 * @param form
	 * @throws WebTemplateException
	 */
	public void putPojoInForm( T pojo, WebForm form ) throws WebTemplateException {
		
		for( FormField field : parsed ){
			
			Object value;
			if( field.getter != null ){
				value = getFromPojoByMethod( pojo, field.getter );
			} else {
				value = getFromPojoByField( pojo, field.field );
			}
			
			FormValue<?> fVal = form.getFormValue( null, field.formName );
			
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
	
	/**
	 * Put HTML input fields in DataHolder fields
	 * 
	 * This method is a helper for connecting Forms and WebTemplates 
	 * and works with a configured Form only. (so this is why it's static)
	 * 
	 * @param holder
	 * @param prefix
	 * @param form
	 * @throws KeyException
	 * @throws WebTemplateException
	 */
	public static void putInputsInHolder( DataHolder holder, String prefix, WebForm form ) throws KeyException, WebTemplateException {
		
		for( String name : form.getFormValueNames() ){
			
			String holderName;
			if( prefix != null ) holderName = prefix+name;
			else holderName = name;
			
			holder.setValue( holderName, form.getHtmlInput( name ) );
		}
		
	}
	
	/**
	 * Put the plain values from WebForm in DataHolder
	 * 
	 * This method is a helper for connecting Forms and WebTemplates 
	 * and works with a configured Form only. (so this is why it's static)
	 * 
	 * @param holder
	 * @param prefix
	 * @param form
	 */
	public static void putPlainInHolder( DataHolder holder, String prefix, WebForm form ) {
		
		if( prefix == null ) prefix="";
		
		for( String name : form.getFormValueNames() ){
			
			holder.setValue( prefix+name, form.getFormValue( null, name ).getPlainValue() );
		}
	}
	
	/**
	 * Put the VERY plain values from WebForm in DataHolder
	 * 
	 * This method is a helper for connecting Pojos and WebTemplates 
	 * and uses the VERY plain Values. No conversion is done. So you
	 * have to live with what 'toString' gives you.
	 * 
	 * Better don't use this.
	 * 
	 * @param holder
	 * @param prefix
	 * @param pojo
	 * @param event
	 * @throws FormParserException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws InvocationTargetException 
	 */
	public static void putPlainInHolder( DataHolder holder, String prefix, Object pojo, On event )
			throws FormParserException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		if( prefix == null ) prefix = "";
		
		Class<?> clz = pojo.getClass();
		
		List<FormField> fields = fields( clz, event );
		for( FormField field : fields ){
			Object value;
			if( field.getter() != null ){
				value = field.getter().invoke( pojo, (Object[])null );
			} else {
				value = field.field().get( pojo );
			}
			holder.setValue( prefix + field.formName(), value );
		}
	}
	
	/**
	 * Create a WebForm. This means generate the Fields to store the values
	 * in according to the definition in the Pojo
	 * 
	 * @param form
	 * @param fvf
	 * @param prefix
	 * @throws WebTemplateException
	 */
	public void createFormValuesInForm( WebForm form, FormValueFactory fvf, String prefix )
			throws WebTemplateException{
		
		for( FormField field : parsed ){
			
			String name = field.formName();
			String fieldName = prefix != null ? prefix+name : name;
			String type = field.formType();
			
			FormValue<?> formValue = fvf.byType( type, fieldName, field.form() );
			
			FormValue<?> oldFormValue = form.getFormValue( null, name );
			
			if( oldFormValue == null ){
				// Skip silently already defined Values
				form.addFormValue( name, formValue );
			}
			
		}
		
	}
	
	@Override
	public String toString(){
		
		Collections.sort( parsed, FF_SORTER );
		
		String className = pojoClass.getSimpleName();
		
		return className + "\n" + Str.join( "\n", parsed );
	}
	
	private static final Comparator<FormField> FF_SORTER = new Comparator<FormField>(){

		@Override
		public int compare( FormField o1, FormField o2 ) {
			return o1.formName().compareTo( o2.formName() );
		}
		
	};
	
	
	/**
	 * Exception thrown if something goes wrong while processing Forms
	 * 
	 * @author flo
	 */
	public static class FormParserException extends WebTemplateException {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1462401608140462715L;
		public FormParserException( String message ){
			super( message );
		}
		public FormParserException( String message, Throwable cause ){
			super( message, cause );
		}
	}
}
