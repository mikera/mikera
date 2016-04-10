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
		return new SingletonList<T>(object);
	}
	
	@Override
	public int size() {
		return 1;
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public T get(int i) {
		if (i==0) return value;
		throw new IndexOutOfBoundsException();
	}
	
	private SingletonList(T object) {
		value=object;
	}
	
	@Override
	public PersistentList<T> front() {
		return this;
	}
	
	@Override
	public T head() {
		return value;
	}

	@Override
	public PersistentList<T> back() {
		return ListFactory.emptyList();
	}
	
	@Override
	public PersistentList<T> tail() {
		return ListFactory.emptyList();
	}
}
