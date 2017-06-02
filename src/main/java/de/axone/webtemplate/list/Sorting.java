package de.axone.webtemplate.list;

import java.util.Comparator;

public interface Sorting<S> {

	public String name();
	public String entity();
	public Comparator<S> comparator();
	
	public default boolean equals( Sorting<S> other ) {
		
		if( other == null ) return false;
		if( name() == null )
				throw new IllegalStateException( "Sorting " + getClass() + " has no name" );
		if( other.name() == null )
				throw new IllegalStateException( "Sorting " + other.getClass() + " has no name" );
		
		return name().equals( other.name() );
	}
	
	public interface SortProvider<T> {
		Sorting<T> sortingFor( String sortname );
	}
	
	public static final class NoSorting<X> implements Sorting<X> {
		
		private final String entity;
		
		public NoSorting( String entity ) {
			this.entity = entity;
		}

		@Override
		public String name() { return "NO_SORTING"; }

		@Override
		public Comparator<X> comparator() {
			return ( o1, o2 ) -> 0;
		}

		@Override
		public String entity() {
			return entity;
		}
	}
	
	public static class NamedSorting<X> implements Sorting<X> {
		
		private final String name;
		private final String entity;
		private final Comparator<X> comparator;
		
		NamedSorting( String name, String entity, Comparator<X> comparator ) {
			this.name = name;
			this.entity = entity;
			this.comparator = comparator;
		}

		@Override
		public String name() { return name; }
		
		@Override
		public String entity() { return entity; }

		@Override
		public Comparator<X> comparator() {
			return comparator;
		}
		
	}
	
}
