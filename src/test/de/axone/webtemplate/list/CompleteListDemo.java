package de.axone.webtemplate.list;

import static org.testng.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.tools.E;
import de.axone.web.TestHttpServletRequest;
import de.axone.web.TestHttpServletResponse;
import de.axone.webtemplate.Renderer;
import de.axone.webtemplate.WebTemplateException;
import de.axone.webtemplate.form.Translator;

public class CompleteListDemo {

	public static void main( String[] args ) throws Exception {

		CompleteListDemo demo = new CompleteListDemo();
		TestHttpServletRequest req = new TestHttpServletRequest();
		TestHttpServletResponse resp = new TestHttpServletResponse();
		
		req.setRequestURI( "http://www.axon-e.de/blah?x=1&y=2&y=3&test-page=5&mylist-page=10" );
		
		demo.doTest( req, resp );
		
		E.rr( resp.getContent() );
	}

	private void doTest( HttpServletRequest req, HttpServletResponse resp )
			throws WebTemplateException, Exception {

		PrintWriter out = resp.getWriter();
		assertNotNull( out );

		// Pager ohne Listen:

		int page = 0;
		if( req.getParameter( "test-page" ) != null ) {
			page = Integer.parseInt( req.getParameter( "test-page" ) );
		}
		DefaultPager pager = new DefaultPager( "test", page, 100 );

		pager.render( null, out, req, resp, null );

		out.println( "<hr/>" );
		out.println( "<hr/>" );

		// Pager mit Listen:

		MyListProvider listProvider = new MyListProvider();
		MyItemTemplate itemTemplate = new MyItemTemplate();
		pager = new DefaultPager();
		pager.setShowArrowheads( false );
		pager.setShowBoundaries( false );
		MyListRenderer listRenderer = new MyListRenderer( req, listProvider,
				itemTemplate );
		
		listRenderer.initPager( pager );

		listRenderer.render( null, out, req, resp, null );
		out.println( "<hr/>" );
		pager.render( null, out, req, resp, null );
	}

	private static class MyListRenderer extends AbstractListRenderer<Integer> {
		
		private final Renderer itemTemplate;

		public MyListRenderer(HttpServletRequest req, ListProvider<Integer> listProvider,
				Renderer itemTemplate ) {
			super( req, "mylist", "name", 10, listProvider );
			this.itemTemplate = itemTemplate;
		}

		@Override
		protected Renderer itemTemplate( Integer item ) {
			return itemTemplate;
		}
	}

	private static class MyListProvider implements ListProvider<Integer> {

		private static final int SIZE = 1234;

		@Override
		public Iterable<Integer> getList( int beginIndex, int count, String sort ) {

			if( beginIndex >= SIZE )
				return new LinkedList<Integer>();

			LinkedList<Integer> result = new LinkedList<Integer>();
			for( int i = beginIndex; i < beginIndex + count; i++ ) {
				if( i >= SIZE )
					break;
				result.add( i );
			}
			return result;
		}

		@Override
		public int getTotalCount() {

			return SIZE;
		}

	}

	private static class MyItemTemplate implements Renderer {

		@Override
		public void render( Object object, PrintWriter out,
				HttpServletRequest request, HttpServletResponse response, Translator translator )
				throws IOException, WebTemplateException, Exception {

			Integer i = (Integer) object;

			out.write( i + "<br/>" );
		}

	}
}
