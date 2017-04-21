package de.axone.webtemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.axone.data.Label;
import de.axone.tools.Stack;
import de.axone.tools.Text;
import de.axone.web.SuperURL;
import de.axone.web.SuperURLPrinter;
import de.axone.web.encoding.Encoder;
import de.axone.web.encoding.Encoder_Amp;
import de.axone.web.encoding.Encoder_Attribute;
import de.axone.web.encoding.Encoder_AttributeId;
import de.axone.web.encoding.Encoder_Html;
import de.axone.web.encoding.Encoder_Text;
import de.axone.web.encoding.Encoder_Url;
import de.axone.webtemplate.Renderer.ContentCache;
import de.axone.webtemplate.form.TKey;
import de.axone.webtemplate.form.Translator;
import de.axone.webtemplate.function.Function;

/**
 * A DataHolder encapsulates a Template file in its sliced form.
 * This class is optimized for speed. Every instance is cloned
 * so that it's variables can be replaced without disturbing other
 * threads who concurently access this Holder.
 * 
 * This class is final so that clone must not call super.clone() and
 * make stuff complicated
 *
 * @author flo
 */
public final class DataHolder implements Serializable {
	
	private static final Logger log = LoggerFactory.getLogger( DataHolder.class );
	
	private static final long serialVersionUID = 1L;
	
	public static final String PARAM_CUT = "Cut",
	                           PARAM_PROCESSOR = "Processor";
	
	public static final String P_FILE = "file",
	                           P_REAL_FILE = "realfile",
	                           P_TIMESTAMP = "timestamp",
	                           P_SOURCE = "source",
	                           P_CALLSTACK = "callstack",
	                           P_TRUE = "true",
	                           P_FALSE = "false";
	
	public static final String NOT_FOUND = "||";

	public static final String NOVAL = "";
	
	public static final SuperURLPrinter URL_PRINTER =
			SuperURLPrinter.ForAttribute;

	
	// Source
	private final String source;
	
	// Keys in use
	// (needed for 'has')
	private final Set<String> keys;
	
	// Items
	private final List<DataHolderItem> items;

	
	// Values
	private Map<String, Object> values
			= new HashMap<>();

	
	// Parameters from Template
	private final Map<String, String> systemParameters;
	
	
	// Parameters from User
	private Map<String, String> parameters
			= new HashMap<>();

	
	// Functions
	// TOOD: serialization / ?
	private FunctionFactory functions
			= new SimpleFunctionFactory();
	
	
	// isRendering Stack
	private LinkedList<Boolean> isRenderingStack
			= new LinkedList<>();
	
	
	private boolean fixed = false;
	
	private final Throwable creator;
	
	public Throwable creator(){ return creator; }

	
	DataHolder( String source ) {
		
		this.creator = new Throwable();
		
		this.source= source;
		this.keys = new HashSet<>();
		this.items = new ArrayList<>();
		this.systemParameters = new HashMap<>();
	}

	
	private DataHolder( String source,
			Set<String> keys,
			List<DataHolderItem> items,
			Map<String, String> systemParameters ) {
		
		this.creator = new Throwable();
		
		this.keys = keys;
		this.source = source;
		this.items = items;
		this.systemParameters = systemParameters;
		this.fixed = true;
	}

	
	static int functionCount = 1;
	void addValue( String key, String value, DataHolderItemType type, boolean translate )
			throws AttributeParserByHand.ParserException {
		
		if( fixed ) throw new IllegalStateException( "try to add to fixed collection" );
		
		AttributeMap attributes = AttributeMap.EMPTY;
		DataHolderEncodingType encoding = null;
		if( type == DataHolderItemType.VAR ){
			encoding = DataHolderEncodingType.matching( key );
			if( encoding.mustBeTrimmed() ){
				key = key.substring( 1, key.length()-1 );
			}
    		attributes = AttributeParserByHand.parse( key );
		}

		if( attributes.size() > 1 ){
			
			key = attributes.get( AttributeParserByHand.TAG_NAME );
		}
		
		key = vKeyForAdd( key );
		
		DataHolderItem item = new DataHolderItem( key, type, encoding, translate, attributes, value );
		
		keys.add( key );
		items.add( item );
	}
	
	
	public void fixValues(){
		fixed = true;
	}
	
