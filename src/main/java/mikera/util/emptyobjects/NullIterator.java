package mikera.util.emptyobjects;

import java.io.ObjectStreamException;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import mikera.annotations.Immutable;

@Immutable
public final class NullIterator<T> implements ListIterator<T> {
	
	@SuppressWarnings({ "rawtypes" })
	public static NullIterator<?> INSTANCE= new NullIterator();
	
	private NullIterator() {
		
	}
	
	public boolean hasNext() {
		return false;
	}

	public T next() {
		throw new NoSuchElementException();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public void add(T e) {
		throw new UnsupportedOperationException();
	}

	public boolean hasPrevious() {
		return false;
	}

	public int nextIndex() {
		return 0;
	}

	public T previous() {
		throw new NoSuchElementException();
	}

	public int previousIndex() {
		return -1;
	}

	public void set(T e) {
		throw new UnsupportedOperationException();
	}

	private Object readResolve() throws ObjectStreamException {
		// needed for deserialisation to the correct static instance
		return INSTANCE;
	}
}
