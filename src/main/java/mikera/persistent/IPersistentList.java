package mikera.persistent;

import java.util.Collection;
import java.util.List;

public interface IPersistentList<T> extends IPersistentCollection<T>, List<T>, Comparable<PersistentList<T>> {
	
	// modifier methods
	
	public PersistentList<T> append(PersistentList<T> values);

	public PersistentList<T> append(Collection<T> values);

	public PersistentList<T> insert(int index, T value);
	
	public PersistentList<T> insertAll(int index, Collection<T> values);

	public PersistentList<T> insertAll(int index, PersistentList<T> values);

	public PersistentList<T> copyFrom(int index, PersistentList<T> values, int srcIndex, int length);
	
	public PersistentList<T> delete(T value);

	public PersistentList<T> deleteAt(int index);

	public PersistentList<T> deleteRange(int startIndex, int endIndex);

	public PersistentList<T> update(int index, T value);
	
	// query methods
	
	/**
	 * Returns the first element in the list
	 */
	public T head();
	
	/**
	 * Returns the tail of the list
	 * i.e. every element except the first
	 * @return
	 */
	public PersistentList<T> tail();
	
	/**
	 * Returns the front part of the list. Not guaranteed to be exactly half,
	 * but intended to be as balanced as possible
	 * @return
	 */
	public PersistentList<T> front();

	/**
	 * Returns the back part of the list. Not guaranteed to be exactly half,
	 * but intended to be as balanced as possible
	 * @return
	 */
	public PersistentList<T> back();
	
	/**
	 * Returns a subset of the given list
	 * Can be the whole list, or an empty list
	 */
	public PersistentList<T> subList(int fromIndex, int toIndex);

	// access methods
	
	public T get(int i);
	
}
