package de.axone.webtemplate.list;


// TODO: DAs auf eine weniger Resourcen verbrauchende Art & Weise lösen.
public interface ListProvider<T> {

	public int getTotalCount();
	
	public Iterable<T> getList( int beginIndex, int count, String sort );
}
