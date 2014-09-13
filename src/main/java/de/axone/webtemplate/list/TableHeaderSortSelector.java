package de.axone.webtemplate.list;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.web.Tag;
import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.form.TKey;
import de.axone.webtemplate.form.Translator;

public class TableHeaderSortSelector implements SortSelector {
	
	private String[] sortMethods;
	
	private String nameBase;
	private String sort;
	
	private boolean keepPageOnSort = false;
	
	public void setMethods( String ... sortMethods ){
		this.sortMethods = sortMethods;
	}

	@Override
	public void setNameBase( String nameBase ) {
		this.nameBase = nameBase;
	}

	@Override
	public void setSelectedSort( String sort ) {
		this.sort = sort;
	}
	
	@Override
	public String getSelectedSort( ) {
		return this.sort;
	}
	
	public void setKeepPageOnSort( boolean keepPageOnSort ){
		this.keepPageOnSort = keepPageOnSort;
	}

	@Override
	public void render( Object object , PrintWriter out ,
			HttpServletRequest request , HttpServletResponse response , Translator translator , ContentCache cache  )
			throws IOException, WebTemplateException, Exception {
		
		StringBuilder result = new StringBuilder();
		
		String q = request.getParameter( "q" );
			
		for( String method : sortMethods ){
			
			LinkedList<String> attributes = new LinkedList<String>();
			
			attributes.add( nameBase + "-sort" );
			attributes.add( method );
			
			if( q != null ){
				attributes.add( "q" );
				attributes.add( q );
			}
			
			if( keepPageOnSort ){
				String pageName = nameBase + "-page";
				String page = request.getParameter( pageName );
				if( page != null ){
					attributes.add( pageName );
					attributes.add( page );
				}
			}
			
			String text = translator != null ? translator.translate(
					TKey.dynamic( method ) ) : method;
			
			boolean selected = method.equals( sort );
			
			if( selected ) text = "[" + text + "]";
			
			Tag.linkBB( 
					result, 
					"", 
					text,
					selected ? "selected" : null,
					attributes.toArray( new String[ attributes.size() ] ) )
			;
		}
		
		/*
		if( keepPageOnSort ){
			String pageName = nameBase + "-page";
			String page = request.getParameter( pageName );
			if( page != null ){
				out.write( Tag.hiddenInput( pageName, page ) );
			}
		}
		*/
		
		out.write( result.toString() );
	}

}
