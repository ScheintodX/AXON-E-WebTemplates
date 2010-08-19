package de.axone.webtemplate.form;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.element.HtmlInput;


public interface WebForm {

	public void initialize( HttpServletRequest request );

	public void addFormValue( String name, FormValue<?> connectorValue )
		throws WebTemplateException;
	public FormValue<?> getFormValue( String name );

	public Set<String> getFormValueNames();

	public HtmlInput getHtmlInput( String name ) throws WebTemplateException;

	public Set<String> getValueNames();

	public boolean isValid();
	public List<String> validate();

	public String getPlainValue( String name )
		throws WebTemplateException;

	public void setShowInvalid( boolean showInvalid );

	public void setTranslationProvider( Translator textProvider );

}
