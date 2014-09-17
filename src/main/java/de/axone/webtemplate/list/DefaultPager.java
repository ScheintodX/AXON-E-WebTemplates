package de.axone.webtemplate.list;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.tools.Text;
import de.axone.web.HttpLinkBuilder;
import de.axone.web.SuperURL;
import de.axone.web.SuperURL.FinalEncoding;
import de.axone.web.SuperURLPrinter;
import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.form.Translator;

/**
 *
 *
 * Include boundaries
 * [1] 2 3 4 5 6 7 8 9 ... 100 >>
 * << 1 2 3 4 [5] 6 7 8 9 ... 100 >>
 * << 1 ... 20 21 22 23 [24] 25 26 27 28 .. 100 >>
 * << 1 ... 91 92 93 94 [95] 96 97 98 99 100 >>
 * << 1 ... 91 92 93 94 95 96 97 98 99 [100]
 *
 * Don't include boundaries
 * [1] 2 3 4 5 6 7 8 9 >>
 * << 1 2 3 4 [5] 6 7 8 9 >>
 * << 20 21 22 23 [24] 25 26 27 28 >>
 * << 90 91 92 93 94 [95] 96 97 98 99 100 >>
 * << 92 93 94 95 96 97 98 99 [100]
 *
 * @author flo
 */
public class DefaultPager implements Pager {
	
	private String nameBase;
	private int numPages;
	private int selectedPage;
	private boolean numeratePageZero = true;

	/* Configuration */
	private int offset = 5;
	protected boolean renderIfOnlyOnePage = false;
	protected boolean noHost = true;
	protected boolean noPath = false;
	protected boolean showBoundaries = true;
	protected boolean showArrowheads = true;
	protected boolean showSelectedArrowheads = true;
	
	protected List<String> parametersWhitelist = null;
	
	@Override
	public String toString(){
		return Text.poster( '~', 
			"nameBase: " + nameBase + "\n"
			+ "numPages: " + numPages + "\n"
			+ "selectedPage: " + selectedPage + "\n"
			+ "renderIfOnlyOnePage: " + renderIfOnlyOnePage + "\n"
		) ;
	}

	/* Templates */
	protected String leftContainer = "<div class=\"pager\">";
	protected String rightContainer = "</div>";
	protected Template leftTemplate = new Template( "<a href=\"__link__\">&lt;&lt;</a>" );
	protected Template selectedLeftTemplate = new Template( "<a class=\"active\">&lt;&lt;</a>" );
	protected Template rightTemplate = new Template( "<a href=\"__link__\">&gt;&gt;</a>" );
	protected Template selectedRightTemplate = new Template( "<a class=\"active\">&gt;&gt;</a>" );
	protected Template innerTemplate = new Template( "<a href=\"__link__\">__no__</a>" );
	protected Template selectedTemplate = new Template( "<a class=\"active\">[__no__]</a>" );
	protected Template skippedTemplate = new Template( "&hellip;" );
	protected Template spaceTemplate = new Template( "" );

	public DefaultPager(){}

	public DefaultPager( String nameBase, int selectedPage, int numPages ){

		setNameBase( nameBase );
		setNumPages( numPages );
		setSelectedPage( selectedPage );
	}

	@Override
	public void setNumPages( int numPages ) {

		this.numPages = numPages;
	}

	@Override
	public void setSelectedPage( int selectedPage ) {

		if( selectedPage < 0 ) selectedPage = 0;
		if( selectedPage > numPages-1 ) selectedPage = numPages-1;
		this.selectedPage = selectedPage;
	}

	@Override
	public void setNameBase( String nameBase ) {

		this.nameBase = nameBase;
	}
	
	public void setNumeratePageZero( boolean numeratePageZero ){
		this.numeratePageZero = numeratePageZero;
	}
	
	public void setOffset( int offset ){
		this.offset = offset;
	}

	public void setRenderIfOnlyOnePage( boolean renderIfOnlyOnePage ) {
		this.renderIfOnlyOnePage = renderIfOnlyOnePage;
	}

	public void setNoHost( boolean noHost ){
		this.noHost = noHost;
	}
	public void setNoPath( boolean noPath ){
		this.noPath = noPath;
	}

	public void setShowBoundaries( boolean showBoundaries ) {
		this.showBoundaries = showBoundaries;
	}

	public void setShowArrowheads( boolean showArrowheads ) {
		this.showArrowheads = showArrowheads;
	}

	public void setShowSelectedArrowheads( boolean showSelectedArrowheads ) {
		this.showSelectedArrowheads = showSelectedArrowheads;
	}
	
	public void setParametersWhitelist( String ... parametersWhitelist ) {
		this.parametersWhitelist = Arrays.asList( parametersWhitelist );
	}

	public void setLeftContainer( String leftContainer ) {
		this.leftContainer = leftContainer;
	}

	public void setRightContainer( String rightContainer ) {
		this.rightContainer = rightContainer;
	}