	/**
	 * set value using an value provider
	 * 
	 * This method is sugar in order to make closures work naturally
	 * 
	 * @param key
	 * @param value
	 * @return self
	 */
	public DataHolder setValue( String key, ValueProvider value ) {
		
		setValue( key, (Object)value );
		
		return this;
	}
	
	
	/**
	 * Set value
	 * 
	 * @param key
	 * @param value
	 * @return self
	 */
	public DataHolder setValue( String key, Object value ) {
		
		key = vKey( key );
		
		if( value == null ) value = NOVAL;
		
		values.put( key, value );
		
		return this;
	}

	
	/**
	 * Set more than one value
	 * 
	 * @param basename
	 * @param values
	 * @return self
	 */
	public DataHolder setValues( String basename, Map<String,? extends Object> values ) {
		
		basename = basename == null ? null: vKey( basename );

		for( Map.Entry<String,? extends Object> entry : values.entrySet() ){
			
			String key = entry.getKey();

			String name;
			if( basename == null ) {
				name = key;
			} else {
				if( key.length() > 0 ) name = basename+"_"+key;
				else name = basename;
			}
			setValue( name, entry.getValue() );
		}
		
		return this;
	}
	
	/**
	 * Fill from templates parameters
	 * @param parameters 
	 * @Deprecated --> Remove this shit
	 */
	public void autofill( Map<String,? extends Object> parameters ) {
		
		for( Map.Entry<String,? extends Object> entry : parameters.entrySet() ){
			
			String key = entry.getKey();
			
			setValue( key, entry.getValue() );
		}
		
	}

	
	public DataHolder setFunctionFactory( FunctionFactory factory ){
		this.functions = factory;
		return this;
	}
	
	
	public DataHolder setFunction( String key, Function function ) {
		functions.add( vKey( key ), function );
		return this;
	}
	
	
	public boolean hasValue( String key ){
		
		// Value
		Object value = values.get( vKey( key ) );
		
		if( value != null && value != NOVAL ) {
			
			if( value instanceof String ){
				return ((String)value).length() > 0;
			}
			
			return true;
		}
		return functions.has( vKey( key ) );
	}

	
	@Deprecated
	public DataHolderItem getItem( String key ) throws KeyException {
		
		String vKey = vKey( key );
		
		return items.stream()
				.filter( item -> item.getName().equals( vKey ) )
				.findFirst()
				.get();
	}

	
	public boolean has( String key ) {
		
		return keys.contains( vKey( key ) );
	}
	
	
	public Object getValue( String key ) {
		
		return values.get( vKey( key ) );
	}


	// For tests
	Set<String> getKeys() {
		
		return Collections.unmodifiableSet( keys );
	}
	
	protected String getSystemParameter( String key ){
		
		return systemParameters.get( pKey( key ) );
	}
	
	protected void setSystemParameter( String key, String value ){
		
		if( fixed ) throw new IllegalStateException( "try to add to fixed collection" );

		systemParameters.put( pKey( key ), value );
	}

	
	public DataHolder setParameter( String key, String value ) {
		
		parameters.put( pKey( key ), value );
		
		return this;
	}

	
	public String getParameter( String key ) {
		
		String result = parameters.get( pKey( key ) );
		
		if( result == null ) result = systemParameters.get( pKey( key ) );
		
		return result;
	}

	
	public DataHolder freshCopy() {
		
		return new DataHolder( source, keys, items, systemParameters );
	}
	
	
	public boolean isRendering(){
		
		return isRenderingStack.size() == 0 || isRenderingStack.getLast();
	}
	
	public void pushRendering( boolean render ){
		
		isRenderingStack.addLast( render );
	}
	
	public void toggleRendering(){
		
		if( isRenderingStack.size() == 0 ) throw new IllegalStateException( "No rendering stack" );
		
		isRenderingStack.addLast( ! isRenderingStack.removeLast() );
	}
	
