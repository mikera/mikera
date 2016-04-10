package mikera.util.emptyobjects;

import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.Iterator;

import mikera.persistent.PersistentSet;
import mikera.persistent.SetFactory;

public final class NullSet<T> extends PersistentSet<T> {
	private static final long serialVersionUID = -6170277533575154354L;
	
	@SuppressWarnings("rawtypes")
	public static NullSet<?> INSTANCE=new NullSet();
	
	private NullSet() {
		
	}
	
	@Override
	public boolean contains(Object t) {
		return false;
	}

	@Override
	public PersistentSet<T> include(T value) {
		return SetFactory.create(value);
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
	
	@Override
	public PersistentSet<T> delete(final T value) {
		return this;
	}

	@Override
	public PersistentSet<T> deleteAll(final Collection<T> values) {
		return this;
	}
	
	@Override
	public PersistentSet<T> includeAll(final Collection<T> values) {
		return SetFactory.createFrom(values);
	}
	
	public PersistentSet<T> include(final PersistentSet<T> values) {
		return SetFactory.create(values);
	}
	
	private Object readResolve() throws ObjectStreamException {
		// needed for deserialisation to the correct static instance
		return INSTANCE;
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
}
