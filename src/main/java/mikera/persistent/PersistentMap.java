package mikera.persistent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mikera.util.Tools;

public abstract class PersistentMap<K,V> extends PersistentObject implements IPersistentMap<K,V> {
	private static final long serialVersionUID = 2304218229796144868L;

	public void clear() {
		throw new UnsupportedOperationException("Unsupported on immutable collection: use an empty instance");
	}

	public abstract boolean containsKey(Object arg0);

	public boolean containsValue(Object value) {
		for (Map.Entry<K,V> ent: entrySet()) {
			if (Tools.equalsWithNulls(ent.getValue(),value)) {
				return true;
			}
		}
		return false;
	}
	
	
	public boolean containsEntry(Map.Entry<K,V> entry) {
		Map.Entry<K,V> e=getMapEntry(entry.getKey());
		if (e==null) return false;
		return Tools.equalsWithNulls(e.getValue(), entry.getValue());
	}
	
	public abstract java.util.Map.Entry<K, V> getMapEntry(Object key);

	public abstract PersistentSet<java.util.Map.Entry<K, V>> entrySet();

	public abstract V get(Object key);

	public boolean isEmpty() {
		return size()==0;
	}

	public abstract PersistentSet<K> keySet();

	public V put(K arg0, V arg1) {
		throw new UnsupportedOperationException("Unsupported on immutable collection: use include(...) instead");
	}

	public void putAll(Map<? extends K, ? extends V> arg0) {
		throw new UnsupportedOperationException();
	}

	public V remove(Object arg0) {
		throw new UnsupportedOperationException("Unsupported on immutable collection: use delete(...) instead");
	}

	public abstract int size();

	public abstract PersistentCollection<V> values();

	@SuppressWarnings("unchecked")
	public PersistentMap<K,V> clone() {
		return (PersistentMap<K,V>)super.clone();
	}
	
	public abstract PersistentMap<K, V> delete(K key);

	public PersistentMap<K, V> delete(Collection<K> keys) {
		PersistentMap<K, V> pm=this;
		for (K k: keys) {
			pm=pm.delete(k);
		}
		return pm;
	}

	public PersistentMap<K, V> delete(PersistentSet<K> keys) {
		return delete((Collection<K>) keys);
	}

	public abstract PersistentMap<K, V> include(K key, V value);

	public PersistentMap<K, V> include(Map<K, V> values) {
		PersistentMap<K, V> pm=this;
		for (Map.Entry<K, V> entry:values.entrySet()) {
			pm=pm.include(entry.getKey(),entry.getValue());
		}
		return pm;
	}

	public PersistentMap<K, V> include(PersistentMap<K, V> values) {
		return include((Map<K,V>) values);
	}
	
	public HashMap<K,V> toHashMap() {
		HashMap<K,V> hm=new HashMap<K, V>();
		for (Map.Entry<K,V> ent: entrySet()) {
			hm.put(ent.getKey(), ent.getValue());
		}
		return hm;
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (o instanceof PersistentMap<?,?>) {
			return equals((PersistentMap<K,V>)o);
		}
		return false;
	}
	
	public int hashCode() {
		return Tools.hashCode(entrySet().iterator());
	}
	
	@Override
	public boolean hasFastHashCode() {
		return false;
	}
	
	public boolean equals(PersistentMap<K,V> pm) {
		if (this==pm) return true;
		if (this.size()!=pm.size()) return false;
		return this.containsAll(pm)&&pm.containsAll(this);
	}
	
	public boolean containsAll(PersistentMap<K,V> pm) {
		for (Map.Entry<K, V> ent:pm.entrySet()) {
			if (!containsEntry(ent)) return false;
		}
		return true;
	}

	public void validate() {
		// nothing to do
	}
	
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append('{');
		boolean first=true;
		for (Map.Entry<K, V> ent: entrySet()) {
			if (first) {
				first=false;
			} else {
				sb.append(", ");
			}
			sb.append(ent.toString());
		}
		sb.append('}');
		return sb.toString();
	}
}
