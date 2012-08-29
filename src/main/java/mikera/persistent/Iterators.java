package mikera.persistent;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Iterators {
	/**
	 * Utility class for creating a key iterator over an entry iterator
	 * @author Mike
	 *
	 * @param <K> the type of keys
	 */
	public static final class KeyIterator<K> implements Iterator<K> {
		
		private Iterator<Map.Entry<K,?>> entryIterator;

		@SuppressWarnings("unchecked")
		public KeyIterator(Iterator<?> iterator) {
			this.entryIterator=(Iterator<Entry<K, ?>>) iterator;
		}
		
		public KeyIterator(Map<K,?> map) {
			this (map.entrySet().iterator());
		}
		
		@Override
		public boolean hasNext() {
			return entryIterator.hasNext();
		}

		@Override
		public K next() {
			Map.Entry<K,?> entry=entryIterator.next();
			return entry.getKey();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	/**
	 * Utility class for creating a value iterator over an entry iterator
	 * @author Mike
	 *
	 * @param <V> the type of values
	 */
	public static final class ValueIterator<V> implements Iterator<V> {
		
		private Iterator<Map.Entry<?,V>> entryIterator;

		public ValueIterator(Iterator<Map.Entry<?,V>> entryIterator) {
			this.entryIterator=entryIterator;
		}
		
		@Override
		public boolean hasNext() {
			return entryIterator.hasNext();
		}

		@Override
		public V next() {
			Map.Entry<?,V> entry=entryIterator.next();
			return entry.getValue();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
}
