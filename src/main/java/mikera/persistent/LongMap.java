package mikera.persistent;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import mikera.util.Bits;
import mikera.util.Tools;

/**
 * Persistent specialised Map implementation for long keys
 * inspired by Clojure's persistent hash map data structures.
 * 
 * Implemented using nested IntMap structures
 * 
 * @author Mike Anderson
 *
 * @param <V> Value type
 */

public final class LongMap<V> extends PersistentMap<Long,V> {
	private static final long serialVersionUID = 4829272016323285638L;

	public static final LongMap<?> EMPTY = new LongMap<>();
	
	// nested IntMap using high words
	private IntMap<IntMap<MapEntry<Long,V>>> data;

	// we use a long count internally
	private long count;
	
	private IntMap<MapEntry<Long,V>> getInner(long key) {
		return data.get(Bits.highWord(key));
	}
	
	@SuppressWarnings("unchecked")
	public LongMap() {
		data=(IntMap<IntMap<MapEntry<Long, V>>>) IntMap.EMPTY;
		count=0;
	}
	
	private LongMap(IntMap<IntMap<MapEntry<Long,V>>> data,long count) {
		this.data=data;
		this.count=count;
	}
	
	/**
	 * Returns an empty LongMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> LongMap<T> create() {
		return (LongMap<T>) EMPTY;
	}
	
	@Override
	public boolean allowsNullKey() {
		return false;
	}
	
	private long toKey(Object key) {
		if (!(key instanceof Long)) {
			throw new IllegalArgumentException("Key must be Long!");
		} else {
			return (Long)key;
		}		
	}

	@Override
	public boolean containsKey(Object key) {
		long k = toKey(key);
		IntMap<MapEntry<Long,V>> im = getInner(k);
		if (im==null) return false;
		return im.containsKey(Bits.lowWord(k));
	}

	@Override
	public java.util.Map.Entry<Long, V> getMapEntry(Object key) {
		long k = toKey(key);
		IntMap<MapEntry<Long,V>> im = getInner(k);
		if (im==null) return null;
		MapEntry<Long,V> entry = im.get(Bits.lowWord(k));
		if (entry==null) return null;
		return entry;
	}

	@Override
	public PersistentSet<java.util.Map.Entry<Long, V>> entrySet() {
		return new LMEntrySet();
	}
	
	private final class LMEntrySet extends PersistentSet<java.util.Map.Entry<Long, V>> {
		private static final long serialVersionUID = 3640862932626136516L;

		@Override
		public Iterator<Map.Entry<Long, V>> iterator() {
			return LongMap.this.entrySetIterator();
		}

		@Override
		public PersistentSet<java.util.Map.Entry<Long, V>> include(
				java.util.Map.Entry<Long, V> entry) {
			return SetFactory.createFrom(this).include(entry);
		}
		
		@SuppressWarnings("unchecked")
		@Override 
		public boolean contains(Object o) {
			if (!(o instanceof java.util.Map.Entry<?, ?>)) return false;
			java.util.Map.Entry<Long, V> entry=(java.util.Map.Entry<Long, V>)o;
			java.util.Map.Entry<Long, V> current=LongMap.this.getMapEntry(entry.getKey());
			if (current==null) return false;
			if (Tools.equalsWithNulls(entry.getValue(), current.getValue())) return true;
			return false;
		}

		@Override
		public int size() {
			return LongMap.this.size();
		}
	}


	@Override
	public V get(Object key) {
		long k = toKey(key);
		IntMap<MapEntry<Long,V>> im = getInner(k);
		if (im==null) return null;
		MapEntry<Long,V> entry = im.get(Bits.lowWord(k));
		if (entry==null) return null;
		return entry.getValue();
	}
	
	
	private final class LMEntryIterator implements Iterator<Map.Entry<Long,V>> {
		private Iterator<java.util.Map.Entry<Integer, IntMap<MapEntry<Long, V>>>> mainIterator;
		private Iterator<java.util.Map.Entry<Integer, MapEntry<Long, V>>> subIterator;
		
		private Iterator<java.util.Map.Entry<Integer, IntMap<MapEntry<Long, V>>>> getMainIterator() {
			return LongMap.this.data.entrySet().iterator();
		}
		
		private LMEntryIterator() {
			mainIterator=getMainIterator();
		}
		
		@Override
		public boolean hasNext() {
			return ((subIterator!=null)&&subIterator.hasNext())||mainIterator.hasNext();
		}

		@Override
		public MapEntry<Long, V> next() {
			if ((subIterator!=null)&&subIterator.hasNext()) {
				java.util.Map.Entry<Integer, MapEntry<Long, V>> entry=subIterator.next();
				return entry.getValue();
			}
			
			if (mainIterator.hasNext()) {
				subIterator=(mainIterator.next().getValue()).entrySet().iterator();
				return next();
			}
				
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	public Iterator<Map.Entry<Long, V>> entrySetIterator() {
		return new LMEntryIterator();
	}

	public V get(long key) {
		IntMap<MapEntry<Long,V>> im = getInner(key);
		if (im==null) return null;
		MapEntry<Long,V> entry = im.get(Bits.lowWord(key));
		if (entry==null) return null;
		return entry.getValue();
	}

	@Override
	public PersistentSet<Long> keySet() {
		return new Wrappers.KeySet<>(this);
	}

	@Override
	public int size() {
		return (int)count;
	}
	
	public long longSize() {
		return count;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PersistentCollection<V> values() {
		Iterator<Map.Entry<Long, V>> entryIterator = this.entrySet().iterator();
		return ListFactory.createFromIterator(new Iterators.ValueIterator(entryIterator));
	}

	@Override
	public PersistentMap<Long, V> delete(Long key) {
		long k=toKey(key);
		return delete(k);
	}
	
	public PersistentMap<Long, V> delete(long key) {
		IntMap<MapEntry<Long,V>> im = getInner(key);
		if (im==null) return this;
		
		int lo=Bits.lowWord(key);
		if (!im.containsKey(lo)) return this;
		IntMap<MapEntry<Long,V>> newInnerMap=im.delete(lo);
		assert (newInnerMap!=im);
		IntMap<IntMap<MapEntry<Long, V>>> newData = data.include(Bits.highWord(key),newInnerMap);		
		return new LongMap<V>(newData,count-1);
	}

	@Override
	public PersistentMap<Long, V> include(Long key, V value) {
		IntMap<MapEntry<Long,V>> im = getInner(key);
		MapEntry<Long,V> entry = new MapEntry<Long, V>(key,value);
		long newCount=count;
		int lo=Bits.lowWord(key);
		if (im==null) {
			im=IntMap.create(lo,entry);
			newCount+=1;
		} else {
			if (!im.containsKey(lo)) newCount++;
			im=im.include(lo,entry);
		}
		
		IntMap<IntMap<MapEntry<Long, V>>> newData = data.include(Bits.highWord(key),im);
		if (data==newData) return this;
		return new LongMap<V>(newData,newCount);

	}

}
