package de.axone.webtemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.axone.data.Label;
import de.axone.tools.Text;
import de.axone.web.SuperURL;
import de.axone.web.SuperURLPrinter;
import de.axone.web.encoding.Encoder;
import de.axone.web.encoding.Encoder_Amp;
import de.axone.web.encoding.Encoder_Attribute;
import de.axone.web.encoding.Encoder_Html;
import de.axone.web.encoding.Encoder_Text;
import de.axone.web.encoding.Encoder_Url;
import de.axone.webtemplate.AbstractFileWebTemplate.ParserException;
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
 * TODO: Die Sache mit dem HolderKey umbauen so dass die Attribute im DataHoderItem landen.
 */
public final class DataHolder implements Serializable {
	
	private static final Logger log = LoggerFactory.getLogger( DataHolder.class );
	
	private static final long serialVersionUID = 1L;
	
	public static final String PARAM_CUT = "Cut",
	                           PARAM_PROCESSOR = "Processor";
	
	public static final String P_FILE = "file",
	                           P_TIMESTAMP = "timestamp",
	                           P_SOURCE = "source";

	public static final String NOVAL = "";
	
	public static final SuperURLPrinter URL_PRINTER =
			SuperURLPrinter.ForAttribute;
	
	// List in proper order
	private LinkedList<DataHolderKey> keys;

	// Values
	private HashMap<String, DataHolderItem> values;

	// Parameters
	private HashMap<String, String> parameters;

	// Functions
	//private HashMap<String, Function> functions;
	// TOOD: serialization / ?
	private FunctionFactory functions;
	
	DataHolder() {
		this.keys = new LinkedList<DataHolderKey>();
		this.values = new HashMap<String, DataHolderItem>();
		this.parameters = new HashMap<String, String>();
		this.functions = new SimpleFunctionFactory();
	}

	private DataHolder(LinkedList<DataHolderKey> keys,
			HashMap<String, DataHolderItem> data,
			HashMap<String, String> parameters ) {

		this.keys = keys;
		this.values = data;
		this.parameters = parameters;
		this.functions = new SimpleFunctionFactory();
	}

	static int functionCount = 1;
	void addValue( String key, String value, DataHolderItemType type, boolean translate )
			throws ParserException {
		
		AttributeMap attributes = null;
		DataHolderEncodingType encoding = null;
		if( type == DataHolderItemType.VAR ){
			encoding = DataHolderEncodingType.matching( key );
			if( encoding.mustBeTrimmed() ){
				key = key.substring( 1, key.length()-1 );
			}

    		attributes = AttributeParserByHand.parse( key );
		}

		DataHolderKey dhKey;

		if( attributes != null && attributes.size() > 1 ){
			key = attributes.get( AttributeParserByHand.TAG_NAME );
			dhKey = new DataHolderKey( key, attributes );
		} else {
			dhKey = new DataHolderKey( key, null );
		}
		
		key = vKeyForAdd( key );

		keys.add( dhKey );

		if( ! values.containsKey( key ) ) {

			values.put( dhKey.getName(), new DataHolderItem( key, value, type, encoding, null, translate ) );
		}
	}

	public void setValue( String key, ValueProvider value ) {
		setValue( key, (Object)value );
	}
	
	public void setValue( String key, Object value ) {
		
		key = vKey( key );

		if( values.containsKey( key ) ) {
			values.get( key ).setValue( value );
		}
	}

	public void setValues( String basename, Map<String,? extends Object> values ) {
		
		basename = vKey( basename );

		for( Map.Entry<String,? extends Object> entry : values.entrySet() ){
			
			String key = vKey( entry.getKey() );

			String name;
			if( basename == null ) {
				name = key;
			} else {
				if( key.length() > 0 ) name = basename+"_"+key;
				else name = basename;
			}
			setValue( name, entry.getValue() );
		}
	}
	
	public void setFunctionFactory( FunctionFactory factory ){
		this.functions = factory;
	}
	public void setFunction( String key, Function function ) {
		functions.add( vKey( key ), function );
	}

	public DataHolderItem getItem( String key ) throws KeyException {
		
		key = vKey( key );

		if( values.containsKey( key ) ) {
			return values.get( key );
		} else
			throw new KeyException( "Doesn't contain: " + key );
	}

	public boolean has( String key ) {
		return values.containsKey( vKey( key ) );
	}

	public Set<String> getKeys() {
		return values.keySet();
	}

	public List<DataHolderKey> getFullKeys(){
		return keys;
	}

	public void setParameter( String key, String value ) {
		parameters.put( pKey( key ), value );
	}

	public String getParameter( String key ) {
		return parameters.get( pKey( key ) );
	}

	public DataHolder freshCopy() {
		
		LinkedList<DataHolderKey> cloneKeys = new LinkedList<DataHolderKey>( keys );
		
		HashMap<String, DataHolderItem> cloneData = new HashMap<String, DataHolderItem>();
		for( String key : values.keySet() ) {
			cloneData.put( key, values.get( key ).freshCopy() );
		}
		
		HashMap<String, String> cloneParameters = new HashMap<String, String>( parameters );

		DataHolder clone = new DataHolder(
				cloneKeys, cloneData, cloneParameters );

		return clone;
	}
	
