package de.axone.webtemplate;

import java.util.List;

import de.axone.webtemplate.form.Form.On;

public abstract class OneTemplateListRenderer<D, T extends WebTemplate>
extends MultiTemplateListRenderer<D>
implements Renderer {
	
	final T template;
	
	public OneTemplateListRenderer( String name, String title, T template, List<D> list, On on ){
		
		super( name, title, list, on );
		
		this.template = template;
		
	}
	
	@Override
	public WebTemplate templateFor( D data, int index ) {
		return template;
	}

}