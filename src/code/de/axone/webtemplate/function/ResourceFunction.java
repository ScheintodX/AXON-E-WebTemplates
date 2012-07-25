package de.axone.webtemplate.function;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.tools.EasyParser;
import de.axone.tools.Mapper;
import de.axone.tools.S;
import de.axone.tools.Str;
import de.axone.web.Tag;
import de.axone.webtemplate.AttributeMap;
import de.axone.webtemplate.DataHolder;
import de.axone.webtemplate.form.Translator;

/**
 * Includes Resources.
 * 
 * Resources can be css or js files.
 * 
 * <h4>Parameters</h4>
 * 	
 * <dl>
 * 	<dt>src
 *  <dd>Semicolon separated list of resources
 *  
 * 	<dt>mode: live/dev, default: live
 *  <dd>include additional parameters for debugging. (yui=no)
 *  
 * 	<dt>cache: yes/rand/file, 'rand' is default
 *  <dd>include additional parameters for debugging. (yui=no)
 *  
 * 	<dt>compress: true/false, defaults are 'true' for live and 'false' for dev mode
 *  <dd>if true, one tag is genereated. if false multiple are generated
 *  
 * 	<dt>type: css/js
 *  <dd>the type of resource. css generates &lt;link ...&gt; tags. js generates &lt;script ...&gt; tags.
 *  
 * 	<dt>media: only for css. Default is empty aka. 'all'
 *  <dd>the type of resource. css generates &lt;link ...&gt; tags. js generates &lt;script ...&gt; tags.
 *  
 * 	<dt>base, default depends and must be set in constructor
 *  <dd>base path to prepend to all media. Handling depends on 'compress'
 * </dl>
 * 
 * <h4>Examples</h4>
 * 
 * <pre>
 * __resource src="main.css;print.css" media="print"__
 * </pre>
 * results in:
 * <pre>
 * &lt;link rel="stylesheet" href="/css/main.css;print.css" media="print" /&gt;
 * </pre>
 * <pre>
 * __resource src="tools.js;print/print.css" base="/static/" compress="false" mode="dev"__
 * </pre>
 * results in:
 * <pre>
 * &lt;script type="text/javascript" src="/static/main.css?yui=no&nocache=32147981324" /&gt;
 * &lt;script type="text/javascript" src="/static/print/print.css?yui=no&nocache=32147981324" /&gt;
 * </pre>
 * 
 * @author flo
 *
 */
public class ResourceFunction implements Function {
	
	public static final String ATTRIBUTE_SRC = "src";
	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_MODE = "mode";
	public static final String ATTRIBUTE_COMPRESS = "compress";
	public static final String ATTRIBUTE_TYPE = "type";
	public static final String ATTRIBUTE_CACHE = "cache";
	public static final String ATTRIBUTE_MEDIA = "media";
	public static final String ATTRIBUTE_BASE = "base";
	
	public enum Mode {
		live, dev;
	}
	public enum Type {
		css, js;
	}
	public enum Cache {
		yes, rand, file;
	}
	
	private final Mode mode;
	private final boolean compress;
	private final Cache cache;
	private final String media;
	private final String base;
	private final File fsBase;
	private final int rand;
	
	/**
	 * 
	 * @param mode
	 * @param compress
	 * @param type
	 * @param media
	 * @param base
	 * @param fsBase only needed for live fs ctime checks if mode==dev.
	 * @param rand a random number used for nocache
	 */
	public ResourceFunction( Mode mode, boolean compress, Cache cache, String media, String base, File fsBase, int rand ){
		this.mode = mode;
		this.compress = compress;
		this.cache = cache;
		this.media = media;
		this.base = base;
		this.fsBase = fsBase;
		this.rand = rand;
	}
	
	public ResourceFunction( Mode mode, boolean compress, String base, File fsBase, int rand ){
		
		this( mode, compress, Cache.rand, null, base, fsBase, rand );
	}
	
	private static final Pattern SEPARATOR = Pattern.compile( "\\s*;\\s*" );
	
	@Override
	public void render( String name, DataHolder holder, 
			PrintWriter out, HttpServletRequest request,
			HttpServletResponse response, AttributeMap attributes, Object value, Translator translator
	) throws Exception {
		
		if( ! holder.isRendering() ) return;
		
		String tmp;
		String pSrc = attributes.getAsStringRequired( ATTRIBUTE_SRC ).trim();
		
		String pId = attributes.getAsString( ATTRIBUTE_ID );
		
		Mode pMode = this.mode;
		tmp = attributes.getAsString( ATTRIBUTE_MODE );
		if( tmp != null ) pMode = Mode.valueOf( tmp.trim() );
		
		boolean pCompress = this.compress;
		tmp = attributes.getAsString( ATTRIBUTE_COMPRESS );
		if( tmp != null ) pCompress = EasyParser.isYes( tmp.trim() );
		
		Type pType = Type.valueOf( attributes.getAsStringRequired( ATTRIBUTE_TYPE ) );
		
		Cache pCache = this.cache;
		tmp = attributes.getAsString( ATTRIBUTE_CACHE );
		if( tmp != null ) pCache = Cache.valueOf( tmp.trim() );
		
		String pMedia = attributes.getAsString( ATTRIBUTE_MEDIA, this.media );
		
		String pBase = base;
		if( pBase.endsWith( "/" ) ) pBase = pBase.substring( 0, pBase.length()-2 );
		String pAddBase = attributes.getAsString( ATTRIBUTE_BASE );
		if( pAddBase != null ){
			if( pAddBase.startsWith( "/" ) ){
				pBase = pAddBase;
			} else {
				pBase += "/" + pAddBase;
			}
		}
		
		pBase += "/";
		
		
		List<String> srcs = Arrays.asList( SEPARATOR.split( pSrc ) );
		
		List<String> paths = new LinkedList<>();
		if( pCompress ){
			paths.add( pBase + Str.join( ";", srcs ) );
		} else {
			for( String src : srcs ){
				paths.add( pBase + src );
			}
		}
		
		for( String path : paths ){
			
			char PS = '?';
			
			if( pMode == Mode.dev ){
				path += PS+"yui=0";
				PS='&';
			}
			
			switch( pCache ){
			case rand:
				path += PS + "nc=" + rand;
				PS='&';
				break;
			case file:
				FileTime ctime = Files.getLastModifiedTime( ( new File( fsBase, pBase + path ).toPath() ) );
				path += "" + PS + ctime.toMillis()/1000;
				PS='&';
				break;
			case yes:
			default:
				break;
			}
			
			String tag;
			switch( pType ){
			case css: {
				
				Map<String,String> args = Mapper.treeMap(
						"rel", "stylesheet",
						"type", "text/css",
						"href", path
				);
				if( pMedia != null ) args.put( "media", pMedia );
				if( pId != null ) args.put( "id", pId.trim() );
				
				tag = Tag.simple( "link", null, args );
				
			} break;
			case js:
			default: {
					
				Map<String,String> args = Mapper.treeMap(
						"type", "text/javascript",
						"src", path
				);
				if( pId != null ) args.put( "id", pId.trim() );
				tag = Tag.simple( "script", "", args );
			} break;
			}
			
			out.write( tag );
			out.write( S.NL );
		}
	}

}
