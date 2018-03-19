package de.axone.webtemplate;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.axone.cache.ng.CacheNG;
import de.axone.cache.ng.CacheProvider;
import de.axone.data.Charsets;
import de.axone.file.Slurper;
import de.axone.tools.watcher.FileDataWatcher;
import de.axone.webtemplate.slicer.Slicer;
import de.axone.webtemplate.slicer.SlicerFactory;

public class FileDataHolderFactory extends AbstractDataHolderFactory {

	public static final Logger log =
			LoggerFactory.getLogger( FileDataHolderFactory.class );

	final SlicerFactory slicerFactory;
	final CacheNG.Cache<File, FileDataWatcher<DataHolder>> cache;
	static int reloadCount=0;
	
	public FileDataHolderFactory(){
		this( null, null, null );
	}
	
	public FileDataHolderFactory( CacheNG.Cache<File, FileDataWatcher<DataHolder>> cache, SlicerFactory slicerFactory, CacheProvider<String,String> cacheProvider ){
		this.slicerFactory = slicerFactory;
		this.cache = cache;
	}

	@SuppressWarnings( "null" )
	synchronized public DataHolder holderFor( File file )
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, WebTemplateException {
		
		FileDataWatcher<DataHolder> watcher;
		DataHolder result;
		
		if( cache == null ) {
			
				result = instantiate( file );
				watcher = null; // make the compiler happier
				
		} else {
			
			if( !cache.isCached( file ) ) {
	
				result = instantiate( file );
				
				watcher = new FileDataWatcher<>( file, result );
				
				cache.put( file, watcher );
				
			} else {
				
				watcher = cache.fetch( file );
				
				if( !watcher.haveChanged() ) {
					result = cache.fetch( file ).getData();
				} else {
					result = instantiate( file );
					watcher.setData( result );
					cache.put( file, watcher );
				}
			}
		}
		
		// Slicer
		if( slicerFactory != null ){
			
			// If has source
			String source = result.getParameter( DataHolder.P_SOURCE );
			
			if( source != null ){
				Slicer slicer = slicerFactory.instance( source );
				//String name = slicer.getTemplateName( file );
				File masterBase = slicer.getMasterBase();
				File master = new File( masterBase, source );
				
				// If timestamp changed
				boolean run = false;
				String timestampS = result.getParameter( DataHolder.P_TIMESTAMP );
				if( timestampS != null ){
					
					long timestamp = Long.parseLong( timestampS );
					long last = master.lastModified() / 1000;
					if( last > timestamp ) run = true;
				} else {
					// no timestamp -> run
					run = true;
				}
				
				// Slice: Allways slice all
				if( run ) slicer.run( source );
				
				// Store
				result = instantiate( file );
				if( cache != null ){
					watcher.setData( result );
					cache.put( file, watcher );
				}
			}
		}
		
		return result.freshCopy();
	}
	
	private static String fileInfo( File file ) {
		
		return file.getParentFile().getName().toUpperCase() + ": " + file.getName();
	}
	
	static DataHolder instantiate( File file ) throws IOException,
			AttributeParserByHand.ParserException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		
		reloadCount++;
		
		String data = Slurper.slurpString( file, Charsets.UTF8 );
		
		DataHolder holder = instantiate( file.getPath(), data );
		
		holder.setSystemParameter( DataHolder.P_FILE, fileInfo( file ) );
		holder.setSystemParameter( DataHolder.P_REAL_FILE, file.getAbsolutePath() );
		
		if( holder.getSystemParameter( DataHolder.P_TIMESTAMP ) == null ){
			holder.setSystemParameter( DataHolder.P_TIMESTAMP, "" + file.lastModified()/1000 ); // 1s
		}
		
		log.trace( "DataHolder for " + file + " created" );
		
		holder.fixValues();

		return holder;

	}
}
