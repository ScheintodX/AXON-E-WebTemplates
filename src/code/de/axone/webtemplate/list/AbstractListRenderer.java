package de.axone.webtemplate.list;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.webtemplate.Renderer;
import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.form.Translator;

public abstract class AbstractListRenderer<T> implements Renderer {

	private final String name;
	private final ListProvider<T> listProvider;
	private final Renderer itemTemplate;
	private final int itemsPerPage;
	private final int currentPage;
	private final int numPages;
	private String sort;

	public AbstractListRenderer(HttpServletRequest request, String name,
			int itemsPerPage, ListProvider<T> listProvider,
			Renderer itemTemplate ) {
		
		this( 
				name, 
				readPage( name, calcNumPages( listProvider, itemsPerPage ), request ), 
				itemsPerPage, 
				readSort( name, request ),
				listProvider,
				itemTemplate
		);
	}

	public AbstractListRenderer(String name, int currentPage, int itemsPerPage,
			String sort, ListProvider<T> listProvider, Renderer itemTemplate ) {

		assert( listProvider != null );
		assert( itemTemplate != null );
		
		this.name = name;
		this.currentPage = currentPage;
		this.itemsPerPage = itemsPerPage;
		this.numPages = calcNumPages( listProvider, itemsPerPage );
		this.listProvider = listProvider;
		this.itemTemplate = itemTemplate;
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

	private static String readSort( String baseName, HttpServletRequest request ) {

		String name = makeName( baseName, "sort" );
		String sort = request.getParameter( name );
		return sort;
	}

	public void initPager( Pager pager ){
		
		pager.setNameBase( name );
		pager.setNumPages( numPages );
		pager.setSelectedPage( currentPage );
	}

	@Override
	public void render( Object object, HttpServletRequest request,
			HttpServletResponse response, Translator translator )
			throws IOException, WebTemplateException, Exception {

		Iterable<T> it = getList();

		for( T t : it ) {

			itemTemplate.render( t, request, response, translator );
		}
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
