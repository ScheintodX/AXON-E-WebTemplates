package de.axone.webtemplate.list;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.axone.webtemplate.Renderer;
import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.form.Translator;

public class AbstractMemoryListRenderer<T> implements Renderer {
	
	private String name;
	private ListProvider<T> listProvider;
	private Renderer itemTemplate;
	private Pager pagerTemplate;
	private int itemsPerPage;
	
	private HttpServletRequest request;
	private HttpSession session;

	public AbstractMemoryListRenderer( String name, int itemsPerPage, ListProvider<T> listProvider,
			Renderer itemTemplate, Pager pagerTemplate ) {

		this.name = name;
		this.itemsPerPage = itemsPerPage;
		this.listProvider = listProvider;
		this.itemTemplate = itemTemplate;
		this.pagerTemplate = pagerTemplate;
	}

	@Override
	public void render( Object object, HttpServletRequest request,
			HttpServletResponse response, Translator translator )
			throws IOException, WebTemplateException, Exception {

		this.request = request;
		this.session = request.getSession();
		
		Iterable<T> it = getList();
		
		pagerTemplate.setNameBase( name );
		pagerTemplate.setNumPages( getNumPages() );
		pagerTemplate.setSelectedPage( getPage() );
		
		for( T t : it ){
			
			itemTemplate.render( t, request, response, translator );
		}
	}
	
	protected Iterable<T> getList(){
		
		return listProvider.getList( getStartIndex(), getItemsPerPage(), getSort() );
	}
	
	protected int getStartIndex(){
		
		return getPage() * getItemsPerPage();
	}
	
	protected int getNumPages(){
		
		int count = (int) Math.ceil( (double) getItemCount() / getItemsPerPage() );
		
		// Even if no items return 1 page
		if( count == 0 ) return 1;
		
		return count;
	}
	
	protected int getItemsPerPage(){
		return itemsPerPage;
	}
	
	protected int getItemCount(){
		
		return listProvider.getTotalCount();
	}
	
	protected int getPage(){
		
		String name = makeName( "page" );
		
		String pageStr = request.getParameter( name );
		Integer page;
		
		if( pageStr != null ){
			
			try{
    			page = Integer.parseInt( pageStr );
    			if( page < 0 ) page = 0;
    			if( page >= getNumPages() ) page = getNumPages()-1;
			} catch( NumberFormatException e ){
				page = 0;
			}
			session.setAttribute( name, page );
		} else {
			page = (Integer)session.getAttribute( name );
		}
		
		if( page != null ){
			return page;
		} else {
			return 0;
		}
	}
	
	protected String getSort(){
		
		String name = makeName( "sort" );
		String sort = request.getParameter( name );
		if( sort != null ){
			session.setAttribute( name, sort );
		} else {
			sort = (String)session.getAttribute( name );
		}
		return sort;
	}
	
	protected String makeName( String name ){
		
		return this.name + "-" + name;
	}
	

}
