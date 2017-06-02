package de.axone.webtemplate.list;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.exception.Assert;
import de.axone.webtemplate.Renderer;
import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.form.Translator;
import de.axone.webtemplate.list.Listable.Position;
import de.axone.webtemplate.list.Sorting.SortProvider;

/**
 * 
 * @author flo
 *
 * @param <T> Type of item in list
 * @param <S> Selftype. Name of actual implementation
 */
public abstract class AbstractListRenderer<T,S extends AbstractListRenderer<T,S>> implements Renderer {
	
	// s. also TemplateArticleList for usage

	private final String name;
	private final ListProvider<T> listProvider;
	private final int itemsPerPage;
	private final int numPages;
	private final ItemTemplateProvider<T> itemTemplateProvider;
	private final Decorator<T> decorator;
	private final SortProvider<T> sortProvider;
	
	private Sorting<T> sort;
	private int currentPage;
	private boolean initComplete = false;
	
	public interface ItemTemplateProvider<X> {
		Renderer itemTemplate( X item );
	}
	public interface Decorator<T> {
		public void decorate( Renderer itemTemplate, T item, int index, int currentPage, int itemsPerPage, boolean hasNext );
	}
	
	public AbstractListRenderer( Class<S> selfType, String name, int itemsPerPage,
			ListProvider<T> listProvider, SortProvider<T> sortProvider, ItemTemplateProvider<T> itemTemplateProvider,
			Decorator<T> decorator  ) {
		
		Assert.notNull( name, "name" );
		Assert.notNull( listProvider, "listProvider" );
		Assert.notNull( itemTemplateProvider, "itemTemplateProvider" );
		Assert.canCast( this, "selfType", selfType );
		
		this.name = name;
		this.itemsPerPage = itemsPerPage;
		this.listProvider = listProvider;
		this.itemTemplateProvider = itemTemplateProvider;
		this.decorator = decorator;
		this.numPages = calcNumPages( listProvider, itemsPerPage );
		this.sortProvider = sortProvider;
		
		this.initComplete = false;
	}
	
	// Completes init
	public S parseRequest( HttpServletRequest request ) {
		
		this.currentPage = readPage( name, numPages, request );
		this.sort = readSort( name, sortProvider, request );
		
		this.initComplete = true;
		
		@SuppressWarnings( "unchecked" )
		S result = (S)this;
		
		return result;
	}
	
	public static int calcNumPages( ListProvider<?> listProvider, int itemsPerPage ) {
		
		if( itemsPerPage <= 0 ) return 1;
		
		int count = (int) Math.ceil( (double) listProvider.getTotalCount()
				/ itemsPerPage );

		// Even if no items return 1 page
		if( count == 0 ) return 1;

		return count;
	}
	
	// see Default Pager for link building
	public static int readPage( String baseName, int numPages, HttpServletRequest request ) {

		String name = makeName( baseName, "page" );
		
		String pageStr = request.getParameter( name );
		Integer page = null;
		
		if( pageStr != null ) {

			try {
				page = Integer.parseInt( pageStr );
				page = page-1; // restore from human friendly
				if( page < 0 )
						page = 0;
				if( page >= numPages )
						page = numPages - 1;
			} catch( NumberFormatException e ) {
				page = 0;
			}
		}
		
		if( page != null ) {
			return page;
		} else {
			return 0;
		}
	}
	
	private static <T> Sorting<T> readSort( String baseName, SortProvider<T> prov, HttpServletRequest request ) {
		
		String name = makeName( baseName, "sort" );
		Sorting<T> sort = prov != null ? prov.sortingFor( request.getParameter( name ) ) : null;
		return sort;
	}
	
	public S initPager( Pager pager ){
		
		Assert.isTrue( initComplete, "must parse Request first" );
		
		pager.setNameBase( name );
		pager.setNumPages( numPages );
		pager.setSelectedPage( currentPage );
		
		@SuppressWarnings( "unchecked" )
		S result = (S)this;
		return result;
	}
	
	public S initSortSelector( SortSelector<T> sortSelector ){
		
		Assert.isTrue( initComplete, "must parse Request first" );
		
		sortSelector.setNameBase( name );
		sortSelector.setSelectedSort( sort );
		
		@SuppressWarnings( "unchecked" )
		S result = (S)this;
		return result;
	}

	@Override
	public void render( Object object, PrintWriter out, HttpServletRequest request,
			HttpServletResponse response, Translator translator, ContentCache cache  )
			throws IOException, WebTemplateException, Exception {
		
		if( !initComplete ) parseRequest( request );
		
		Iterable<T> list = getList();
		
		int i=0;
		for( Iterator<T> it = list.iterator(); it.hasNext(); ){
			
			T item = it.next();
			
			Renderer itemTemplate = itemTemplateProvider.itemTemplate( item );
			
			if( decorator != null ) decorator.decorate( itemTemplate, item, i, currentPage, itemsPerPage, it.hasNext() );
			
			itemTemplate.render( item, out, request, response, translator, cache );
			i++;
		}
	}
	
