package mikera.persistent;

import java.util.Collection;
import java.util.Map;

public interface IPersistentMap<K,V> extends Map<K,V>, IPersistentObject {
	// include methods
	
	public PersistentMap<K,V> include(K key, V value);
	
	public PersistentMap<K,V> include(Map<K,V> values);

	public PersistentMap<K,V> include(PersistentMap<K,V> values);

	// delete methods

	public PersistentMap<K,V> delete(K key);
	
	public PersistentMap<K,V> delete(Collection<K> key);

	public PersistentMap<K,V> delete(PersistentSet<K> key);
	
	// query methods
	
	public boolean allowsNullKey();
}
