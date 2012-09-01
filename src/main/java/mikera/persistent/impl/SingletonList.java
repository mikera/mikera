package mikera.persistent.impl;

import mikera.persistent.ListFactory;
import mikera.persistent.PersistentList;

/**
 * Singleton list instance
 * 
 * @author Mike Anderson
 *
 * @param <T>
 */
public final class SingletonList<T> extends BasePersistentList<T> {

	private static final long serialVersionUID = 8273587747838774580L;
	
	final T value;
	
	public static <T> SingletonList<T> create(T object) {
		return new SingletonList<>(object);
	}
	
	public int size() {
		return 1;
	}
	
	public boolean isEmpty() {
		return false;
	}
	
	public T get(int i) {
		if (i==0) return value;
		throw new IndexOutOfBoundsException();
	}
	
	private SingletonList(T object) {
		value=object;
	}
	
	public PersistentList<T> front() {
		return this;
	}
	
	public T head() {
		return value;
	}

	public PersistentList<T> back() {
		return ListFactory.emptyList();
	}
	
	public PersistentList<T> tail() {
		return ListFactory.emptyList();
	}
}
