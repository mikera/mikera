package mikera.persistent;

import java.util.Collection;
import java.util.Set;

public interface IPersistentSet<T> extends Set<T> {
	// delete methods
	
	public PersistentSet<T> delete(final T value);
	
	public PersistentSet<T> deleteAll(final Collection<T> values);

	public PersistentSet<T> deleteAll(final PersistentCollection<T> values);

	// include methods
	
	public PersistentSet<T> includeAll(final Collection<T> values);

	public PersistentSet<T> includeAll(final PersistentSet<T> values);

	// query methods
	
	public boolean allowsNulls();
}
