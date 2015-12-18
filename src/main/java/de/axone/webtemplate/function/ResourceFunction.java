package de.axone.webtemplate.function;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.tools.KeyValueAccessor.ValueProvider;
import de.axone.tools.Mapper;
import de.axone.tools.S;
import de.axone.tools.Str;
import de.axone.web.Tag;
import de.axone.webtemplate.AttributeMap;
import de.axone.webtemplate.DataHolder;
import de.axone.webtemplate.Renderer.ContentCache;
import de.axone.webtemplate.form.Translator;

/**
 * Includes Resources.
 * 
 * Resources can be css or js files.
 * 
 * <em>Parameters</em>
 * 	
 * <dl>
 * 	<dt>src
 *  <dd>Semicolon separated list of resources
 *  
 * 	<dt>mode: live/dev, default: live
 *  <dd>include additional parameters for debugging. (yui=no)
 *  
 * 	<dt>cache: yes/rand/file, 'rand' is default
 *  <dd>Append nc (nocache) parameter to url or does nothing in case of cache='yes'. 
 *  'rand' will include a per server restart random value. 'file' will include the files newest timestamp. (Not implementd).
 *  This will change in future versions because ?= url parameters prevent some proxies from caching
 *  
 * 	<dt>combine: true/false, default is 'true'
 *  <dd>if true, one tag is genereated. if false multiple are generated
 *  
 * 	<dt>type: css/js
 *  <dd>the type of resource. css generates &lt;link ...&gt; tags. js generates &lt;script ...&gt; tags.
 *  
 * 	<dt>media: only for css. Default is empty aka. 'all'
 *  <dd>the type of resource. css generates &lt;link ...&gt; tags. js generates &lt;script ...&gt; tags.
 *  
 * 	<dt>base, default depends and must be set in constructor
 *  <dd>base path to prepend to all media. Handling depends on 'combine'
 * </dl>
 * 
 * <em>Examples</em>
 * 
 * <pre>
 * __resource src="main.css;print.css" media="print"__
 * </pre>
 * results in:
 * <pre>
 * &lt;link rel="stylesheet" href="/css/main.css;print.css" media="print" /&gt;
 * </pre>
 * <pre>
 * __resource src="tools.js;print/print.css" base="/static/" combine="false" mode="dev"__
 * </pre>
 * results in:
 * <pre>
 * &lt;script type="text/javascript" src="/static/main.css?yui=no&amp;nocache=32147981324" /&gt;
 * &lt;script type="text/javascript" src="/static/print/print.css?yui=no&amp;nocache=32147981324" /&gt;
 * </pre>
 * 
 * @author flo
 */
public class ResourceFunction implements Function {
	
	public static final String ATTRIBUTE_SRC = "src";
	public static final String ATTRIBUTE_RESOURCE = "resource";
	public static final String ATTRIBUTE_ID = "id";
	public static final String ATTRIBUTE_MODE = "mode";
	public static final String ATTRIBUTE_COMBINE = "combine";
	public static final String ATTRIBUTE_TYPE = "type";
	public static final String ATTRIBUTE_CACHE = "cache";
	public static final String ATTRIBUTE_MEDIA = "media";
	public static final String ATTRIBUTE_BASE = "base";
	public static final String ATTRIBUTE_SYNC = "sync";
	public static final String ATTRIBUTE_CDN = "cdn";
	
	public enum Runmode {
		dev, live;
	}
	public enum Type {
		css, js;
	}
	public enum Cache {
		yes, rand, file;
	}
	public enum Sync {
		sync, defer, async
	}
	
	private final ResourceProvider provider;
	private final Runmode mode;
	private final boolean combine;
	private final Cache cache;
	private final String media;
	private final String base;
	private final CDNProvider cdn;
	private final File fsBase;
	private final int rand;
	
	/**
	 * @param provider
	 * @param mode
	 * @param combine
	 * @param cache
	 * @param media
	 * @param cdn CDN Url to use (e.g. "//cdn.blah.com") or null
	 * @param base
	 * @param fsBase only needed for live fs ctime checks if mode==dev.
	 * @param rand a random number used for nocache
	 */
	public ResourceFunction( ResourceProvider provider, Runmode mode, boolean combine,
			Cache cache, String media, CDNProvider cdn, String base, File fsBase, int rand ){
		
		this.provider = provider;
		this.mode = mode;
		this.combine = combine;
		this.cache = cache;
		this.media = media;
		this.cdn = cdn;
		this.base = base;
		this.fsBase = fsBase;
		this.rand = rand;
	}
	
	public ResourceFunction( @Nonnull ResourceProvider provider, @Nonnull Runmode mode, boolean combine,
			@Nullable CDNProvider cdn, @Nonnull String base, @Nonnull File fsBase, int rand ){
		
		this( provider, mode, combine, Cache.file, null, cdn, base, fsBase, rand );
	}
	