	public void popRendering(){
		
		if( isRenderingStack.size() == 0 ) throw new IllegalStateException( "No rendering stack" );
		
		isRenderingStack.removeLast();
	}
	
	public void render( Object object, PrintWriter out, HttpServletRequest request,
			HttpServletResponse response, Translator translator, ContentCache cache )
	throws IOException, WebTemplateException, Exception {
		
		for( DataHolderItem item : items ) {
			
			String key = item.getName();
			
			if( key == "creator" ) {
				
				Stack.print( out, creator );
			}
			
			Function function = null;
			
			if( item.getAttributes().size() > 1 || functions.has( key ) ){
				
				try {
					function = functions.get( key );
				} catch( Throwable t ) {
					throw new RenderException( "Problem rendering in key: " + key, t );
				}
			}
			
			if( function != null ){
				
				try {
					function.render( key, this, out, request, response, item.getAttributes(), object, translator, cache );
				} catch( Throwable t ) {
					throw new RenderException( "Problem rendering in function: " + function.getClass() + " in key: " + key, t );
				}
				
			} else {
				
				Object value;
				if( item.getType() == DataHolderItemType.VAR ){
					value = values.get( key );
					if( value == null ) value = systemParameters.get( key );
					if( value == null ) value = NOT_FOUND + key + NOT_FOUND;
				} else {
					value = item.getValue();
				}
				
				if( isRendering() ){
					
					if( value != null && value instanceof ValueProvider ){
						
						value = ( (ValueProvider) value ).provide();
					}
					
					if( value != null ) {
		
						// ==== RENDERER ====
						if( value instanceof Renderer ) {
							
							if(
									cache != null
									&& value instanceof CacheableRenderer 
									&& ((CacheableRenderer)value).cacheable()
							) {
								
								CacheableRenderer renderer = (CacheableRenderer)value;
								Object cacheK = renderer.cacheKey();
								
								String cachedS = cache.fetch( cacheK,
										k -> renderer.renderToString( object, request, response, translator, cache ) );
									
								response.getWriter().write( cachedS );
									
							} else {
		
								Renderer renderer = (Renderer)value;
								
								renderer.render( object, out, request, response, translator, cache );
							}
		
						// ==== COLLECTION  ====
						} else if( value instanceof Collection<?> ) {
		
							Collection<?> collection = (Collection<?>) value;
		
							for( Object o : collection ) {
		
								Renderer renderer = (Renderer) o;
								renderer.render( object, out, request, response, translator, cache );
							}
		
						// ==== SUPERURL  ====
						} else if( value instanceof SuperURL ){
							
							URL_PRINTER.write( out, (SuperURL)value );
							
						// === Labels are mostly enum constants implementing this
						} else if( value instanceof Label ){
							
							out.write( ((Label)value).label() );
							
						// ==== STRING / OBJECT  ====
						} else {
							
							String stringValue;
							
							if( value instanceof Translatable ){
								
								if( translator != null ) {
									stringValue = ((Translatable)value).translated( translator );
								} else {
									stringValue = ((Translatable)value).plain();
								}
								
							} else {
								
								stringValue = value.toString(); // Does nothing for String anyway
								
								if( item.isTranslate() && translator != null ){
									
									stringValue = translator.translate( TKey.dynamic( stringValue ) );
								}
							}
							
							if( item.encoding != null && item.encoding.encoder != null ){
				    			stringValue = item.encoding.encoder.encode( stringValue );
				    		}
							
							out.write( stringValue );
						}
					}
				}
			}
		}
	}

	
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		
		@SuppressWarnings( "resource" )
		Formatter formatter = new Formatter( builder );

		builder.append( "ITEMS:\n" );
		builder.append( Text.line( 79 ) + "\n" );
		for( DataHolderItem item : items ) {

			formatter.format( "%s: %s (%s)\n",
					item.getName(), values.get( item.getName() ), item.toString() );
		}

		builder.append( "\nPARAMETERS:\n" );
		builder.append( Text.line( 79 ) + "\n" );
		for( String key : systemParameters.keySet() ) {
			
			String name = systemParameters.get( key );
			formatter.format( "%s: %s\n", key, name );
		}
		
