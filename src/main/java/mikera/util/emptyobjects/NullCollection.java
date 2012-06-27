package mikera.util.emptyobjects;

import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.Iterator;

import mikera.persistent.PersistentCollection;
import mikera.persistent.impl.SingletonList;

public final class NullCollection<T> extends PersistentCollection<T> {
	private static final long serialVersionUID = 2925953822669265599L;

	@SuppressWarnings("rawtypes")
	public static NullCollection<?> INSTANCE=new NullCollection();
	
	protected NullCollection() {
		
	}
	
	public boolean add(T e) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean contains(Object o) {
		return false;
	}

	public boolean containsAll(Collection<?> c) {
		if (c.isEmpty()) return true;
		return false;
	}

	public boolean isEmpty() {
		return true;
	}

	@SuppressWarnings("unchecked")
	public Iterator<T> iterator() {
		return (Iterator<T>)NullIterator.INSTANCE;
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return 0;
	}

	public Object[] toArray() {
		return NullArrays.NULL_OBJECTS;
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	public <T> T[] toArray(T[] a) {
		return (T[])toArray();
	}

	public PersistentCollection<T> delete(T value) {
		return this;
	}

	public PersistentCollection<T> deleteAll(Collection<T> values) {
		return this;
	}
	
	public PersistentCollection<T> clone() {
		return this;
	}

	public int hashCode() {
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object a) {
		return ((a instanceof PersistentCollection<?>)&&((PersistentCollection<T>)a).isEmpty());
	}

	private Object readResolve() throws ObjectStreamException {
		// needed for deserialisation to the correct static instance
		return INSTANCE;
	}

	@Override
	public PersistentCollection<T> include(T value) {
		return SingletonList.create(value);
	}
}