	protected Iterable<T> getList() {
		
		return listProvider.getList( getStartIndex(), itemsPerPage, sort );
	}

	protected int getStartIndex() {

		return currentPage * itemsPerPage; 
	}

	public void setSort( Sorting<T> sort ) {
		this.sort = sort;
	}

	protected static String makeName( String baseName, String name ) {

		return baseName + "-" + name;
	}
	
	public static class ListableDecorator<T> implements Decorator<T> {

		@Override
		public void decorate( Renderer itemTemplate, T item, int index, int currentPage, int itemsPerPage, boolean hasNext ) {
			
			if( itemTemplate instanceof Listable ){
				Listable listableItemTemplate = (Listable) itemTemplate;
				listableItemTemplate.setIndexInList( currentPage * itemsPerPage + index );
				Position pos;
				if( index == 0 ) pos = Position.TOP;
				else if( hasNext ) pos = Position.MIDDLE;
				else pos = Position.BOTTOM;
				listableItemTemplate.setPositionInList( pos );
				listableItemTemplate.setHighlight( isHighlight( item ) );
			}

		}
		protected boolean isHighlight( T t ){
			return false;
		}
	}

}

/*
 * Old AbstractListRenderer only used by MailOrderListRenderer
 * 
package de.axone.shop.template;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.refactor.Refactor;
import de.axone.shop.EMogul;
import de.axone.shop.selector.SelectorException;
import de.axone.webtemplate.Renderer;
import de.axone.webtemplate.WebTemplate;
import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.form.Translator;
import de.emogul.object.TemplateMailOrder;


@Refactor( action="REWRITE", reason="wofür ist das ganze parameter gesetze" )
public class AbstractListRenderer<T> implements Renderer {

	protected final PageInfo pageInfo;
	protected final ObjectInfo objectInfo;
	protected Collection<T> items;

	public AbstractListRenderer( ObjectInfo objectInfo, PageInfo pageInfo, Collection<T> items ){

		this.pageInfo = pageInfo;
		this.objectInfo = objectInfo;
		this.items = items;
	}

	@Override
	public void render( Object object, PrintWriter out,
			HttpServletRequest request, HttpServletResponse response, Translator translator, ContentCache cache )
			throws IOException, WebTemplateException, Exception {

		int c = 0;
		for( T item : items ){

			WebTemplate template = getTemplate( item );

			String pos = pos( c, items.size() );

			template.setParameter( "index", "index-" + c );
			template.setParameter( "pos", pos );
			if( pageInfo != null ){
				if( pageInfo.getPage() != null ) template.setParameter( "page", pageInfo.getPage() );
				if( pageInfo.getSubpage() != null ) template.setParameter( "subpage", pageInfo.getSubpage() );
				if( pageInfo.getPlace() != null ) template.setParameter( "place", pageInfo.getPlace() );
				if( pageInfo.getSubplace() != null ) template.setParameter( "subplace", pageInfo.getSubplace() );
				if( pageInfo.getOption() != null ) template.setParameter( "option", pageInfo.getOption() );
			}
			if( objectInfo != null ){
				if( objectInfo.getObjectGroup() != null ) template.setParameter( "group", objectInfo.getObjectGroup() );
				if( objectInfo.getObjectSubgroup() != null ) template.setParameter( "subgroup", objectInfo.getObjectSubgroup() );
				if( objectInfo.getObjectType() != null ) template.setParameter( "type", objectInfo.getObjectType() );
				if( objectInfo.getObjectSubtype() != null ) template.setParameter( "subtype", objectInfo.getObjectSubtype() );
			}

			template.render( item, out, request, response, translator, cache );

			c++;
		}
	}

	private static String pos( int index, int size ){

		StringBuilder builder = new StringBuilder();

		if( index== 0 ) builder.append( " pos-top" );
		if( index > 0 && index < size-1 ) builder.append( " pos-middle" );
		if( index== size-1 ) builder.append( " pos-bottom" );

		if( builder.length() > 0 ){
			return builder.toString().substring( 1 );
		} else {
			return "";
		}
	}

	private WebTemplate template;
	protected WebTemplate getTemplate( T t ) throws SelectorException, WebTemplateException{

		if( template == null ){
			
			template = EMogul.rt().templates()
					.getObjectTemplate( objectInfo, pageInfo )
					.expectIsInstanceOf( TemplateMailOrder.class )
					;
		}
		return template;
	}


}
*/