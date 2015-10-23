package de.axone.webtemplate.form;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.axone.web.rest.RestRequest;
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
	 * Initialize the WebForm with the values read from an http request
	 * in json format.
	 * 
	 * The json has to be passed as paramter 'data'
	 * 
	 * @param request
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public void readFromJsonRequest( RestRequest request ) throws JsonParseException, JsonMappingException, IOException;

	
	/**
	 * For debug only. Return some meaningfull name.
	 * 
	 * In the std. implementation this is the classname
	 * 
	 * @return the name
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
	 * @return the FormValue for the given name
	 * @param name
	 * @param type 
	 */
	public <T> FormValue<T> getFormValue( Class<T> type, String name );
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
