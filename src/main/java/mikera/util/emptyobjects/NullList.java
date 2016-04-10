package mikera.util.emptyobjects;

import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import mikera.persistent.ListFactory;
import mikera.persistent.PersistentList;
import mikera.persistent.impl.SingletonList;

public final class NullList<T> extends PersistentList<T> {
	
	private static final long serialVersionUID = -268387358134950528L;

	@SuppressWarnings("rawtypes")
	public static NullList<?> INSTANCE=new NullList();
	
	private NullList() {
		
	}

	@Override
	public PersistentList<T> append(T value) {
		return ListFactory.create(value);
	}

	@Override
	public PersistentList<T> append(PersistentList<T> value) {
		return value;
	}

	@Override
	public PersistentList<T> deleteAt(int index) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public PersistentList<T> deleteRange(int start, int end) {
		if ((start==0)&&(end==0)) return this;
		throw new IndexOutOfBoundsException();
	}

	public PersistentList<T> deleteFirst(T value) {
		return this;
	}
	
	@Override
	public PersistentList<T> deleteAll(Collection<T> values) {
		return this;
	}

	@Override
	public T get(int i) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public void add(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object o) {
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		return -1;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ListIterator<T> listIterator() {
		return (ListIterator<T>) NullIterator.INSTANCE;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ListIterator<T> listIterator(int index) {
		return (ListIterator<T>) NullIterator.INSTANCE;
	}

	@Override
	public T remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T set(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PersistentList<T> subList(int fromIndex, int toIndex) {
		if ((fromIndex!=0)||(toIndex!=0)) throw new IllegalArgumentException();
		return this;
	}

	@Override
	public int compareTo(PersistentList<T> o) {
		if (o.size()>0) return -1;
		return 0;
	}

	@Override
	public <V> V[] toArray(V[] a, int offset) {
		return null;
	}

	@Override
	public int hashCode() {
		// need to be 0 to be consistent will zero length PersistentList
		return 0;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (o instanceof List<?>) {
			return equals((List<T>)o);
		}
		return false;
	}
	
	@Override
	public boolean equals(List<T> list) {
		return list.size()==0;
	}

	@Override
	public PersistentList<T> back() {
		return this;
	}

	@Override
	public PersistentList<T> front() {
		return this;
	}

	@Override
	public int indexOf(Object value, int start) {
		return -1;
	}

	@Override
	public PersistentList<T> update(int index, T value) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public PersistentList<T> insert(int index, T value) {
		if (index!=0) throw new IndexOutOfBoundsException();
		return SingletonList.create(value);
	}

	@Override
	public PersistentList<T> insertAll(int index, Collection<T> values) {
		if (index!=0) throw new IndexOutOfBoundsException();
		return ListFactory.createFromCollection(values);
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Iterator<T> iterator() {
		return (Iterator<T>) NullIterator.INSTANCE;
	}
	
	private Object readResolve() throws ObjectStreamException {
		// needed for deserialisation to the correct static instance
		return INSTANCE;
	}

	@Override
	public PersistentList<T> copyFrom(int index, PersistentList<T> values,
			int srcIndex, int length) {
		if (length>0) throw new IndexOutOfBoundsException();
		return this;
	}
}
