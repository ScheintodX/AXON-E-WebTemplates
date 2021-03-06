package de.axone.webtemplate.list;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.web.Tag;
import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.form.TKey;
import de.axone.webtemplate.form.Translator;

public class DefaultSortSelector<T> implements SortSelector<T> {
	
	private List<Sorting<T>> sortMethods;
	
	private String nameBase;
	private Sorting<T> selectedSort;
	
	private boolean keepPageOnSort = false;
	private List<String> keepParameters = Collections.emptyList();
	
	@SafeVarargs
	public final void setMethods( Sorting<T> ... sortMethods ) {
		setMethods( Arrays.asList( sortMethods ) );
	}
	public void setMethods( List<Sorting<T>> sortMethods ){
		this.sortMethods = sortMethods;
	}

	@Override
	public void setNameBase( String nameBase ) {
		this.nameBase = nameBase;
	}

	@Override
	public void setSelectedSort( Sorting<T> sort ) {
		this.selectedSort = sort;
	}
	
	@Override
	public Sorting<T> getSelectedSort(){
		return selectedSort;
	}
	
	public void setKeepPageOnSort( boolean keepPageOnSort ){
		this.keepPageOnSort = keepPageOnSort;
	}
	
	public void setKeepParameters( List<String> parameterNames ){
		this.keepParameters = parameterNames;
	}
	public void setKeepParameters( String ... parameterNames ) {
		setKeepParameters( Arrays.asList( parameterNames ) );
	}

	@Override
	public void render( Object object , PrintWriter out ,
			HttpServletRequest request , HttpServletResponse response , Translator translator , ContentCache cache  )
			throws IOException, WebTemplateException, Exception {
		
		StringBuilder options = new StringBuilder();
		
		for( Sorting<T> method : sortMethods ){
			LinkedList<String> args = new LinkedList<String>();
			args.add( "value" );
			args.add( method.name() );
			if( method.equals( selectedSort ) ){
				args.add( "selected" );
				args.add( "selected" );
			}
			Tag.simpleBB( 
				options, "option",
				translator != null ? translator.translate( TKey.dynamic( method.name() ) ) : method.name(),
				args.toArray( new String[ args.size() ] )
			);
		}
		
		String select = Tag.simple( 
				"select", options.toString(), false,
				"name", nameBase + "-sort",
				"class",
				"submit_on_change"
		);
		
		out.write( select );
		
		for( String parameterName : keepParameters ){
			String val = request.getParameter( parameterName );
			if( val != null ){
				out.write( Tag.hiddenInput( parameterName, val ) );
			}
		}
		
		if( keepPageOnSort ){
			String pageName = nameBase + "-page";
			String page = request.getParameter( pageName );
			if( page != null ){
				out.write( Tag.hiddenInput( pageName, page ) );
			}
		}
		
	}

}
