package mikera.persistent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import mikera.persistent.impl.ArraySet;
import mikera.persistent.impl.SingletonSet;
import mikera.util.Tools;
import mikera.util.emptyobjects.NullSet;

/**
 * Factory class for persistent sets
 * 
 * @author Mike Anderson
 *
 */
public class SetFactory {
	public static <T> PersistentSet<T> create(T value) {
		return SingletonSet.create(value);
	}
	
	public static <T> PersistentSet<T> create() {
		return emptySet();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> PersistentSet<T> emptySet() {
		return (PersistentSet<T>) NullSet.INSTANCE;
	}
	
	public static <T> PersistentSet<T> createFrom(Set<T> source) {
		if (source instanceof PersistentSet<?>) {
			return create((PersistentSet<T>)source);
		}
		int size=source.size();
		if (size==0) return emptySet();
		return PersistentHashSet.createFromSet(source);
	}
	
	public static <T> PersistentSet<T> createFrom(Iterator<T> source) {
		return createFrom(Tools.buildHashSet(source));
	}
	
	public static <T> PersistentSet<T> create(PersistentSet<T> source) {
		return PersistentHashSet.createFromSet(source);
	}
	
	public static <T> PersistentSet<T> createFrom(Collection<T> source) {
		if (source instanceof Set<?>) {
			return createFrom((Set<T>)source);
		}
		return createFrom(source.iterator());
	}
	
	public static <T> PersistentSet<T> createFrom(T[] source) {
		return ArraySet.createFromArray(source);
	}
	
	public static <T> PersistentSet<T> concat(PersistentSet<T> a, T value) {
		if (a.contains(value)) return a;
		if (a.size()==0) return SingletonSet.create(value);
		return PersistentHashSet.createFromSet(a).include(value);
	}
	
}
