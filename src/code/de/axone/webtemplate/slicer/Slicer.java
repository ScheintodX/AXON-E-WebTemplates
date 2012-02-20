package de.axone.webtemplate.slicer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public abstract class Slicer {

	public abstract File getOutputPath();
	public abstract File getMasterTemplate();
	public abstract List<String> getTemplateNames();
	public abstract String getTemplateClass( String name );
	public abstract void makeTemplate( String name ) throws Exception;
	public abstract void prepare() throws Exception;
	public abstract String out();
	public abstract void init();
	public abstract void load() throws IOException;
	
	public File getTemplatePath( String name ){
		return new File( getOutputPath(), name );
	}
	
	protected boolean verbose = false;
	protected PrintWriter log = new PrintWriter( System.out, true );
	
	public void setVerbose( boolean verbose ){
		this.verbose = verbose;
	}

	public void parse() throws Exception {
		
		for( String name : getTemplateNames() ){
			
			if( verbose )
				log.println( ("Template: " + name).toUpperCase() );
			
			try {
				String clazzs = getTemplateClass( name );
				
				makeTemplate( name );
				
				String content = out();
				
				File outFile = getTemplatePath( name );
				
				try (
					PrintWriter fOut = new PrintWriter( new FileWriter( outFile ) );
				) {
					fOut.println( "@Class: " + clazzs );
					fOut.println( "@Source: " + getMasterTemplate().getAbsolutePath() );
					fOut.println();
					fOut.println( content );
					
					if( verbose )
						log.println( " Written: " + outFile );
				}
				
			} catch( Throwable t ) {
				throw new SkriptException( name, t );
			}
		}
	}
	
	public void run() throws Exception {
		
		init();
		
		load();
		
		prepare();
		
		parse();
	}

	private static class SkriptException extends Exception {
		
		String name;
		
		SkriptException( String name, Throwable throwable ){
			
			super( throwable );
			this.name = name;
		}
		
		@Override
		public String getMessage(){
			return super.getMessage() + " in '" + name + "'";
		}
	}
}
