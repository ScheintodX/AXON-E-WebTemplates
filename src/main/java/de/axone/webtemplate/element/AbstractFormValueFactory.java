package de.axone.webtemplate.element;

import java.util.Locale;

import de.axone.webtemplate.Decorator;
import de.axone.webtemplate.DefaultDecorator;
import de.axone.webtemplate.elements.impl.OptionImpl;
import de.axone.webtemplate.elements.impl.OptionList;

public abstract class AbstractFormValueFactory implements FormValueFactory {

	protected boolean useHtml5Input;
	protected Decorator decorator = new DefaultDecorator();
	protected Locale defaultLocale = Locale.GERMANY;
	protected String defaultAjaxValidate;

	public AbstractFormValueFactory() {
		super();
	}

	@Override
	public Decorator getDecorator() {
		return decorator;
	}

	@Override
	public void setDecorator( Decorator decorator ) {
		this.decorator = decorator;
	}

	@Override
	public String getStandardClass() {
		return decorator.getStandardClass();
	}

	@Override
	public void setLocale( Locale locale ) {
		this.defaultLocale = locale;
	}

	@Override
	public Locale getLocale() {
		return defaultLocale;
	}

	@Override
	public void setUseHtml5Input( boolean useHtml5Input ) {
		this.useHtml5Input = useHtml5Input;
	}

	@Override
	public boolean isUseHtml5Input() {
		return this.useHtml5Input;
	}

	@Override
	public void setDefaultAjaxValidate( String defaultAjaxValidate ) {
		this.defaultAjaxValidate = defaultAjaxValidate;
	}

	@Override
	public String getDefaultAjaxValidate() {
		return this.defaultAjaxValidate;
	}

	protected OptionList convert( SelectableOption [] options ) {
		
		OptionList list = new OptionList();
		for( SelectableOption option : options ){
			list.add( new OptionImpl( option.key(), option.title() ) );
		}
		return list;
		
	}

}