	private LinkedList<Boolean> renderingQueue = new LinkedList<>();

	public boolean isRendering(){
		return renderingQueue.size() == 0 || renderingQueue.getLast();
	}
	public void pushRendering( boolean render ){
		renderingQueue.addLast( render );
	}
	public void toggleRendering(){
		if( renderingQueue.size() > 0 )
				renderingQueue.addLast( ! renderingQueue.removeLast() );
	}
	public void popRendering(){
		if( renderingQueue.size() > 0 )
				renderingQueue.removeLast();
	}

	public void render( Object object, PrintWriter out, HttpServletRequest request,
			HttpServletResponse response, Translator translator, ContentCache cache )
	throws IOException, WebTemplateException, Exception {
		
		for( DataHolderKey key : keys ) {

			Function function = null;
			String functionName;
			if( key.getAttributes() != null ){
				functionName = key.getAttributes().getRequired( AttributeParserByHand.TAG_NAME );
			} else {
				functionName = key.getName();
			}
			if( functions.has( functionName ) ){
				function = functions.get( functionName );
			}
			if( key.getAttributes() != null ){
    			function = functions.get( functionName );
			}
			if( function != null ){
				if( key.getAttributes() != null ){
    				function.render( functionName, this, out, request, response, key.getAttributes(), object, translator, cache );
				} else {
					function.render( functionName, this, out, request, response, AttributeMap.EMPTY, object, translator, cache );
				}
			} else {
				
				DataHolderItem item = values.get( key.getName() );
				Object value = item.getValue();
				
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

		builder.append( "KEYS:\n" );
		builder.append( Text.line( 79 ) + "\n" );
		for( DataHolderKey key : keys ) {

			formatter.format( "%s\n", key );
		}

		builder.append( "\nPARAMETERS:\n" );
		builder.append( Text.line( 79 ) + "\n" );
		for( String key : parameters.keySet() ) {
			
			String name = parameters.get( key );
			formatter.format( "%s: %s\n", key, name );
		}
		
		builder.append( "\nVALUES:\n" );
		builder.append( Text.line( 79 ) + "\n" );
		for( String key : values.keySet() ){
			
			DataHolderItem item = values.get( key );
			formatter.format( "%s: %s\n", key, item );
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
	
		private String name;
		private Object value;
		private DataHolderItemType type;
		private boolean translate;
		private DataHolderEncodingType encoding;
		private HashMap<String,Object> attributes;

		public DataHolderItem(String name, Object value,
				DataHolderItemType type, DataHolderEncodingType encoding, HashMap<String,Object> attributes , boolean translate) {

			setEncoding( encoding );
			setName( name );
			setValue( value );
			setType( type );
			setAttributes( attributes );
			setTranslate( translate );
		}

		public String getName() {
			return name;
		}

		public void setName( String name ) {
			this.name = name;
		}

		public Object getValue() {
			return value;
		}

		public void setValue( Object value ) {
			this.value = value;
		}

		public DataHolderItemType getType() {
			return type;
		}

		public void setType( DataHolderItemType type ) {
			this.type = type;
		}

		public DataHolderEncodingType getEncoding() {
			return encoding;
		}

		public void setEncoding( DataHolderEncodingType encoding ) {
			this.encoding = encoding;
		}

		public HashMap<String, Object> getAttributes() {
			return attributes;
		}

		public void setAttributes( HashMap<String, Object> attributes ) {
			this.attributes = attributes;
		}

		public boolean isTranslate() {
			return translate;
		}

		public void setTranslate( boolean translate ) {
			this.translate = translate;
		}

		@Override
		public String toString() {
			return name + ": " + value + " (" + type + ")";
		}

		public DataHolderItem freshCopy() {

			return new DataHolderItem( name, value, type, encoding, attributes, translate );
		}
	}

	public static class DataHolderKey implements Serializable {

		private static final long serialVersionUID = 1L;
		
		String name;
		AttributeMap attributes;

		DataHolderKey( String name, AttributeMap attributes ){
			this.name = vKey( name );
			this.attributes = attributes;
		}

		@Override
		public int hashCode(){

			int hashCode = name.hashCode();

			if( attributes != null ){
				hashCode ^= attributes.hashCode();
			}

			return hashCode;
		}

		public String getName() {
			return name;
		}

		public AttributeMap getAttributes() {
			return attributes;
		}

		@Override
		public String toString() {
			return "DataHolderKey [name=" + name + ", attributes=" + attributes + "]";
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
	
	/*
	private static boolean hasOnlyAsciiLetterAndUnderscore( String value ){
		
		for( int i=0; i<value.length(); i++ ){
			char c = value.charAt( i );
			if( c >= 'a' && c <= 'z' || c>= 'A' && c <= 'Z' || c == '_' ) continue;
			return false;
		}
		return true;
	}
	*/
	
	/**
	 * ValueProvider can be used instead of direct value 
	 * if the value is expensive to compute because it
	 * is only computed lazily if needed.
	 * 
	 * @author flo
	 *
	 */
	@FunctionalInterface
	public interface ValueProvider {
		public Object provide();
	}

}