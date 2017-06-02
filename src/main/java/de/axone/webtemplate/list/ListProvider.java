package de.axone.webtemplate.list;

import java.util.Collections;
import java.util.List;


public interface ListProvider<T> {

	public int getTotalCount();
	
	public Iterable<T> getList( int beginIndex, int count, Sorting<T> sort );
	
	// Convenience Wrapper
	public static class Wrapper<T> implements ListProvider<T> {
		
		private final List<T> wrapped;
		
		public Wrapper( List<T> wrapped ) {
			this.wrapped = wrapped;
		}

		@Override
		public int getTotalCount() {
			return wrapped.size();
		}

		@Override
		public Iterable<T> getList( int beginIndex, int count, Sorting<T> sort ) {
			
			int toIndex = beginIndex+count;
			if( toIndex > wrapped.size() ) toIndex = wrapped.size();
			
			List<T> result =  wrapped.subList( beginIndex, toIndex );
			
			if( sort != null ) Collections.sort( result, sort.comparator() );
			
			return result;
		}
		
	}
}
