package de.axone.webtemplate.slicer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.axone.exception.Assert;
import de.axone.tools.Mapper;
import de.axone.webtemplate.WebTemplateException;


public abstract class Slicer {
	
	private File masterBase;
	private File templateBase;

	protected static final Logger log = LoggerFactory.getLogger( Slicer.class );
	
	public abstract List<String> getTemplateNames( String master );
	
	public abstract Class<?> getTemplateClass( String master, String name );
	public abstract void makeTemplate( String master, String name ) throws WebTemplateException;
	public abstract void prepare( String master ) throws WebTemplateException;
	protected abstract int outLen();
	protected abstract String out();
	public abstract void init();
	public abstract void load( String master ) throws IOException;
	public abstract void prependText( String text );
	public abstract void appendText( String text );
	
	public abstract File getTemplateFile( String name );
	public abstract String getTemplateName( File file );
	
	public void setMasterBase( File masterBase ){
		this.masterBase = masterBase;
	}
	public void setTemplateBase( File templateBase ){
		this.templateBase = templateBase;
	}
	
	public File getMasterBase() {
		return masterBase;
	}
	public File getTemplateBase(){
		return templateBase;
	}
	
	public void parse( String master, String ... names ) throws WebTemplateException{
		parse( master, Mapper.hashSet( names ) );
	}
	public void parse( String master, Collection<String> names ) throws WebTemplateException{
		
		Assert.notNull( master, "master" );
		
		for( String name : getTemplateNames( master ) ){
			
			if( names != null && names.size() > 0 && !names.contains( name ) ) continue;
			
			log.debug( "TEMPLATE: {}", name.toUpperCase() );
			
			try {
				Class<?> clazz = getTemplateClass( master, name );
				
				makeTemplate( master, name );
				
				String content = out();
				
				File outFile = getTemplateFile( name );
				File masterFile = new File( getMasterBase(), master );
				
				try (
					PrintWriter fOut = new PrintWriter(
							new OutputStreamWriter(
									new FileOutputStream( outFile ), "utf-8" ) );
				) {
					fOut.println( "@Class: " + clazz.getName() );
					fOut.println( "@Source: " + master );
					fOut.println( "@Timestamp: " + masterFile.lastModified()/1000 );
					fOut.println();
					fOut.println( content );
					
					log.debug( " Written: {}", outFile.getCanonicalPath() );
				}
				
			} catch( Throwable t ) {
				throw new SlicerException( name, t );
			}
		}
	}
	
	public void run( String master, String ... names ) throws WebTemplateException, IOException {
		run( master, Mapper.treeSet( names ) );
	}
	public void run( String master, Collection<String> names ) throws WebTemplateException, IOException {
		
		init();
		
		load( master );
		
		prepare( master );
		
		parse( master, names );
	}

	private static class SlicerException extends WebTemplateException {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 5222215964190533168L;
		String name;
		
		SlicerException( String name, Throwable throwable ){
			
			super( throwable );
			this.name = name;
		}
		
		@Override
		public String getMessage(){
			return super.getMessage() + " in '" + name + "'";
		}
	}
}
