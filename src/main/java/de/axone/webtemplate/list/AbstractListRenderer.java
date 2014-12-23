package de.axone.webtemplate.list;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.Renderer;
import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.form.Translator;
import de.axone.webtemplate.list.Listable.Position;

public abstract class AbstractListRenderer<T> implements Renderer {

	private final String name;
	private final ListProvider<T> listProvider;
	private final int itemsPerPage;
	private final int currentPage;
	private final int numPages;
	private String sort;
	
	protected abstract Renderer itemTemplate( T item );

	public AbstractListRenderer( HttpServletRequest request, String name, String defaultSort,
			int itemsPerPage, ListProvider<T> listProvider ) {
		
		this( 
				name, 
				readPage( name, calcNumPages( listProvider, itemsPerPage ), request ), 
				itemsPerPage, 
				readSort( name, defaultSort, request ),
				listProvider
		);
	}

	public AbstractListRenderer(String name, int currentPage, int itemsPerPage,
			String sort, ListProvider<T> listProvider ) {

		assert name != null;
		assert listProvider != null;
		
		this.name = name;
		this.currentPage = currentPage;
		this.itemsPerPage = itemsPerPage;
		this.numPages = calcNumPages( listProvider, itemsPerPage );
		this.listProvider = listProvider;
		this.sort = sort;
	}
	
	private static int calcNumPages( ListProvider<?> listProvider, int itemsPerPage ) {
		
		int count = (int) Math.ceil( (double) listProvider.getTotalCount()
				/ itemsPerPage );

		// Even if no items return 1 page
		if( count == 0 )
			return 1;

		return count;
	}

	private static int readPage( String baseName, int numPages, HttpServletRequest request ) {

		String name = makeName( baseName, "page" );
		
		String pageStr = request.getParameter( name );
		Integer page = null;
		
		if( pageStr != null ) {

			try {
				page = Integer.parseInt( pageStr );
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

	private static String readSort( String baseName, String defaultSort, HttpServletRequest request ) {

		String name = makeName( baseName, "sort" );
		String sort = request.getParameter( name );
		if( sort == null ) sort = defaultSort;
		return sort;
	}

	public void initPager( Pager pager ){
		
		pager.setNameBase( name );
		pager.setNumPages( numPages );
		pager.setSelectedPage( currentPage );
	}
	
	public void initSortSelector( SortSelector sortSelector ){
		
		sortSelector.setNameBase( name );
		sortSelector.setSelectedSort( sort );
	}

	@Override
	public void render( Object object , PrintWriter out , HttpServletRequest request ,
			HttpServletResponse response , Translator translator , ContentCache cache  )
			throws IOException, WebTemplateException, Exception {

		Iterable<T> list = getList();

		int i=0;
		for( Iterator<T> it = list.iterator(); it.hasNext(); ){
			
			T t = it.next();
			
			Renderer itemTemplate = itemTemplate( t );
			
			if( itemTemplate instanceof Listable ){
				Listable listableItemTemplate = (Listable) itemTemplate;
				listableItemTemplate.setIndexInList( currentPage * itemsPerPage + i );
				Position pos;
				if( i == 0 ) pos = Position.TOP;
				else if( it.hasNext() ) pos = Position.MIDDLE;
				else pos = Position.BOTTOM;
				listableItemTemplate.setPositionInList( pos );
				listableItemTemplate.setHighlight( isHighlight( t ) );
			}

			itemTemplate.render( t, out, request, response, translator, cache );
			i++;
		}
	}
	
	protected boolean isHighlight( T t ){
		return false;
	}

	protected Iterable<T> getList() {

		return listProvider.getList( getStartIndex(), itemsPerPage, sort );
	}

	protected int getStartIndex() {

		return currentPage * itemsPerPage; 
	}

	public void setSort( String sort ) {
		this.sort = sort;
	}

	protected static String makeName( String baseName, String name ) {

		return baseName + "-" + name;
	}

}
