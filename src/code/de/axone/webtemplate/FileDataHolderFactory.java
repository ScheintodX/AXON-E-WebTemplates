package de.axone.webtemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.axone.cache.Cache;
import de.axone.data.Pair;
import de.axone.tools.FileWatcher;
import de.axone.tools.Slurper;
import de.axone.webtemplate.AbstractFileWebTemplate.ParserException;

public class FileDataHolderFactory extends AbstractDataHolderFactory {

	public static final Logger log =
			LoggerFactory.getLogger( FileDataHolderFactory.class );

	final Cache.Direct<File, Pair<FileWatcher, DataHolder>> storage;
	static int reloadCount=0;
	
	public FileDataHolderFactory( Cache.Direct<File, Pair<FileWatcher, DataHolder>> storage ){
		this.storage = storage;
	}

	synchronized public DataHolder holderFor( File file )
			throws IOException, ParserException, ClassNotFoundException, InstantiationException, IllegalAccessException {

		FileWatcher watcher;
		DataHolder result;
		if( !storage.containsKey( file ) ) {

			watcher = new FileWatcher( file );
			result = instantiate( file );
			
			storage.put( file, new Pair<FileWatcher, DataHolder>( watcher, result ) );
		} else {
			watcher = storage.get( file ).getLeft();
			
			if( !watcher.hasChanged() ) {
				result = storage.get( file ).getRight();
			} else {
				result = instantiate( file );
				storage.put( file, new Pair<FileWatcher, DataHolder>( watcher, result ) );
			}
		}

		return result.clone();
	}
	
	static DataHolder instantiate( File file ) throws IOException,
			ParserException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		
		reloadCount++;

		String data = slurp( file );
		
		DataHolder holder = instantiate( data );
		holder.setParameter( "file", file.getPath() );
		
		log.trace( "DataHolder for " + file + " created" );

		return holder;

	}

	private static String slurp( File file ) throws IOException {

		Charset charset = Charset.forName( "UTF-8" );
		CharsetDecoder decoder = charset.newDecoder();
		FileInputStream fin = new FileInputStream( file );
		
		FileChannel cIn = fin.getChannel();
		
		int size = (int)file.length();
		ByteBuffer buf;
		try {
			buf = Slurper.slurp( cIn, size );
			if( buf.limit() != size ) throw new IOException( 
					"Filesize (" + size + ") doesn't match read size (" + buf.limit() + ")" );
		} finally {
			if( cIn != null ) cIn.close();
		}
		
		CharBuffer cBuf = decoder.decode( buf );
		
		return cBuf.toString();
	}

}
