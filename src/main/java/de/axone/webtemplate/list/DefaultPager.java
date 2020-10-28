package de.axone.webtemplate.list;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.tools.S;
import de.axone.tools.Text;
import de.axone.web.HttpLinkBuilder;
import de.axone.web.Meta;
import de.axone.web.SuperURL;
import de.axone.web.SuperURL.FinalEncoding;
import de.axone.web.SuperURLPrinter;
import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.form.Translator;

/**
 * Include boundaries
 * [1] 2 3 4 5 6 7 8 9 ... 100 &gt;&gt;
 * &lt;&lt; 1 2 3 4 [5] 6 7 8 9 ... 100 &gt;&gt;
 * &lt;&lt; 1 ... 20 21 22 23 [24] 25 26 27 28 .. 100 &gt;&gt;
 * &lt;&lt; 1 ... 91 92 93 94 [95] 96 97 98 99 100 &gt;&gt;
 * &lt;&lt; 1 ... 91 92 93 94 95 96 97 98 99 [100]
 *
 * Don't include boundaries
 * [1] 2 3 4 5 6 7 8 9 &gt;&gt;
 * &lt;&lt; 1 2 3 4 [5] 6 7 8 9 &gt;&gt;
 * &lt;&lt; 20 21 22 23 [24] 25 26 27 28 &gt;&gt;
 * &lt;&lt; 90 91 92 93 94 [95] 96 97 98 99 100 &gt;&gt;
 * &lt;&lt; 92 93 94 95 96 97 98 99 [100]
 *
 * TODO: rel=first
 *
 * @author flo
 */
public class DefaultPager implements Pager {

	private String nameBase;
	private int numPages;
	private int selectedPage;
	private boolean numeratePageZero = false;

	/* Configuration */
	private int offset = 5;
	protected boolean renderIfOnlyOnePage = false;
	protected boolean noHost = true;
	protected boolean noPath = false;
	protected boolean showBoundaries = true;
	protected boolean showArrowheads = true;
	protected boolean showSelectedArrowheads = true;

	protected List<String> parametersWhitelist = null;
	protected Map<String,String> addParameters = null;

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
	protected Template leftTemplate = new StringTemplate( "<a href=\"__link__\" rel=\"prev\">&lt;&lt;</a>" );
	protected Template leftOfSelectedTemplate = new StringTemplate( "<a rel=\"prev\" href=\"__link__\">__no__</a>" );
	protected Template selectedLeftTemplate = new StringTemplate( "<a class=\"active\">&lt;&lt;</a>" );
	protected Template rightTemplate = new StringTemplate( "<a href=\"__link__\" rel=\"next\">&gt;&gt;</a>" );
	protected Template rightOfSelectedTemplate = new StringTemplate( "<a rel=\"next\" href=\"__link__\">__no__</a>" );
	protected Template selectedRightTemplate = new StringTemplate( "<a class=\"active\">&gt;&gt;</a>" );
	protected Template innerTemplate = new StringTemplate( "<a href=\"__link__\">__no__</a>" );
	protected Template selectedTemplate = new StringTemplate( "<a class=\"active\">[__no__]</a>" );
	protected Template skippedTemplate = new StringTemplate( "&hellip;" );
	protected Template spaceTemplate = new StringTemplate( "" );

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
	public void addParameter( String key, String value ) {
		if( this.addParameters == null ) this.addParameters = new HashMap<>();
		this.addParameters.put( key, value );
	}

	public void setLeftContainer( String leftContainer ) {
		this.leftContainer = leftContainer;
	}
	public void setRightContainer( String rightContainer ) {
		this.rightContainer = rightContainer;
	}


	public void setLeftTemplate( String leftTemplate ){
		setLeftTemplate( leftTemplate != null ? new StringTemplate( leftTemplate ) : EMPTY_TEMPLATE );
	}
	public void setLeftTemplate( Template leftTemplate ){
		this.leftTemplate = leftTemplate;
	}

	public void setRightTemplate( String rightTemplate ){
		setRightTemplate( rightTemplate != null ? new StringTemplate( rightTemplate ) : EMPTY_TEMPLATE );
	}
	public void setRightTemplate( Template rightTemplate ){
		this.rightTemplate = rightTemplate;
	}

	public void setSelectedLeftTemplate( String selectedLeftTemplate ){
		setSelectedLeftTemplate( selectedLeftTemplate != null ? new StringTemplate( selectedLeftTemplate ) : EMPTY_TEMPLATE );
	}
	public void setSelectedLeftTemplate( Template selectedLeftTemplate ){
		this.selectedLeftTemplate = selectedLeftTemplate;
	}

	public void setLeftOfSelectedTemplate( String leftOfSelectedTemplate ){
		setLeftOfSelectedTemplate( leftOfSelectedTemplate != null ? new StringTemplate( leftOfSelectedTemplate ) : EMPTY_TEMPLATE );
	}
	public void setLeftOfSelectedTemplate( Template leftOfSelectedTemplate ){
		this.leftOfSelectedTemplate = leftOfSelectedTemplate;
	}

	public void setSelectedRightTemplate( String selectedRightTemplate ){
		setSelectedRightTemplate( selectedRightTemplate != null ? new StringTemplate( selectedRightTemplate ) : EMPTY_TEMPLATE );
	}
	public void setSelectedRightTemplate( Template selectedRightTemplate ){
		this.selectedRightTemplate = selectedRightTemplate;
	}

