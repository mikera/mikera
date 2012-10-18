package mikera.persistent.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import mikera.persistent.PersistentSet;
import mikera.util.Tools;
import mikera.util.emptyobjects.NullSet;

/**
 * Array based immutable set implementation
 * 
 * Not great performance - since set membership testing requires
 * full scan of array. Hence should only be used for small sets.
 * 
 * 
 * @author Mike
 *
 * @param <T>
 */
public final class ArraySet<T> extends BasePersistentSet<T> {
	private final T[] data;
	
	public static <T> ArraySet<T> createFromSet(Set<T> source) {
		return new ArraySet<T>((T[])source.toArray());
	}
	
	public static <T> ArraySet<T> createFromSet(PersistentSet<T> source) {
		if (source instanceof ArraySet<?>) return (ArraySet<T>)source;
		return new ArraySet<T>((T[])source.toArray());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> ArraySet<T> createFromValue(T source) {
		return new ArraySet<T>((T[])new Object[]{source});
	}
	
	public static <T> ArraySet<T> createFromArray(T[] source) {
		HashSet<T> hs=new HashSet<T>();
		for (int i=0; i<source.length; i++) {
			hs.add(source[i]);
		}
		return createFromSet(hs);
	}
	
	@Override public boolean contains(Object o) {
		for (T t : data) {
			if (Tools.equalsWithNulls(t, o)) return true;
		}
		return false;
	}
	
	private ArraySet(T[] newData) {
		data=newData;
	}
	
	public Iterator<T> iterator() {
		return new ArraySetIterator<T>();
	}

	public int size() {
		return data.length;
	}
	
	private int indexOf(T value) {
		for (int i=0; i<data.length; i++) {
			if (Tools.equalsWithNulls(value,data[i])) return i;
		}
		return -1;
	}
	
	private class ArraySetIterator<K> implements Iterator<K> {
		private int pos=0;
		
		public boolean hasNext() {
			return pos<data.length;
		}

		@SuppressWarnings("unchecked")
		public K next() {
			if (pos>=data.length) throw new NoSuchElementException();
			return (K)data[pos++];
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	private static final long serialVersionUID = -3125683703717134995L;

	@SuppressWarnings("unchecked")
	@Override
	public PersistentSet<T> include(T value) {
		if (contains(value)) return this;
		
		T[] ndata=(T[])new Object[data.length+1];
		System.arraycopy(data, 0, ndata, 0, data.length);
		ndata[data.length]=value;
		return new ArraySet<T>(ndata);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PersistentSet<T> delete(T value) {
		int pos=indexOf(value);
		if (pos<0) return this;
		int size=data.length;
		if (size<=2) {
			if (size==2) return SingletonSet.create(data[1-pos]);
			return (PersistentSet<T>) NullSet.INSTANCE;
		}
		T[] ndata=(T[])new Object[size-1];
		System.arraycopy(data, 0, ndata, 0, pos);
		System.arraycopy(data, pos+1, ndata, pos, size-pos-1);
		return createFromArray(ndata);
	}

}
