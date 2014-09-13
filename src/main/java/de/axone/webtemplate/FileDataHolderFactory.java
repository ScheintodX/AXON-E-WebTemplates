package de.axone.webtemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.MalformedInputException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.axone.cache.ng.CacheNG;
import de.axone.cache.ng.CacheProvider;
import de.axone.tools.FileDataWatcher;
import de.axone.tools.Slurper;
import de.axone.webtemplate.AbstractFileWebTemplate.ParserException;
import de.axone.webtemplate.slicer.Slicer;
import de.axone.webtemplate.slicer.SlicerFactory;

public class FileDataHolderFactory extends AbstractDataHolderFactory {

	public static final Logger log =
			LoggerFactory.getLogger( FileDataHolderFactory.class );

	final SlicerFactory slicerFactory;
	final CacheNG.Cache<File, FileDataWatcher<DataHolder>> cache;
	static int reloadCount=0;
	
	public FileDataHolderFactory( CacheNG.Cache<File, FileDataWatcher<DataHolder>> cache, SlicerFactory slicerFactory, CacheProvider<String,String> cacheProvider ){
		this.slicerFactory = slicerFactory;
		this.cache = cache;
	}

	synchronized public DataHolder holderFor( File file, CacheProvider<String,String> contentCache )
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, WebTemplateException {
		
		FileDataWatcher<DataHolder> watcher;
		DataHolder result;
		if( !cache.isCached( file ) ) {

			result = instantiate( file, contentCache );
			watcher = new FileDataWatcher<>( file, result );
			
			cache.put( file, watcher );
			
		} else {
			watcher = cache.fetch( file );
			
			if( !watcher.hasChanged() ) {
				result = cache.fetch( file ).getData();
			} else {
				result = instantiate( file, contentCache );
				watcher.setData( result );
				cache.put( file, watcher );
			}
		}
		
		// Sliceer
		if( slicerFactory != null ){
			
			// If has source
			String source = result.getParameter( DataHolder.PARAM_SOURCE );
			
			if( source != null ){
				Slicer slicer = slicerFactory.instance( source );
				//String name = slicer.getTemplateName( file );
				File masterBase = slicer.getMasterBase();
				File master = new File( masterBase, source );
				
				// If timestamp changed
				boolean run = false;
				String timestampS = result.getParameter( DataHolder.PARAM_TIMESTAMP );
				if( timestampS != null ){
					long timestamp = Long.parseLong( timestampS );
					long last = master.lastModified() / 1000;
					if( last > timestamp ) run = true;
				} else {
					// no timestamp -> run
					run = true;
				}
				
				// Slice: Allways slice all
				//if( run ) slicer.run( source, name );
				if( run ) slicer.run( source );
				
				// Store
				result = instantiate( file, contentCache );
				watcher.setData( result );
				cache.put( file, watcher );
			}
		}
		
		return result.freshCopy();
	}
	
	static DataHolder instantiate( File file, CacheProvider<String,String> contentCache ) throws IOException,
			ParserException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		
		reloadCount++;

		String data = slurp( file );
		
		DataHolder holder = instantiate( data, contentCache );
		
		holder.setParameter( DataHolder.PARAM_FILE, file.getPath() );
		if( holder.getParameter( DataHolder.PARAM_TIMESTAMP  ) == null ){
			holder.setParameter( DataHolder.PARAM_TIMESTAMP, "" + file.lastModified()/1000 ); // 1s
		}
		
		log.trace( "DataHolder for " + file + " created" );

		return holder;

	}

	private static String slurp( File file ) throws IOException {

		Charset charset = Charset.forName( "UTF-8" );
		CharsetDecoder decoder = charset.newDecoder();
		
		try (
			FileInputStream fin = new FileInputStream( file );
			FileChannel cIn = fin.getChannel();
		) {
		
			int size = (int)file.length();
			ByteBuffer buf;
			
				buf = Slurper.slurp( cIn, size );
				if( buf.limit() != size ) throw new IOException( 
						"Filesize (" + size + ") doesn't match read size (" + buf.limit() + ")" );
			
			try {
				CharBuffer cBuf = decoder.decode( buf );
				return cBuf.toString();
			} catch( MalformedInputException e ){
				throw new IOException( "Perhaps not UFT-8?", e );
			}
		}
		
	}

}
