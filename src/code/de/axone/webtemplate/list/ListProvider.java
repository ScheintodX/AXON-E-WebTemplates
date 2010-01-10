package de.axone.webtemplate.list;


public interface ListProvider<T> {

	public int getTotalCount();
	
	public Iterable<T> getList( int beginIndex, int count, String sort );
}
