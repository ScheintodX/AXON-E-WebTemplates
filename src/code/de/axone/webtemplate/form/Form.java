package de.axone.webtemplate.form;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a class, method or member with this annotation
 * to tell the FormParser that this object are parsable
 * 
 * Annotate to class to tell that by default all values should
 * be parsable.
 * 
 * Annotate to method to tell that by default the method is parsable.
 * Annotation to methods will imply that there is a public getter and setter
 * available which will be used.
 * 
 * Annotate to member variable to tell that it should be parsed
 * 
 * use 'use' to inverse this behaviour.
 * 
 * Variable name generation is done in this way:
 * <ul>
 * <li>get/set will be replaced
 * <li>upper case letters following a lower case will be replaced by _+lower case letter
 * <li>all remaining is converted to lower case
 * </ul>
 * 
 * e.g.: <tt>getMyValue</tt> will convert to <tt>my_value</tt>
 * <tt>getTheZIPCode will be converted to <tt>get_zip_code</tt>
 * 
 * @author flo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface Form {

	/**
	 * Name of form variable to use
	 * 
	 * @return the name as string
	 */
	public String name() default "[guess]";
	
	/**
	 * Set to <tt>false</tt> to exclude or inverse default behaviour.
	 * 
	 * @return <tt>true</tt> if is a form field. default <tt>true</tt>
	 */
	public boolean use() default true;
	
	/**
	 * Tell on which action the parsing should occur.
	 * 
	 * This is especially useful for the 'id' field which 
	 * should only be parsed on edit, not on create.
	 * 
	 * @return on which operations this field is used. default: <tt>On.ANY</tt>
	 */
	public On [] on() default {On.ANY};
	
	/**
	 * Enumeration for 'on()'
	 * 
	 * @author flo
	 */
	public enum On {
		
		/**
		 * Parse on create
		 */
		CREATE,
		
		/**
		 * Parse on show
		 */
		SHOW,
		
		/**
		 * Parse on edit
		 */
		EDIT,
		
		/**
		 * Parse on delete
		 */
		DELETE,
		
		/**
		 * Parse every time
		 */
		ANY,
		
		/**
		 * Parse never. Similar to formalbe=false
		 */
		NEVER
		
		;
	}
}