	private static final char FS = ';';
	
	private long filetime( String path, String base ) throws IOException{
		
		if( path.startsWith( "//" ) ) return -1;
		
		String myPath = path;
		if( myPath.startsWith( base ) ) {
			myPath = myPath.substring( base.length() );
		}
		
		File file = new File( fsBase, myPath );
		FileTime ctime = Files.getLastModifiedTime( ( file.toPath() ) );
		return ctime.toMillis()/1000;
	}
	
	@Override
	public void render( String name , DataHolder holder , 
			PrintWriter out , HttpServletRequest request ,
			HttpServletResponse response , AttributeMap attributes , Object value , Translator translator , ContentCache cache 
	) throws Exception {
		
		if( ! holder.isRendering() ) return;
		
		// Parse Attributes
		
		String pSrc = attributes.get( ATTRIBUTE_SRC ),
		       pResource = attributes.get( ATTRIBUTE_RESOURCE );
		
		if( pSrc == null && pResource == null )
				throw new IllegalArgumentException( "Either 'src' or 'resource' must be set" );
		
		String pId = attributes.get( ATTRIBUTE_ID );
		
		Runmode pMode = attributes.getEnum( Runmode.class, ATTRIBUTE_MODE, this.mode );
		
		boolean pCombine = attributes.getBoolean( ATTRIBUTE_COMBINE, this.combine );
		
		Type pType = attributes.getEnumRequired( Type.class, ATTRIBUTE_TYPE );
		
		Cache pCache = attributes.getEnum( Cache.class, ATTRIBUTE_CACHE, this.cache );
		
		Sync sync = attributes.getEnum( Sync.class, ATTRIBUTE_SYNC, Sync.sync );
		
		String pMedia = attributes.get( ATTRIBUTE_MEDIA, this.media );
		
		String pBase = base;
		if( pBase.endsWith( "/" ) ) pBase = pBase.substring( 0, pBase.length()-2 );
		String pAddBase = attributes.get( ATTRIBUTE_BASE );
		if( pAddBase != null ){
			if( pAddBase.startsWith( "/" ) ){
				pBase = pAddBase;
			} else {
				pBase += "/" + pAddBase;
			}
		}
		
		if( pMode != Runmode.dev && ( pBase.length() < 2 || ! ( pBase.startsWith( "//" ) || pBase.startsWith( "http" ) ) ) ) {
			String lCdn = attributes.get( ATTRIBUTE_CDN, this.cdn );
			if( lCdn != null ) pBase = lCdn + pBase;
		}
		
		pBase += "/";
		
		// Split src and build complete paths of it
		
		if( pResource != null ){
			pSrc = provider.get( pResource ) + ( pSrc != null ? ";"+pSrc : "" );
		}
		
		String [] srcs = Str.splitFastAndTrim( pSrc, FS );
		
		long newest = 0;
		List<String> paths = new LinkedList<>();
		if( pCombine ){
			// find latest change
			for( String src : srcs ){
				long ft = filetime( pBase + src, base );
				if( ft > newest ) newest = ft;
			}
			paths.add( pBase + Str.join( ";", srcs ) );
		} else {
			for( String src : srcs ){
				paths.add( pBase + src );
			}
		}
		
		
		for( String path : paths ){
			
			String ext = "";
			
			char PS = '?';
			
			if( pMode == Runmode.dev ){
				ext += PS+"yui=0";
				PS='&';
			}
			
			switch( pCache ){
			case rand:
				ext += PS + "nc=" + rand;
				PS='&';
				break;
			case file:
				ext += PS + "nc=" + ( pCombine ? newest : filetime( path, base ) );
				PS='&';
				break;
			case yes:
			default:
				break;
			}
			
			// build tags according to type
			
			String tag;
			switch( pType ){
			case css: {
				
				Map<String,String> args = Mapper.treeMap(
						"rel", "stylesheet",
						"type", "text/css",
						"href", path+ext
				);
				if( pMedia != null ) args.put( "media", pMedia );
				if( pId != null ) args.put( "id", pId.trim() );
				
				tag = Tag.simple( "link", null, args );
				
			} break;
			case js:
			default: {
					
				Map<String,String> args = Mapper.treeMap(
						"type", "text/javascript",
						"src", path+ext
				);
				if( sync != Sync.sync ) args.put( sync.name(), sync.name() );
				if( pId != null ) args.put( "id", pId.trim() );
				tag = Tag.simple( "script", "", args );
			} break;
			}
			
			out.write( tag );
			out.write( S.NL );
		}
	}
	
	@FunctionalInterface
	public interface ResourceProvider {
		
		public String get( String name );
	}
	
	@FunctionalInterface
	public interface CDNProvider extends ValueProvider<String> {
		
		@Override
		public String get();
	}

}
