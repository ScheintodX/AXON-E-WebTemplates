package de.axone.webtemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import de.axone.data.LRUCache;
import de.axone.data.Pair;
import de.axone.logging.Log;
import de.axone.logging.Logging;
import de.axone.tools.FileWatcher;
import de.axone.tools.Slurper;
import de.axone.webtemplate.AbstractFileWebTemplate.ParserException;

public abstract class FileDataHolderFactory extends AbstractDataHolderFactory {

	private static Log log = Logging.getLog( FileDataHolderFactory.class );

	static LRUCache<File, Pair<FileWatcher, DataHolder>> storage = new LRUCache<File, Pair<FileWatcher,DataHolder>>( 10000 );
	static int reloadCount=0;

	synchronized public static DataHolder holderFor( File file )
			throws KeyException, IOException, ParserException {

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
			KeyException, ParserException {
		
		reloadCount++;

		String data = slurp( file );
		
		DataHolder holder = instantiate( data );
		holder.putParameter( "file", file.getPath() );
		
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
