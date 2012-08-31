package mikera.persistent;

import java.io.Serializable;
import java.util.Map;

import mikera.util.Tools;

/**
 * A persistent MapEntry
 * 
 * @author Mike
 *
 * @param <K>
 * @param <V>
 */

public final class MapEntry<K, V> implements Map.Entry<K,V>, Serializable {
	private static final long serialVersionUID = -7907536048311814113L;

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
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if (this==o) return true;
		if (!(o instanceof Map.Entry<?,?>)) return false;		
		Map.Entry<K,V> ent = (Map.Entry<K,V>)o;
		return (  Tools.equalsWithNulls(key, ent.getKey())
				&&Tools.equalsWithNulls(value, ent.getValue()));
	}
	
	public int hashCode() {
		// defined same as java.util.Map.Entry
		return (getKey()==null   ? 0 : getKey().hashCode()) ^
			   (getValue()==null ? 0 : getValue().hashCode());
		
	}
}
