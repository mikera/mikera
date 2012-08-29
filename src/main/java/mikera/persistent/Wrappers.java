package mikera.persistent;

import java.util.*;

public class Wrappers {
	/**
	 * Keyset wrapper backed by a persistent map
	 * 
	 * @author Mike
	 *
	 * @param <K>
	 */
	public static class KeySet<K> extends PersistentSet<K> {
		private static final long serialVersionUID = -5590892588861307863L;

		private PersistentMap<K, ?> map;

		public KeySet (PersistentMap<K, ?> map) {
			this.map=map;
		}

		@Override
		public int size() {
			return map.size();
		}

		@Override
		public boolean isEmpty() {
			return map.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return map.containsKey(o);
		}

		@Override
		public Iterator<K> iterator() {
			return new Iterators.KeyIterator<K>(map.entrySet().iterator());
		}

		@Override
		public PersistentSet<K> include(K value) {
			return SetFactory.create(this).include(value);
		}

	}
}
