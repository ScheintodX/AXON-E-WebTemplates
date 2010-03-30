package de.axone.webtemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import de.axone.logging.Log;
import de.axone.logging.Logging;
import de.axone.tools.FileWatcher;
import de.axone.webtemplate.AbstractFileWebTemplate.ParserException;
import de.axone.webtemplate.DataHolder.DataHolderItemType;

public abstract class DataHolderFactory {

	private static Log log = Logging.getLog( DataHolderFactory.class );

	static Map<File, DataHolder> storage = new HashMap<File, DataHolder>();
	static Map<File, FileWatcher> watchers = new HashMap<File, FileWatcher>();
	static int reloadCount=0;

	synchronized public static DataHolder holderFor( File file )
			throws KeyException, IOException, ParserException {

		FileWatcher watcher;
		if( !watchers.containsKey( file ) ) {

			watcher = new FileWatcher( file );
			watchers.put( file, watcher );
		} else {
			watcher = watchers.get( file );
		}

		DataHolder result = null;
		if( !watcher.hasChanged() ) {
			result = storage.get( file );
		} else {
			result = instantiate( file );
			storage.put( file, result );
		}

		return result.clone();
	}

	static DataHolder instantiate( File file ) throws IOException,
			KeyException, ParserException {
		
		reloadCount++;

		String data = slurp( file );

		DataHolder holder = new DataHolder();
		holder.putParameter( "file", file.getPath() );

		// Replace fucking dos newlines with unix ones
		data = data.replace( "\r\n", "\n" );

		// Header
		BufferedReader in = new BufferedReader( new StringReader( data ) );
		String line = null;
		int count=0;
		while( ( line = in.readLine() ) != null ){

			// Skip empty lines
			String trimmed = line.trim();
			if( trimmed.length() == 0 ){
				count += line.length()+1;
				continue;
			}

			if( ! trimmed.startsWith( "@" ) ) break; // Quit on first not header line

			trimmed = trimmed.substring( 1 ); // remove the @

			int indexOfSep = trimmed.indexOf( ':' );
			//String[] parts = trimmed.split( "\\s*:\\s*" ); // Split on :
			String key = trimmed.substring( 0, indexOfSep ).trim().toLowerCase();
			String value = trimmed.substring( indexOfSep+1 ).trim();

			// Store
			holder.putParameter( key, value );

			// Count header chars
			count += line.length() +1; // +1 for NL
		}

		// Remove header
		data = data.substring( count );
		data = data.trim();

		// Parse template for variables
		boolean translate = false;
		String translationParts[] = data.split( "@@@" );

		int c=1;
		for( int t = 0; t < translationParts.length; t++ ){

			String translationPart = translationParts[ t ];

    		String parts[] = translationPart.split( "__" );

    		for( int i = 0; i < parts.length; i++ ) {

    			String part = parts[ i ];

    			if( i % 2 == 0 ) {
    				if( part.length() > 0 ){
        				holder.addValue( "text" + c++, part,
        						DataHolderItemType.TEXT, translate );
    				}
    			} else {
    				holder.addValue( part, "__" + part + "__",
    						DataHolderItemType.VAR, translate );
    			}
    		}

    		translate = !translate;
		}

		log.trace( "DataHolder for " + file + " created" );

		return holder;
	}

	private static String slurp( File file ) throws IOException {

		// This is to use utf-8 encoding. FileReader can't do so.
		FileInputStream fin = new FileInputStream( file );
		InputStreamReader in = new InputStreamReader( fin, "utf-8" );

		char[] buffer = new char[1024];
		StringBuilder builder = new StringBuilder();

		int len;
		while( ( len = in.read( buffer ) ) > 0 ) {

			builder.append( buffer, 0, len );
		}
		in.close();

		return builder.toString();
	}

}