	public void setRightOfSelectedTemplate( String rightOfSelectedTemplate ){
		setRightOfSelectedTemplate( rightOfSelectedTemplate != null ? new StringTemplate( rightOfSelectedTemplate ) : EMPTY_TEMPLATE );
	}
	public void setRightOfSelectedTemplate( Template rightOfSelectedTemplate ){
		this.rightOfSelectedTemplate = rightOfSelectedTemplate;
	}

	public void setInnerTemplate( String innerTemplate ){
		setInnerTemplate( innerTemplate != null ? new StringTemplate( innerTemplate ) : EMPTY_TEMPLATE );
	}
	public void setInnerTemplate( Template innerTemplate ){
		this.innerTemplate = innerTemplate;
	}

	public void setSelectedTemplate( String selectedTemplate ){
		setSelectedTemplate( selectedTemplate != null ? new StringTemplate( selectedTemplate ) : EMPTY_TEMPLATE );
	}
	public void setSelectedTemplate( Template selectedTemplate ){
		this.selectedTemplate = selectedTemplate;
	}

	public void setSkippedTemplate( String skippedTemplate ){
		setSkippedTemplate( skippedTemplate != null ? new StringTemplate( skippedTemplate ) : EMPTY_TEMPLATE );
	}
	public void setSkippedTemplate( Template skippedTemplate ){
		this.skippedTemplate = skippedTemplate;
	}

	public void setSpaceTemplate( String spaceTemplate ){
		setSpaceTemplate( spaceTemplate != null ? new StringTemplate( spaceTemplate ) : EMPTY_TEMPLATE );
	}
	public void setSpaceTemplate( Template spaceTemplate ){
		this.spaceTemplate = spaceTemplate;
	}

	public List<Meta> makeMetas( HttpServletRequest request ) {

		if( numPages <= 1 ) return Collections.emptyList();

		List<Meta> result = new ArrayList<>( 2 );

		if( selectedPage > 0 ) {
			result.add( Meta.link( "prev", makePageLink( request, selectedPage-1 ) ) );
		}
		if( selectedPage < numPages-1 ) {
			result.add( Meta.link( "next", makePageLink( request, selectedPage+1 ) ) );
		}
		return result;
	}

	@Override
	public void render( Object object, PrintWriter out,
			HttpServletRequest request, HttpServletResponse response, Translator translator, ContentCache cache  )
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
			} else if( p == selectedPage-1 ){
				out.write( leftOfSelectedTemplate.toString( p, printer, nPageLink ) );
			} else if( p == selectedPage+1 ){
				out.write( rightOfSelectedTemplate.toString( p, printer, nPageLink ) );
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

		HashMap<String, String> parameters = new HashMap<>();
		if( this.addParameters != null ) parameters.putAll( addParameters );
		List<String> removeParameters = null;

		String pageParameter = nameBase + "-page";

		if( page > 0 || numeratePageZero ) parameters.put( pageParameter, ""+(page+1) ); // human friendly page
		else removeParameters = Arrays.asList( pageParameter );

		SuperURL result = HttpLinkBuilder.makeLink( request,
				noHost, noPath, parametersWhitelist, parameters, removeParameters );

		return result;
	}

	// see AbstractListRenderer.readPage for reading pagelink-parameters

	protected interface Template {

		String toString( int index, SuperURLPrinter printer, SuperURL link );
		String toString( int index );
	}

	protected static final class StringTemplate implements Template {

		private static final String _NO_ = "__no__",
		                            _INDEX_ = "__index__",
		                            _LINK_ = "__link__";

		private final String template;
		private final boolean hasLink;
		private final boolean hasIndex;
		private final boolean hasNo;

		public StringTemplate( String str ){

			template = str;

			if( str == null ) {
				hasLink = false;
				hasIndex = false;
				hasNo = false;
			} else {
				hasLink = str.contains( _LINK_ );
				hasIndex = str.contains( _INDEX_ );
				hasNo = str.contains( _NO_ );
			}

		}

		@Override
		public String toString( int index, SuperURLPrinter printer, SuperURL link ){

			if( template == null ) return S.EMPTY;

			String s = template;
			if( hasLink ) s = s.replaceAll( _LINK_, printer.toString( link ) );
			if( hasIndex ) s = s.replaceAll( _INDEX_, ""+index );
			if( hasNo ) s = s.replaceAll( _NO_, ""+(index+1) );

			return s;
		}

		@Override
		public String toString( int index ){

			if( template == null ) return S.EMPTY;

			String s = template;
			if( hasIndex ) s = s.replaceAll( _INDEX_, ""+index );
			if( hasNo ) s = s.replaceAll( _NO_, ""+(index+1) );
			return s;
		}

		@Override
		public String toString(){
			return template;
		}
	}

	private static final class EmptyTemplate implements Template {

		@Override
		public String toString( int index, SuperURLPrinter printer, SuperURL link ) {
			return S.EMPTY;
		}

		@Override
		public String toString( int index ) {
			return S.EMPTY;
		}

		@Override
		public String toString() {
			return S.EMPTY;
		}
	}

	protected static final Template EMPTY_TEMPLATE = new EmptyTemplate();

}
