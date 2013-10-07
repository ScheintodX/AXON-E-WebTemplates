package de.axone.webtemplate.form;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.element.HtmlInput;


public interface WebForm {
	
	public String getName();

	public void initialize( HttpServletRequest request );

	public void addFormValue( String name, FormValue<?> connectorValue )
		throws WebTemplateException;
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