	public void setLeftTemplate( String leftTemplate ){

		this.leftTemplate = new Template( leftTemplate );
	}
	public void setRightTemplate( String rightTemplate ){

		this.rightTemplate = new Template( rightTemplate );
	}
	public void setSelectedLeftTemplate( String selectedLeftTemplate ){

		this.selectedLeftTemplate = new Template( selectedLeftTemplate );
	}
	public void setSelectedRightTemplate( String selectedRightTemplate ){

		this.selectedRightTemplate = new Template( selectedRightTemplate );
	}
	public void setInnerTemplate( String innerTemplate ){

		this.innerTemplate = new Template( innerTemplate );
	}
	public void setSelectedTemplate( String selectedTemplate ){

		this.selectedTemplate = new Template( selectedTemplate );
	}
	public void setSkippedTemplalte( String skippedTemplate ){

		this.skippedTemplate = new Template( skippedTemplate );
	}
	public void setSpaceTemplalte( String spaceTemplate ){

		this.spaceTemplate = new Template( spaceTemplate );
	}

	@Override
	public void render( Object object , PrintWriter out ,
			HttpServletRequest request , HttpServletResponse response , Translator translator , ContentCache cache  )
			throws IOException, WebTemplateException, Exception {
		
		if( ! renderIfOnlyOnePage && numPages <= 1 ) return;

		int lastPage = numPages-1;

		// Range around selected:
		int start = selectedPage - offset;
		int end = selectedPage + offset;

		// Adjust range to fit into valid pages
		if( start < 0 ){
			end = end + ( 0 - start );
			start = 0;
		}
		if( end > lastPage ){
			start = start + ( lastPage - end );
			end = lastPage;
			
			if( start < 0 ){
				start = 0;
			}
		}
		
		SuperURLPrinter printer = SuperURLPrinter.MinimalEncoded
				.finishFor( FinalEncoding.Html )
				;

		// Container: left
		if( leftContainer != null ) out.write( leftContainer );

		// Arrowheads left
		if( showArrowheads ){
			SuperURL leftLink = makePageLink( request, selectedPage-1 );
			if( selectedPage > 0 ){
    			out.write( leftTemplate.toString( selectedPage-1, printer, leftLink ) );
    			out.write( spaceTemplate.toString( selectedPage-1 ) );
			} else if( showSelectedArrowheads ){
    			out.write( selectedLeftTemplate.toString( selectedPage-1, printer, leftLink ) );
    			out.write( spaceTemplate.toString( selectedPage-1 ) );
			}
		}

		if( showBoundaries ){
		// First Page
    		if( start > 0 ){
    			SuperURL firstLink = makePageLink( request, 0 );
    			out.write( innerTemplate.toString( 0, printer, firstLink ) );
    			out.write( spaceTemplate.toString() );
    		}

    		// Skipmark
    		if( start > 1 ){
    			out.write( skippedTemplate.toString() );
    		}
		}

		// Pages
		boolean first = true;
		for( int p = start; p <= end; p++ ){

			if( first ) first = false;
			else out.write( spaceTemplate.toString() );

			SuperURL nPageLink = makePageLink( request, p );
			if( p == selectedPage ){
				out.write( selectedTemplate.toString( p, printer, nPageLink ) );
			} else {
				out.write( innerTemplate.toString( p, printer, nPageLink ) );
			}
		}

		if( showBoundaries ){
    		// Skipmark
    		if( end < lastPage-1 ){
    			out.write( skippedTemplate.toString() );
    		}

    		// LastPage
    		if( end < lastPage ){
    			SuperURL lastLink = makePageLink( request, lastPage );
    			out.write( spaceTemplate.toString() );
    			out.write( innerTemplate.toString( lastPage, printer, lastLink ) );
    		}
		}

		// Arrowhead right
		if( showArrowheads ){
			SuperURL rightLink = makePageLink( request, selectedPage+1 );
			if( selectedPage < lastPage ){
    			out.write( spaceTemplate.toString() );
    			out.write( rightTemplate.toString( selectedPage+1, printer, rightLink ) );
			} else if( showSelectedArrowheads ){
    			out.write( spaceTemplate.toString() );
    			out.write( selectedRightTemplate.toString( selectedPage+1, printer, rightLink ) );
			}
		}

		// Container: right
		if( rightContainer != null ) out.write( rightContainer );
	}

	protected SuperURL makePageLink( HttpServletRequest request, int page ){

		HashMap<String, String> parameters = new HashMap<String,String>();
		List<String> removeParameters = null;
		
		String pageParameter = nameBase + "-page";

		if( page > 0 || numeratePageZero ) parameters.put( pageParameter, ""+page );
		else removeParameters = Arrays.asList( pageParameter );
		
		SuperURL result = HttpLinkBuilder.makeLink( request,
				noHost, noPath, parametersWhitelist, parameters, removeParameters );
		
		return result;
	}

	protected static class Template {

		String str;
		boolean hasLink;
		boolean hasIndex;
		boolean hasNo;

		Template( String str ){
			this.str = str;

			hasLink = str.contains( "__link__" );
			hasIndex = str.contains( "__index__" );
			hasNo = str.contains( "__no__" );
		}

		String toString( int index, SuperURLPrinter printer, SuperURL link ){

			String s = str;
			if( hasLink ) s = s.replaceAll( "__link__", printer.toString( link ) );
			if( hasIndex ) s = s.replaceAll( "__index__", ""+index );
			if( hasNo ) s = s.replaceAll( "__no__", ""+(index+1) );

			return s;
		}
		String toString( int index ){

			String s = str;
			if( hasIndex ) s = s.replaceAll( "__index__", ""+index );
			if( hasNo ) s = s.replaceAll( "__no__", ""+(index+1) );
			return s;
		}
		@Override
		public String toString(){
			return str;
		}
	}

}
