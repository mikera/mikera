package mikera.persistent.impl;

import java.util.Iterator;
import java.util.Map;

import mikera.persistent.PersistentSet;
import mikera.persistent.SetFactory;

public final class KeySetWrapper<K,V> extends PersistentSet<K> {
	private static final long serialVersionUID = -3297453356838115646L;

	
	PersistentSet<Map.Entry<K,V>> source;
	
	
	public KeySetWrapper(PersistentSet<Map.Entry<K, V>> base) {
		source=base;
	}
	
	@Override
	public PersistentSet<K> include(K value) {
		return SetFactory.create(this).include(value);
	}

	@Override
	public int size() {
		return source.size();
	}

	public Iterator<K> iterator() {
		return new KeySetIterator<>(source);
	}
	
	public static class KeySetIterator<K,V> implements Iterator<K> {
		private Iterator<Map.Entry<K,V>> source;
		
		public KeySetIterator(PersistentSet<Map.Entry<K,V>> base) {
			source=base.iterator();
		}

		public boolean hasNext() {
			return source.hasNext();
		}

		public K next() {
			Map.Entry<K,V> next=source.next();
			return next.getKey();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}
