package de.axone.webtemplate.slice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

import de.axone.tools.EasyParser;

/**
 * Settings for Slicing
 * 
 * One Settings-File is for exactly one master-template
 * 
 * @author flo
 */
public class MasterTemplate {
	
	static final String MASTER = "master";
		
	public static void main( String [] args )
			throws FileNotFoundException, MissingValueException, SettingsException, IOException{
		
		new MasterTemplate( new File( "master_print.properties" ) );
	}
	
	File masterFile;
	Properties properties;
	final Map<String,Template> templates;
	
	public MasterTemplate( File file ) throws SettingsException,
			FileNotFoundException, IOException, MissingValueException {
		
		Map<String,Template> newTemplates = load( file );
		templates = Collections.unmodifiableMap( newTemplates );
	}
	
	private Map<String,Template> load( File file )
			throws FileNotFoundException, IOException, SettingsException {
		
		// Load properties
		properties = new Properties();
		properties.load( new FileReader( file ) );
		
		// Trim
		for( Object key : properties.keySet() ){
			String value = properties.getProperty( (String)key );
			value = value.trim();
			properties.setProperty( (String)key, value );
		}
		
		// Master in file
		masterFile = new File( properties.getProperty( "in.file" ) );
		if( !masterFile.canRead() )
			throw new IllegalArgumentException( "Cannot read: " + masterFile.getAbsolutePath() );
		
		// All output files
		String filesStr = properties.getProperty( "out.files" );
		if( filesStr == null ) throw new MissingValueException( "files" );
		
		// Build Templates
		HashMap<String,Template> templates = new HashMap<String,Template>();
		String [] files = filesStr.split( "\\s+" );
		for( String f : files ){
			
			Template template = new Template( f, properties );
			templates.put( f, template );
		}
		
		if( ! templates.containsKey( MASTER ) )
			throw new MissingValueException( MASTER );
		
		return templates;
	}
	
	/*
	 * This is one template of a master template
	 * 
	 * 
	 */
	static class Template {
		
		private static final Pattern VARPAT = Pattern.compile( "%%(.*?)%%" );
		
		private static final String NAME = "name";
		private static final String FILE = "file";
		private static final String PATH = "path";
		private static final String CLASS = "class";
		
		private static final String STRIP = "strip";
		
		private static final String PREFIX = "prefix";
		private static final String SUFFIX = "suffix";
		
		private static final String REPLACE = "replace";
		
		/**
		 * Name of the config entries
		 */
		final String configName;
		
		/**
		 * File to write to
		 */
		final File file;
		
		/**
		 * Templates name. This is used to replace cut sections with. 
		 * E.g. 'name' will be replaced with '__name__'
		 */
		final String name;
		
		/**
		 * XQuery selector to select a template out of the master template
		 */
		final String path;
		
		/**
		 * Class to prepend to the template with <tt>@Class</tt> annotation
		 */
		final String clazz;
		
		/**
		 * Remove element which was selected by XQuery before writing the template
		 */
		final boolean strip;
		
		/**
		 * Prepend this to Template before writing
		 */
		final String prefix;
		
		/**
		 * Append this to Template before writing
		 */
		final String suffix;
		
		/**
		 * Will be set by slicer when with the XPath selection
		 */
		Node node;
		
		/**
		 * List of key/value replacement which will be replaced in the template
		 * 
		 * The key is a XQuery relative to the the XQuery which selected the template
		 */
		List<Replacement> replacements = new LinkedList<Replacement>();
		
		Template( String configName, Properties p ) throws MissingValueException{
			
			// -- Needed by prop method: ----------------------
			this.configName = configName;
			
			// -- name ----------------------------------------
			String myName = prop( p, NAME );
			
			if( MASTER.equals( configName ) ){
				this.name = MASTER;
			} else {
				this.name = myName;
			}
			
			if( this.name == null )
				throw new MissingValueException( configName, "name" );
			
			// -- file ----------------------------------------
			String myFileName = propReq( p, FILE );
			this.file = new File( myFileName );
			
			this.path = prop( p, PATH );
			if( path == null && this.name != MASTER )
				throw new MissingValueException( configName, "path" );
			this.clazz = propReq( p, CLASS );
			
			this.prefix = prop( p, PREFIX );
			this.suffix = prop( p, SUFFIX );
			
			this.strip = EasyParser.isYes( prop( p, STRIP ) );
			
			for( int i=0; ; i++ ){
				String key = REPLACE + "." + i;
				String value = prop( p, key );
				if( value == null ) break;
				
				String [] parts = value.split( "\\s+-->\\s+" );
				
				replacements.add( new Replacement( parts[0], parts[1] ) );
			}
		}
		
		private String prop( Properties properties, String name ) throws MissingValueException {
			return prop( properties, name, false );
		}
		
		private String propReq( Properties properties, String name ) throws MissingValueException {
			return prop( properties, name, true );
		}
			
		private String prop( Properties properties, String name, boolean required ) throws MissingValueException {
			
			String propName = configName + "." + name;
			
			String result = properties.getProperty( propName );
			
			if( required && result == null )
				throw new MissingValueException( propName );
			
			result = replaceVars( result, properties );
			
			return result;
		}
		
		private String replaceVars( String value, Properties properties ){
			
			if( value == null ) return null;
			
			StringBuffer result = new StringBuffer();
			
			Matcher m = VARPAT.matcher( value );
			
			while( m.find() ){
				
				String val = m.group( 1 );
				
				String repl = properties.getProperty( val );
				
				if( repl != null ){
					m.appendReplacement( result, repl );
				}
			}
			
			m.appendTail( result );
			
			return result.toString();
			
		}
		
		@Override
		public String toString() {
			return "Template [configName=" + configName + ", file=" + file.getName()
					+ ", name=" + name + ", path=" + path + ", clazz=" + clazz
					+ ", strip=" + strip + ", prefix=" + prefix + ", suffix="
					+ suffix + ", replacements=" + replacements + "]";
		}

		private static class Replacement {
			
			String path;
			String replace;
			
			Replacement( String path, String replacement ){
				this.path = path;
				this.replace = replacement;
			}
			
			@Override
			public String toString() {
				return "Replacement [path=" + path + ", replace=" + replace
						+ "]";
			}
			
		}
		
	}
	
	static class SettingsException extends Exception {

		public SettingsException() { super(); }

		public SettingsException( String message, Throwable cause ) {
			super( message, cause );
		}
		public SettingsException( String message ) {
			super( message );
		}
		public SettingsException( Throwable cause ) {
			super( cause );
		}
	}
	
	static class MissingValueException extends SettingsException {
		
		MissingValueException( String configName, String name ){
			super( "Missing value: '" + configName + "." + name + "'" );
		}
		MissingValueException( String name ){
			super( "Missing value: '" + name + "'" );
		}
	}
	
}
