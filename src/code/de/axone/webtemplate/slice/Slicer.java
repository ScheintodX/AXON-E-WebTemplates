package de.axone.webtemplate.slice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.axone.tools.E;
import de.axone.webtemplate.slice.MasterTemplate.MissingValueException;
import de.axone.webtemplate.slice.MasterTemplate.SettingsException;
import de.axone.webtemplate.slice.MasterTemplate.Template;


public class Slicer {
	
	private static final String USAGE = "SLICE filename\n" +
			"	slices using 'filename.properties' for config";
	
	/**
	 * In namespace awareness mode html-tags have to be prefixed with 'h:'
	 */
	private static final boolean nsAware = true;
	
	//private static final String baseName = "master_print_0201";
	private static final String baseName = "test";
	private static final File inFile = new File( baseName+".xhtml" );
	private static final File propFile = new File( baseName+".properties" );
	
	private final XPathFactory xFactory;
	private final XPath xPath;
	
	private final DocumentBuilderFactory fac;
	private final DocumentBuilder builder;
	
	public Slicer() throws ParserConfigurationException{
		
		fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware( nsAware );
		
		builder = fac.newDocumentBuilder();
		builder.setEntityResolver( LocalEntityResolver );
		
		xFactory = XPathFactory.newInstance();
		
		xPath = xFactory.newXPath();
		if( nsAware ) xPath.setNamespaceContext( CTX );
	}
	
	public static void main( String [] args ) throws Exception {
		
		if( args.length != 1 ){
			
			System.err.println( "Filename missing" );
			System.err.println( USAGE );
			System.exit( 1 );
		}
		
		String filename = args[0];
		
		
		Slicer slicer = new Slicer();
		
		// === Load Settings =====================================
		MasterTemplate settings = slicer.readSettings( new File( filename + ".properties" ) );
		
		// === Parse Document ====================================
		Document doc = slicer.parse( inFile );
		
		// === Do queries ========================================
		/*
		String xQuery = "//h:div[not(node())] | //h:span[not(node())] | //h:button[not(node())]";
		NodeList nodes = slicer.queryNodes( doc, xQuery );
		
		printNodes( nodes );
		
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item( i );
			n.setTextContent( " " );
		}
		
		printNodes( slicer.queryNodes( doc, "//*[@class='first']" ) );
		*/
		
		// === Slice =============================================
		slicer.slice( doc, settings );
		
	}
	
	private MasterTemplate readSettings( File file )
			throws FileNotFoundException, MissingValueException, SettingsException, IOException {
		
		return new MasterTemplate( file );
	}
	
	private NodeList queryNodes( Document doc, String xQuery ) throws XPathExpressionException{
		
		/*
		XPathExpression xExp = xPath.compile( xQuery );
		return (NodeList) xExp.evaluate( doc, XPathConstants.NODESET );
		*/
		E.rr( xQuery );
		return (NodeList) xPath.evaluate( xQuery, doc, XPathConstants.NODESET );
	}
	
	private Document parse( File file ) throws SAXException, IOException{
		
		long start = System.currentTimeMillis();
		Document doc = builder.parse( file );
		long dur = System.currentTimeMillis()-start;
		System.out.println( "Parsing took " + dur + "ms" );
		
		return doc;
	}
	
	/*
	private static void printNodes( NodeList nodes ){
		
		System.out.println( nodes.getLength() + " nodes" );
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item( i );
			System.out.println( n.getNodeName() + "/" + n.getTextContent() );
		}
	}
	*/
	
	private void writeNodes( NodeList nodes, Template tmpl, boolean includeXmlPrefix ) throws IOException, TransformerException{
		
		E.rr( "Save nodes to " + tmpl.file.getName() );
		
        FileWriter fw = new FileWriter( tmpl.file );
        
        if( tmpl.prefix != null ){
        	fw.write( tmpl.prefix );
        	fw.write( '\n' );
        }
        for( int i=0; i<nodes.getLength(); i++ ){
        	writeNode( nodes.item( i ), tmpl, fw, includeXmlPrefix );
        }
        if( tmpl.suffix != null ){
        	fw.write( tmpl.suffix );
        	fw.write( '\n' );
        }
        
        fw.close();
	}
	
