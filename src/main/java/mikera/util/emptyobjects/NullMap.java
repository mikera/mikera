package mikera.util.emptyobjects;

import java.io.ObjectStreamException;
import java.util.Map;

import mikera.persistent.PersistentCollection;
import mikera.persistent.PersistentHashMap;
import mikera.persistent.PersistentMap;
import mikera.persistent.PersistentSet;

public final class NullMap<K,V> extends PersistentMap<K, V> {
	private static final long serialVersionUID = 1717634837542733926L;

	
	@SuppressWarnings({ "rawtypes" })
	public static final NullMap<?,?> INSTANCE=new NullMap();
	
	private NullMap() {
		
	}

	@Override
	public void clear() {
		// We are already empty, so nothing to do
	}

	public boolean containsKey(Object key) {
		return false;
	}

	public boolean containsValue(Object value) {
		return false;
	}

	@SuppressWarnings("unchecked")
	public PersistentSet<java.util.Map.Entry<K, V>> entrySet() {
		return (PersistentSet<java.util.Map.Entry<K, V>>) NullSet.INSTANCE;
	}

	public boolean isEmpty() {
		return true;
	}

	@SuppressWarnings("unchecked")
	public PersistentSet<K> keySet() {
		return (PersistentSet<K>) NullSet.INSTANCE;
	}

	public V put(K key, V value) {
		throw new UnsupportedOperationException();
	}



	public int size() {
		return 0;
	}

	@SuppressWarnings("unchecked")
	public PersistentCollection<V> values() {
		return (PersistentCollection<V>) NullSet.INSTANCE;
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException();
	}

	public V get(Object key) {
		return null;
	}

	public V remove(Object key) {
		throw new UnsupportedOperationException();
	}
	
	public NullMap<K,V> clone() {
		return this;
	}

	public PersistentMap<K, V> include(K key, V value) {
		return PersistentHashMap.create(key,value);
	}

	@Override
	public PersistentMap<K, V> delete(K key) {
		return this;
	}

	@Override
	public java.util.Map.Entry<K, V> getMapEntry(Object key) {
		return null;
	}

	private Object readResolve() throws ObjectStreamException {
		// needed for deserialisation to the correct static instance
		return INSTANCE;
	}
	
	public boolean allowsNullKey() {
		return false;
	}
}
