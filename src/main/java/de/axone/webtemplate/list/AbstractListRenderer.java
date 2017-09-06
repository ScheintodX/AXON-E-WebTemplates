package de.axone.webtemplate.list;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.exception.Assert;
import de.axone.webtemplate.Renderer;
import de.axone.webtemplate.WebTemplate;
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
	
	private Map<String,Object> options;
	
	public interface ItemTemplateProvider<X> {
		Renderer itemTemplate( X item );
	}
	public interface Decorator<T> {
		public void decorate( Renderer itemTemplate, T item, String active, int index, int currentPage, int itemsPerPage, boolean hasNext );
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
	
	public S addOption( String key, Object value ) {
		
		if( options == null ) options = new HashMap<>();
		
		options.put( key, value );
		
		@SuppressWarnings( "unchecked" )
		S result = (S)this;
		
		return result;
	}
	
	public S setOptions( Map<String, Object> options ) {
		
		this.options = options;
		
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
		String active = getActive();
		
		int i=0;
		for( Iterator<T> it = list.iterator(); it.hasNext(); ){
			
			T item = it.next();
			
			Renderer itemTemplate = itemTemplateProvider.itemTemplate( item );
			
			if( decorator != null ) decorator.decorate( itemTemplate, item, active, i, currentPage, itemsPerPage, it.hasNext() );
			
			if( options != null && itemTemplate instanceof WebTemplate ) {
				
				for( Map.Entry<String,Object> e : options.entrySet() ) {
					((WebTemplate)itemTemplate).setParameter( e.getKey(), e.getValue() );
				}
			}
			
			itemTemplate.render( item, out, request, response, translator, cache );
			i++;
		}
	}
	
	protected Iterable<T> getList() {
		
		return listProvider.getList( getStartIndex(), itemsPerPage, sort );
	}

	protected String getActive() {
		
		if( !( listProvider instanceof Activatable )) return null;
		
		return ((Activatable)listProvider).getActiveIdentifier();
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
		public void decorate( Renderer itemTemplate, T item, String active, int index, int currentPage, int itemsPerPage, boolean hasNext ) {
			
			if( itemTemplate instanceof Listable ){
				Listable listableItemTemplate = (Listable) itemTemplate;
				listableItemTemplate.setIndexInList( currentPage * itemsPerPage + index );
				Position pos;
				if( index == 0 ) pos = Position.TOP;
				else if( hasNext ) pos = Position.MIDDLE;
				else pos = Position.BOTTOM;
				listableItemTemplate.setPositionInList( pos );
				listableItemTemplate.setHighlight( isHighlight( item, active ) );
			}

		}
		
		protected boolean isHighlight( T t, String active ){
			if( active != null ) {
				if( !( t instanceof Identifiable ) ) throw new IllegalArgumentException( "Cannot identify: " + t );
				
				return active.equals( ((Identifiable)t).getIdentifier() );
			} else {
				return false;
			}
		}
	}

}
