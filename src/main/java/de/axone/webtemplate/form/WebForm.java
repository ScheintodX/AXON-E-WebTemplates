package de.axone.webtemplate.form;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.element.HtmlInput;


/**
 * A webform encapsulates the access to form-values in a html &lt;form&gt;.
 * 
 * It is composed of a couple of FormValues which do the real work of
 * representing an html input each.
 * 
 * Implementing classes should extend WebFormImpl
 * 
 * There is no need to construct WebForms by hand. The FormParser can do this for you.
 * 
 * @author flo
 *
 */
public interface WebForm {
	
	/**
	 * Initialize the WebForm with the values read from an http request
	 * 
	 * @param request
	 */
	public void readFromRequest( HttpServletRequest request );

	
	/**
	 * For debug only. Return some meaningfull name.
	 * 
	 * In the std. implementation this is the classname
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Adds a form value to this WebForm
	 * 
	 * @param name as it is used in &lt;input name="..." &gt;
	 * @param connectorValue
	 * @throws WebTemplateException
	 */
	public void addFormValue( String name, FormValue<?> connectorValue )
		throws WebTemplateException;
	
	/**
	 * Returns the FormValue for the given name
	 * @param name
	 * @return
	 */
	public FormValue<?> getFormValue( String name );
	public void remFormValue( String name )
		throws WebTemplateException;

	public Set<String> getFormValueNames();

	public HtmlInput getHtmlInput( String name ) throws WebTemplateException;

	public boolean isValid();
	public List<String> validate();

	public String getPlainValue( String name )
		throws WebTemplateException;

	public void setShowInvalid( boolean showInvalid );

	/**
	 * Sets a translation provider and activates translating
	 * 
	 * Set to NULL if you don't want translation to happen here
	 * 
	 * @param textProvider
	 */
	public void setTranslationProvider( Translator textProvider );

}