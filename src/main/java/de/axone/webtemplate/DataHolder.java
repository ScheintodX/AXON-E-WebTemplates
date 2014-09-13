package de.axone.webtemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.axone.cache.ng.CacheProvider;
import de.axone.data.Label;
import de.axone.tools.Text;
import de.axone.web.SuperURL;
import de.axone.web.SuperURL.FinalEncoding;
import de.axone.web.SuperURLPrinter;
import de.axone.web.encoding.Encoder;
import de.axone.web.encoding.Encoder_Amp;
import de.axone.web.encoding.Encoder_Attribute;
import de.axone.web.encoding.Encoder_Html;
import de.axone.web.encoding.Encoder_Text;
import de.axone.web.encoding.Encoder_Url;
import de.axone.webtemplate.AbstractFileWebTemplate.ParserException;
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
	
	private static final long serialVersionUID = 1L;
	
	public static final String PARAM_FILE = "file";
	public static final String PARAM_TIMESTAMP = "timestamp";
	public static final String PARAM_SOURCE = "source";
	public static final String PARAM_CUT = "Cut";

	public static final String NOVAL = "";
	
	public static final SuperURLPrinter URL_PRINTER = SuperURLPrinter.MinimalEncoded
			.finishFor( FinalEncoding.Attribute );
	
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
	
	// TODO: serialization / Der sollte eigentlich wo anders hin.
	private CacheProvider<String,String> contentCache;
	
	DataHolder() {
		this.keys = new LinkedList<DataHolderKey>();
		this.values = new HashMap<String, DataHolderItem>();
		this.parameters = new HashMap<String, String>();
		this.functions = new SimpleFunctionFactory();
	}

	private DataHolder(LinkedList<DataHolderKey> keys,
			HashMap<String, DataHolderItem> data,
			HashMap<String, String> parameters,
			CacheProvider<String,String> cache ) {

		this.keys = keys;
		this.values = data;
		this.parameters = parameters;
		this.functions = new SimpleFunctionFactory();
		this.contentCache = cache;
	}

	List<DataHolderKey> getVariables() {

		return keys;
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

    		attributes = AttributeParser.parse( key );
		}

		DataHolderKey dhKey;

		if( attributes != null && attributes.size() > 1 ){
			key = attributes.get( AttributeParser.TAG_NAME ).asString();
			dhKey = new DataHolderKey( key, attributes );
		} else {
			dhKey = new DataHolderKey( key, null );
		}

		keys.add( dhKey );

		if( !values.containsKey( key ) ) {

			values.put( dhKey.getName(), new DataHolderItem( key, value, type, encoding, null, translate ) );
		}
	}

	public void setValue( String key, Object value ) {
		
		key = key.toLowerCase();

		if( values.containsKey( key ) ) {
			values.get( key ).setValue( value );
		}
	}

	public void setValues( String basename, Map<String, String> values ) {
		
		basename = basename.toLowerCase();

		for( Map.Entry<String,? extends Object> entry : values.entrySet() ){
			
			String key = entry.getKey().toLowerCase();

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
	public void setCacheProvider( CacheProvider<String,String> cache ){
		this.contentCache = cache;
	}

	public void setFunction( String key, Function function ) {
		functions.add( key, function );
	}

	public DataHolderItem getItem( String key ) throws KeyException {

		if( values.containsKey( key ) ) {
			return values.get( key );
		} else
			throw new KeyException( "Doesn't contain: " + key );
	}

	public void setParameter( String key, String value ) {
		parameters.put( key.toLowerCase(), value );
	}

	public String getParameter( String key ) {
		return parameters.get( key.toLowerCase() );
	}

	public boolean has( String key ) {
		return values.containsKey( key );
	}

	public Set<String> getKeys() {
		return values.keySet();
	}

	public List<DataHolderKey> getFullKeys(){
		return keys;
	}

	public void clear() {
		for( String key : getKeys() ) {

			try {
				DataHolderItem item = getItem( key );

				if( item.getType() == DataHolderItemType.VAR )
					item.setValue( null );

			} catch( KeyException e ) {
				e.printStackTrace(); // Never happens
			}
		}
	}

	public DataHolder freshCopy() {
		
		LinkedList<DataHolderKey> cloneKeys = new LinkedList<DataHolderKey>( keys );
		
		HashMap<String, DataHolderItem> cloneData = new HashMap<String, DataHolderItem>();
		for( String key : values.keySet() ) {
			cloneData.put( key, values.get( key ).freshCopy() );
		}
		
		HashMap<String, String> cloneParameters = new HashMap<String, String>( parameters );

		DataHolder clone = new DataHolder(
				cloneKeys, cloneData, cloneParameters, contentCache );

		return clone;
	}
	
	private boolean rendering = true;
	public boolean isRendering(){
		return rendering;
	}
	public void setRendering( boolean render ){
		this.rendering = render;
	}

	public void render( Object object, PrintWriter out, HttpServletRequest request,
			HttpServletResponse response, Translator translator ) throws IOException, WebTemplateException, Exception {
		
		for( DataHolderKey key : keys ) {

			DataHolderItem item = values.get( key.getName() );
			Object value = item.getValue();

			Function function = null;
			String functionName;
			if( key.getAttributes() != null ){
				functionName = key.getAttributes().getAsStringRequired( AttributeParser.TAG_NAME );
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
    				function.render( functionName, this, out, request, response, key.getAttributes(), object, translator );
				} else {
					function.render( functionName, this, out, request, response, new AttributeMap(), object, translator );
				}
			} else if( value != null && rendering ) {

				// ==== RENDERER ====
				if( value instanceof Renderer ) {
					
					CacheableRenderer renderer;
					if(
							value instanceof CacheableRenderer 
							&& contentCache != null
							&& (renderer=(CacheableRenderer)value).cacheable()
					) {
						
						String cacheK = renderer.cacheKey();
						String cachedS = contentCache.getCache().fetch( cacheK );
						
						if( cachedS == null ){
							StringWriter s = new StringWriter();
							renderer.render( object, new PrintWriter( s ), request, response, translator );
							cachedS = s.toString();
							contentCache.getCache().put( cacheK, cachedS );
						}
						response.getWriter().write( cachedS );
							
					} else {

						((Renderer)value).render( object, out, request, response, translator );
					}

				// ==== COLLECTION  ====
				} else if( value instanceof Collection<?> ) {

					Collection<?> collection = (Collection<?>) value;

					for( Object o : collection ) {

						Renderer renderer = (Renderer) o;
						renderer.render( object, out, request, response, translator );
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
			this.name = name.toLowerCase();
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

}