		builder.append( "\nUSER PARAMETERS:\n" );
		builder.append( Text.line( 79 ) + "\n" );
		for( String key : parameters.keySet() ) {
			
			String name = parameters.get( key );
			formatter.format( "%s: %s\n", key, name );
		}
		
		builder.append( "\nFUNCTIONS:\n" );
		builder.append( Text.line( 79 ) + "\n" );
		builder.append( functions.toString() );

		return builder.toString();
	}

	
	enum DataHolderItemType implements Serializable {

		TEXT, VAR;
	}

	
	static enum DataHolderEncodingType {

		none('(',')', null ),
		attribute('#','#', Encoder_Attribute.instance() ),
		attribute_id('%','%', Encoder_AttributeId.instance() ),
		amp('&','&', Encoder_Amp.instance() ),
		text('[',']', Encoder_Text.instance() ),
		url('@', '@', Encoder_Url.instance() ),
		html('{','}', Encoder_Html.instance() ),
		// Better safe than sorry:
		defaultEncoding( null, null, Encoder_Attribute.instance() );

		Character begin, end;
		Encoder encoder;

		DataHolderEncodingType( Character begin, Character end, Encoder encoder ){
			this.begin=begin;
			this.end=end;
			this.encoder = encoder;
		}
		
		static DataHolderEncodingType matching( String probe ){

			if( probe == null || probe.length() < 2 ) return defaultEncoding;

			for( DataHolderEncodingType encoding : DataHolderEncodingType.values() ){

				if(
					encoding.begin != null
					&& probe.charAt( 0 ) == encoding.begin
					&& probe.charAt( probe.length()-1 ) == encoding.end
				){
					return encoding;
				}
			}
			return defaultEncoding;
		}
		
		boolean mustBeTrimmed(){
			return begin != null;
		}
	};

	
	public static class DataHolderItem implements Serializable {

		private static final long serialVersionUID = 1L;
	
		private final String name;
		private final DataHolderItemType type;
		private final boolean translate;
		private final DataHolderEncodingType encoding;
		private final AttributeMap attributes;
		private final String value;

		public DataHolderItem(
				String name,
				DataHolderItemType type,
				DataHolderEncodingType encoding,
				boolean translate,
				AttributeMap attributes,
				String value
		) {
			this.encoding = encoding;
			this.name = name;
			this.type = type;
			this.translate = translate;
			this.attributes = attributes;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public DataHolderItemType getType() {
			return type;
		}

		public DataHolderEncodingType getEncoding() {
			return encoding;
		}

		public AttributeMap getAttributes() {
			return attributes;
		}
		
		public boolean isTranslate() {
			return translate;
		}
		
		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return name + " " + " (" + type + ")";
		}
	}

	
	// HELPER
	
	private static String vKeyForAdd( String key ){
		
		return key.toLowerCase();
	}
	
	
	private static String vKey( String key ){
		
		if( ! hasOnlyLowerCaseAndUnderscore( key ) ){
			
			log.error( "wrong key format: '{}'", key );
			key = key.toLowerCase();
		}
		
		return key;
	}
	
	
	private static String pKey( String key ){
		
		return key.toLowerCase();
	}
	
	
	private static boolean hasOnlyLowerCaseAndUnderscore( String value ){
		
		for( int i=0; i<value.length(); i++ ){
			char c = value.charAt( i );
			if( c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' ) continue;
			return false;
		}
		return true;
	}
	
	/**
	 * ValueProvider can be used instead of direct value 
	 * if the value is expensive to compute because it
	 * is only computed lazily if needed.
	 * 
	 * @author flo
	 */
	@FunctionalInterface
	public interface ValueProvider {
		
		public Object provide();
	}
	
	public class RenderException extends RuntimeException {
		
		public RenderException( String message, Throwable cause ) {
			super( message, cause );
		}
		
		@Override
		public String getMessage() {
			return super.getMessage() + " in: " + source;
		}
		
	}

}