	private void writeNode( Node node, Template tmpl, boolean includeXmlPrefix ) throws TransformerException, IOException{
		
		writeNodes( new NodesList( node ), tmpl, includeXmlPrefix );
	}
	
	private void writeNode( Node node, Template tmpl, FileWriter writer, boolean includeXmlPrefix )
	throws IOException, TransformerException{
		
        //set up a transformer
        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes" );
        if( includeXmlPrefix ){
	        trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" );
	        trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//W3C//DTD XHTML 1.0 Strict//EN" );//"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" );
        }
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        //create string from xml tree
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(node);
        trans.transform(source, result);
        String xmlString = sw.toString();
        
        // Remove useless empty spaces in empty Tags
        xmlString = xmlString.replace( "> <", "><" );

        writer.write( xmlString );
	}
	
	private void slice( Document doc, MasterTemplate settings )
	throws XPathExpressionException, TransformerException, IOException{
		
		// Templates
		for( String name : settings.templates.keySet() ){
			
			if( MasterTemplate.MASTER.equals( name ) ) continue; //skip self
			
			Template tx = settings.templates.get( name );
			
			E.rr( tx.name + ": " + tx.path );
			
			NodeList nl = queryNodes( doc, tx.path );
			
			E.rr( name + " (" + nl.getLength() + "): " + tx.path );
			
			for( int i=0; i<nl.getLength(); i++ ){
				
				tx.node = nl.item( i );
				
				Node textNode = doc.createTextNode( "__" + tx.name + "__" );
				tx.node.getParentNode().replaceChild( textNode, tx.node );
				
				// Write node
				if( !tx.strip ){
					writeNode( tx.node, tx, false );
				} else {
					writeNodes( tx.node.getChildNodes(), tx, false );
				}
			}
			
		}
		
		Template master = settings.templates.get( MasterTemplate.MASTER );
		
		// Write master
		writeNode( doc, master, true );
		
	}


	private static NamespaceContext CTX = new NamespaceContext() {

		@Override
		public String getNamespaceURI( String prefix ) {
			
			if( prefix.equals( "h" ) ){
				return "http://www.w3.org/1999/xhtml";
			} else {
				return null;
			}
		}

		// Not needed:
		@Override
		public String getPrefix( String namespaceURI ) { return null; }

		@Override
		public Iterator<?> getPrefixes( String namespaceURI ) { return null; }
		
	};
	
	private static EntityResolver LocalEntityResolver = new EntityResolver() {
		
		private final File BASE = new File(".");
		
		private final String [] entities = new String[]{
			"xhtml1-strict.dtd",
			"xhtml-lat1.ent",
			"xhtml-symbol.ent",
			"xhtml-special.ent"
		};
	
		@Override
		public InputSource resolveEntity( String publicId, String systemId )
				throws SAXException, IOException {
			InputSource inp = null;
			File file = null;
			
			String entityName = systemId;
			
			for( String entityEnding : entities ){
				
				if( entityName.endsWith( entityEnding ) ){
					
					file = new File( BASE, entityEnding );
					break;
				}
			}
			
			if( file != null ) {
				
				URL path = getClass().getResource( file.getName() );
				return new InputSource( path.toString() );
	
			} else {
				throw new UnsupportedOperationException( "Unsupported dtd: " + entityName );
			}
			
		}
	
	};
		
	
	private static final class NodesList implements NodeList {
		
		private final List<Node> nodes;
		
		NodesList( Node node ){
			
			this( Arrays.asList( node ) );
		}
		
		NodesList( List<Node> nodes ){
			this.nodes = nodes;
		}

		@Override
		public Node item( int index ) {
			return nodes.get( index );
		}

		@Override
		public int getLength() {
			return nodes.size();
		}
		
	}
	
}