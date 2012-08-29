package mikera.persistent;

import java.io.Serializable;
import java.util.Map;

/**
 * A persistent MapEntry
 * 
 * @author Mike
 *
 * @param <K>
 * @param <V>
 */

public final class MapEntry<K, V> implements Map.Entry<K,V>, Serializable {
	final K key;
	final V value;
	
	MapEntry(K key, V value) {
		this.value=value;
		this.key=key;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		throw new UnsupportedOperationException();
	}
}
