package mikera.persistent;

import java.io.Serializable;
import java.util.Collection;

public interface IPersistentCollection<T> extends Collection<T>, Cloneable, Serializable {

	// include methods
	
	public PersistentCollection<T> include(final T value);
	
	public PersistentCollection<T> includeAll(final Collection<T> values);

	public PersistentCollection<T> includeAll(final PersistentCollection<T> values);

	// delete methods
	
	public PersistentCollection<T> delete(final T value);
	
	public PersistentCollection<T> deleteAll(final Collection<T> values);

	public PersistentCollection<T> deleteAll(final PersistentCollection<T> values);

	// query methods
	
	public boolean contains(Object o);

	public boolean containsAll(Collection<?> c);
	
	public boolean containsAny(Collection<?> c);
	
	public boolean isEmpty();
	
	// testing methods
	
	public void validate();
}
