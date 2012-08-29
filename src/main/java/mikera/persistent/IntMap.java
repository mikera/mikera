package mikera.persistent;

import java.io.ObjectStreamException;
import java.util.Iterator;
import java.util.Map;

import mikera.persistent.impl.KeySetWrapper;
import mikera.persistent.impl.ValueCollectionWrapper;
import mikera.util.Bits;
import mikera.util.TODOException;
import mikera.util.Tools;

/**
 * Persistent specialised HashMap implementation for integer keys
 * inspired by Clojure's persistent hash map data structures.
 * 
 * @author Mike Anderson
 *
 * @param <V> Value type
 */

public final class IntMap<V> extends PersistentMap<Integer,V> {
	private static final long serialVersionUID = 2243997925850227720L;

	/**
	 * SHIFT_AMOUNT controls the maximum branching factor.
	 * 
	 * Valid values are 2 (x4) through to 5 bits (x32 branching). 4 seems to be about the sweet spot.
	 */
	public static final int SHIFT_AMOUNT=5;
	public static final int LOW_MASK=(1<<SHIFT_AMOUNT)-1;
	public static final int DATA_SIZE=1<<SHIFT_AMOUNT;
	
	private final IMNode<V> root;

	@SuppressWarnings("rawtypes")
	private static final IMNullList<?> EMPTY_NODE_LIST=new IMNullList();
	
	@SuppressWarnings("rawtypes")
	public static final IntMap<?> EMPTY=new IntMap();

	
	@SuppressWarnings("unchecked")
	private IntMap() {
		this((IMNode<V>) EMPTY_NODE_LIST);
	}
	 
	@SuppressWarnings("unchecked")
	public IntMap(IMNode<V> newRoot) {
		if (newRoot==null) newRoot=(IMNode<V>) EMPTY_NODE_LIST;
		root=newRoot;
	}
	
	public static<V> IntMap<V> create(int key, V value) {
		return new IntMap<V>(new IMEntry<V>(key,value));
	}
	
	public static<V> IntMap<V> create(Map<Integer,V> values) {
		IntMap<V> pm=new IntMap<V>();
		for (Map.Entry<Integer,V> ent: values.entrySet()) {
			pm=pm.include(ent.getKey(),ent.getValue());
		}
		return pm;
	}

	public static <V> int countEntries(IMNode<V> node) {
		if (node==null) return 0;
		return node.size();
	}
	
	private abstract static class IMNode<V> extends PersistentObject {
		private static final long serialVersionUID = -4378011224932646278L;

		/**
		 * Removes key from IMNode, returning a modified HashNode
		 * 
		 * @param key
		 * @return Modified IMNode, the same IMNode if key not found, or null if all data deleted
		 */
		protected abstract IMNode<V> delete(int key);

		/**
		 * Returns a new IMNode including the given (key,value) pair
		 * 
		 * @param key
		 * @param value
		 * @param shift
		 * @return
		 */
		protected abstract IMNode<V> include(int key, V value, int shift);

		protected abstract IMNode<V> include(IMEntry<V> entry, int shift);

	
		/**
		 * Returns the entry for the given key value, or null if not found
		 * 
		 * @param key
		 * @return
		 */
		protected abstract IMEntry<V> getEntry(int key);
		
		/**
		 * Finds the next entry in the IMNode map, or null if not found
		 * Updates the given IMEntrySetIterator
		 * 
		 * @param it IMEntrySetIterator to be updated
		 * @return the next entry, or null if none remaining
		 */
		protected abstract IMEntry<V> findNext(IMEntrySetIterator<V> it);
		
		/**
		 * Returns the size of the IMNode, i.e. the total number of distinct entries
		 * @return
		 */
		protected abstract int size();
		
		/**
		 * Determine if the IMNode is a leaf entry
		 * Used to determine how the nodes can be re-used
		 * 
		 * @return true if leaf node, false otherwise
		 */
		protected abstract boolean isLeaf();
		
		/**
		 * Determine if the IMNode contains a given key
		 * 
		 * @return true if key is present, false otherwise
		 */
		public final boolean containsKey(int key) {
			return getEntry(key)!=null;
		}

		/**
		 * Testing function to validate internal structure of IMNode
		 */
		public abstract void validate();
	}
	
	/**
	 * Represents a full node with DATA_SIZE non-null elements
	 * @author Mike
	 *
	 * @param <K>
	 * @param <V>
	 */
	private static final class IMFullNode<V> extends IMNode<V> {
		private static final long serialVersionUID = 5910832730804486676L;
		
		
		private final IMNode<V>[] data;
		private final int shift;
		private final int count;
		
		
		protected IMFullNode(IMNode<V>[] newData, int newShift) {
			data=newData;
			shift=newShift;
			count=countEntries();
		}
		
		private static final int slotFromKey(int key, int shift) {
			return (key>>>shift)&LOW_MASK;
		}

		@Override
		protected IMNode<V> delete(int key) {
			int slot=slotFromKey(key,shift);
			IMNode<V> n=data[slot];
			IMNode<V> dn=n.delete(key);
			if (dn==null) return remove(slot);
			if (dn==n) return this;
			return replace(slot,dn);
		}
		
		@SuppressWarnings("unchecked")
		protected IMNode<V> remove(int i) {
			IMNode<V>[] newdata=new IMNode[DATA_SIZE-1];
			System.arraycopy(data, 0, newdata, 0, i);
			System.arraycopy(data, i+1, newdata, i, DATA_SIZE-i-1);
			return new IMBitMapNode<V>(newdata,shift,0xFFFFFFFF&(~(1<<i)));
		}
		
		@SuppressWarnings("unchecked")
		protected IMNode<V> replace(int i, IMNode<V> node) {
			IMNode<V>[] newData=new IMNode[DATA_SIZE];
			System.arraycopy(data, 0, newData, 0, DATA_SIZE);
			newData[i]=node;
			return new IMFullNode<V>(newData,shift);
		}
		
		@Override
		protected IMEntry<V> findNext(IMEntrySetIterator<V> it) {
			int i=slotFromKey(it.position,shift);
			IMNode<V> n=data[i];
			if (n!=null) {
				IMEntry<V> ent=n.findNext(it);
				if (ent!=null) return ent;
			}
			i++;
			while(i<DATA_SIZE) {
				n=data[i];
				if (n!=null) {
					it.position=(it.position&((1<<shift)-1)) | ((i<<shift));
					it.index=0;
					return n.findNext(it);
				}
				i++;
			}
			return null;
		}

		@Override
		protected IMEntry<V> getEntry(int key) {
			int i=slotFromKey(key,shift);
			IMNode<V> n=data[i];
			return n.getEntry(key);
		}

		@Override
		protected IMNode<V> include(int key, V value, int shift) {
			int i=slotFromKey(key,shift);
			IMNode<V> n=data[i];
			IMNode<V> dn=n.include(key, value, shift+SHIFT_AMOUNT);
			if (dn==n) return this;
			return replace(i,dn);
		}
		
		@Override
		protected IMNode<V> include(IMEntry<V> entry, int shift) {
			int key=entry.key();
			int i=slotFromKey(key,shift);
			IMNode<V> n=data[i];
			IMNode<V> dn=n.include(entry, shift+SHIFT_AMOUNT);
			if (dn==n) return this;
			return replace(i,dn);
		}

		

		private int countEntries() {
			int res=0;
			for (int i=0; i<data.length; i++) {
				IMNode<V> n=data[i];
				res+=n.size();
			}
			return res;
		}
		
		@Override
		protected int size() {
			return count;
		}

		@Override
		public void validate() {
			int count=0;
			for (int i=0; i<DATA_SIZE; i++) {
				IMNode<V> n=data[i];
				count+=n.size();
				if (n instanceof IMFullNode<?>) {
					IMFullNode<V> pfn=(IMFullNode<V>)n;
					if (pfn.shift!=(this.shift+SHIFT_AMOUNT)) throw new Error();
				}
				n.validate();
			}
			if (count!=size()) throw new Error();
		}

		@Override
		protected boolean isLeaf() {
			return false;
		}	
	}
	
	/**
	 * Represents a bitmapped node with 1 to DATA_SIZE-1 branches
	 * 
	 * Inspired by Clojure's persistent data structures
	 * 
	 * @author Mike
	 *
	 * @param <K>
	 * @param <V>
	 */
	public static final class IMBitMapNode<V> extends IMNode<V> {
		private static final long serialVersionUID = -4936128089990848344L;
		
		
		private final IMNode<V>[] data;
		private final int shift;
		private final int count;
		private final int bitmap; // bitmap indicating which slots are present in data array
		
		
		private IMBitMapNode(IMNode<V>[] newData, int newShift, int newBitmap) {
			data=newData;
			shift=newShift;
			bitmap=newBitmap;
			count=countEntries();
		}
		
		public static final int indexFromSlot(int slot, int bm) {
			int masInteger = (1<<slot) - 1;
			return Integer.bitCount( bm & masInteger );
		}
		
		public static final int slotFromHash(int key, int shift) {
			int slot=(key>>>shift)&LOW_MASK;
			return slot;
		}
		
		private final int indexFromKey(int hash, int shift) {
			return indexFromSlot(slotFromHash(hash,shift),bitmap);
		}
		
		private final int slotFromIndex(int index) {
			int v=bitmap;
			int m=Bits.lowestSetBit(v);
			while ((index--)>0) {
				v=v&(~m);
				m=Bits.lowestSetBit(v);
			}
			return Integer.bitCount(m-1);
		}

		@Override
		protected IMNode<V> delete(int key) {
			int i=indexFromKey(key,shift);
			if (i>=data.length) return this; // needed in case slot not present in current node
			IMNode<V> n=data[i];
			IMNode<V> dn=n.delete(key);
			if (dn==n) return this;
			if (dn==null) {
				return remove(i);
			}
			return replace(i,dn);
		}
		
		@SuppressWarnings("unchecked")
		private IMNode<V> remove(int i) {
			if (data.length==1) return null;
			if (data.length==2) {
				// only return the node if it is a leaf node (otherwise shift levels are disrupted....
				IMNode<V> node=data[1-i];
				if (node.isLeaf()) return node; 
			}
			IMNode<V>[] newData=new IMNode[data.length-1];
			System.arraycopy(data, 0, newData, 0, i);
			System.arraycopy(data, i+1, newData, i, data.length-i-1);
			return new IMBitMapNode<V>(newData,shift,bitmap&(~(1<<slotFromIndex(i))));
		}
		
		@SuppressWarnings("unchecked")
		protected IMNode<V> replace(int i, IMNode<V> node) {
			IMNode<V>[] newData=new IMNode[data.length];
			System.arraycopy(data, 0, newData, 0, data.length);
			newData[i]=node;
			return new IMBitMapNode<V>(newData,shift,bitmap);
		}
		
		@Override
		protected IMEntry<V> findNext(IMEntrySetIterator<V> it) {
			// note ugly but fast hack: we store index rather than slot in it.position for bitmap nodes
			int i=slotFromHash(it.position,shift);
			IMNode<V> n=data[i];
			IMEntry<V> ent=n.findNext(it);
			if (ent!=null) return ent;
			i++;
			if(i<data.length) {
				n=data[i];
				// here again we store index rather than slot
				it.position=(it.position&((1<<shift)-1)) | ((i<<shift));
				it.index=0;
				return n.findNext(it);
			}
			return null;
		}

		@Override
		protected IMEntry<V> getEntry(int key) {
			int i=indexFromKey(key,shift);
			if (i>=data.length) return null;
			IMNode<V> n=data[i];
			if (n!=null) return n.getEntry(key);
			return null;
		}

		@Override
		protected IMNode<V> include(int key, V value, int shift) {
			int s=slotFromHash(key,shift);
			int i=indexFromSlot(s,bitmap);
			if (((1<<s)&bitmap)==0) {
				return insertSlot(i,s,new IMEntry<V>(key,value));
			}
			IMNode<V> n=data[i];
			return replace(i,n.include(key, value, shift+SHIFT_AMOUNT));
		}
		
		@Override
		protected IMNode<V> include(IMEntry<V> entry, int shift) {
			int key=entry.key();
			int s=slotFromHash(key,shift);
			int i=indexFromSlot(s,bitmap);
			if (((1<<s)&bitmap)==0) {
				return insertSlot(i,s,entry);
			}
			IMNode<V> n=data[i];
			return replace(i,n.include(entry, shift+SHIFT_AMOUNT));
		}
		
		@SuppressWarnings("unchecked")
		protected IMNode<V> insertSlot(int i, int s, IMNode<V> node) {
			IMNode<V>[] newData=new IMNode[data.length+1];
			System.arraycopy(data, 0, newData, 0, i);
			System.arraycopy(data, i, newData, i+1, data.length-i);
			newData[i]=node;
			if (data.length==31) {
				return new IMFullNode<V>(newData,shift);
			} else {
				return new IMBitMapNode<V>(newData,shift,bitmap|(1<<s));				
			}
		}
		
		
		@SuppressWarnings("unchecked")
		protected static <V> IMBitMapNode<V> concat(IMNode<V> a, int ha, IMNode<V> b, int hb, int shift) {
			IMNode<V>[] nodes;
			int sa=slotFromHash(ha,shift);
			int sb=slotFromHash(hb,shift);
			int bitmap=(1<<sa)|(1<<sb);
			if (sa!=sb) {
				nodes=new IMNode[2];
				int ia=indexFromSlot(sa,bitmap);
				nodes[ia]=a;
				nodes[1-ia]=b;
			} else {
				nodes=new IMNode[1];
				nodes[0]=concat(a,ha,b,hb,shift+SHIFT_AMOUNT);
			}
			IMBitMapNode<V> fn=new IMBitMapNode<V>(nodes,shift,bitmap);
			return fn;
		}

		private int countEntries() {
			int res=0;
			for (int i=0; i<data.length; i++) {
				IMNode<V> n=data[i];
				res+=n.size();
			}
			return res;
		}
		
		@Override
		protected int size() {
			return count;
		}

		@Override
		public void validate() {
			if (data.length!=Integer.bitCount(bitmap)) throw new Error();
			int count=0;
			for (int i=0; i<data.length; i++) {
				if (i!=indexFromSlot(slotFromIndex(i),bitmap)) throw new Error();
				IMNode<V> n=data[i];
				count+=n.size();
				if (n instanceof IMFullNode<?>) {
					IMFullNode<V> pfn=(IMFullNode<V>)n;
					if (pfn.shift!=(this.shift+SHIFT_AMOUNT)) throw new Error();
				}
				n.validate();
			}
			if (count!=size()) throw new Error();
		}	
		
		@Override
		protected boolean isLeaf() {
			return false;
		}	
	}

	/**
	 * Null list implementation for starting root nodes
	 * @author Mike
	 *
	 * @param <K>
	 * @param <V>
	 */
	private static final class IMNullList<V> extends IMNode<V> {
		private static final long serialVersionUID = 1677618725079327002L;

		
		@Override
		protected IMNode<V> delete(int key) {
			return this;
		}

		@Override
		protected IMEntry<V> findNext(IMEntrySetIterator<V> it) {
			return null;
		}

		@Override
		protected IMEntry<V> getEntry(int key) {
			return null;
		}

		@Override
		protected IMNode<V> include(int key, V value, int shift) {
			return new IMEntry<V>(key,value);
		}
		
		protected IMNode<V> include(IMEntry<V> entry, int shift) {
			return entry;
		}

		@Override
		protected int size() {
			return 0;
		}

		@Override
		public void validate() {
		}
		
		@Override
		protected boolean isLeaf() {
			return true;
		}	
		
		private Object readResolve() throws ObjectStreamException {
			// needed for deserialisation to the correct static instance
			return EMPTY_NODE_LIST;
		}
	}
	
	
	/**
	 * Represents a single IntMap entry
	 * 
	 * @author Mike
	 *
	 * @param <K>
	 * @param <V>
	 */
	public static final class IMEntry<V> extends IMNode<V> implements Map.Entry<Integer,V> {
		private static final long serialVersionUID = -4668010646096033269L;
		
		
		private final int key;
		private final V value;
		
		public int key() {
			return key;
		}
		
		public Integer getKey() {
			return Integer.valueOf(key);
		}
		
		public V getValue() {
			return value;
		}
				
		public V setValue(V value) {
			throw new UnsupportedOperationException();
		}	
		
		public IMEntry(int k, V v) {
			key=k;
			value=v;
		}
		
		public boolean matches(int key) {
			return this.key==key;
		}
		
		public boolean matchesValue(V value) {
			return this.value==value;
		}
		
		@Override
		protected IMEntry<V> getEntry(int key) {
			if (matches(key)) return this;
			return null;
		}
		
		@Override
		protected IMNode<V> include(int newkey, V value,int shift) {
			if (newkey==this.key) {
				// replacement case
				if (!matchesValue(value)) return new IMEntry<V>(newkey,value);
				return this;
			}
			
			return IMBitMapNode.concat(this,key,new IMEntry<V>(newkey,value),newkey,shift);
		}
		
		@Override
		protected IMNode<V> include(IMEntry<V> entry, int shift) {
			int newkey=entry.key();
			if (newkey==this.key) {
				// replacement case
				if (!matchesValue(entry.getValue())) return entry;
				return this;
			}
			
			return IMBitMapNode.concat(this,key,entry,newkey,shift);
		}
		
		@Override
		protected IMNode<V> delete(int k) {
			if (k==this.key) return null;
			return this;
		}
		
		@Override
		protected int size() {
			return 1;
		}

		@Override
		protected IMEntry<V> findNext(IMEntrySetIterator<V> it) {
			if (it.index>0) {
				return null;
			} else {
				it.index=1;
				return this;
			}
		}

		@Override
		public void validate() {
		}
		
		@Override
		protected boolean isLeaf() {
			return true;
		}
		
		// toString() consistent with java.util.AbstractMap
		public String toString() {
			return String.valueOf(key)+'='+String.valueOf(value);
		}
	}
	
	/**
	 * EntrySet implementation
	 */
	protected final class IMEntrySet extends PersistentSet<Map.Entry<Integer,V>> {
		private static final long serialVersionUID = -3437346777467759443L;

		@Override
		public int size() {
			return IntMap.this.size();
		}
		
		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry<?,?>)) return false;
			Map.Entry<?,?> ent=(Map.Entry<?,?>)o;
			IMEntry<?> pe=IntMap.this.getEntry((Integer) ent.getKey());
			if (pe==null) return false;
			return Tools.equalsWithNulls(pe.value, ent.getValue());
		}

		public Iterator<Map.Entry<Integer,V>> iterator() {
			return new IMEntrySetIterator<V>(IntMap.this);
		}

		public PersistentSet<Map.Entry<Integer,V>> include(
				Map.Entry<Integer,V> value) {
			return SetFactory.create(this).include(value);
		}
	}
	
	
	/**
	 * Entry set iterator
	 * @author Mike
	 *
	 * @param <K>
	 * @param <V>
	 */
	private static class IMEntrySetIterator<V> implements Iterator<Map.Entry<Integer,V>> {
		public IMNode<V> root;
		public IMEntry<V> next;
		public int position=0;
		public int index=0;
		
		private IMEntrySetIterator(IntMap<V> IM) {
			root=IM.root;
			findNext();
		}

		public boolean hasNext() {
			return (next!=null);
		}

		public IMEntry<V> next() {
			IMEntry<V> result=next;
			findNext();
			return result;
		}
		
		private void findNext() {
			next=root.findNext(this);
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/*
	 *  IPersistentMap methods
	 */

	@Override
	public boolean containsKey(Object key) {
		return root.containsKey(((Integer)key).intValue());
	}
	
	public boolean containsKey(int key) {
		return root.containsKey(key);
	}

	@Override
	public PersistentSet<Map.Entry<Integer,V>> entrySet() {
		return new IMEntrySet();
	}

	@Override
	public V get(Object key) {
		IMEntry<V> entry=root.getEntry((Integer)key);
		if (entry!=null) return entry.getValue();
		return null;
	}
	
	public V get(int key) {
		IMEntry<V> entry=root.getEntry(key);
		if (entry!=null) return entry.getValue();
		return null;
	}
	
	public IMEntry<V> getEntry(Integer key) {
		return root.getEntry(key);
	}
	
	public IMEntry<V> getEntry(int key) {
		return root.getEntry(key);
	}
	
	public IMEntry<V> getEntryByPosition(int index) {
		throw new TODOException();
	}
	
	public java.util.Map.Entry<Integer,V> getMapEntry(Object key) {
		return getEntry((Integer)key);
	}

	@Override
	public PersistentSet<Integer> keySet() {
		return new KeySetWrapper<Integer, V>(entrySet());
	}

	@Override
	public int size() {
		return root.size();
	}

	@Override
	public PersistentCollection<V> values() {
		return new ValueCollectionWrapper<Integer, V>(entrySet());
	}

	@Override
	public IntMap<V> include(Integer key, V value) {
		IMNode<V> newRoot=root.include(key.intValue(), value,0);
		if (root==newRoot) return this;
		return new IntMap<V>(newRoot);
	}
	
	public IntMap<V> include(int key, V value) {
		IMNode<V> newRoot=root.include(key, value,0);
		if (root==newRoot) return this;
		return new IntMap<V>(newRoot);
	}
	
	@Override
	public IntMap<V> include(Map<Integer,V> values) {
		if (values instanceof IntMap<?>) {
			return include((IntMap<V>)values);
		}
		
		IntMap<V> pm=this;
		for (Map.Entry<Integer,V> entry:values.entrySet()) {
			pm=pm.include(entry.getKey(),entry.getValue());
		}
		return pm;
	}
	
	public IntMap<V> include(IntMap<V> values) {
		// TODO: Consider fast node-level merging implementation
		IntMap<V> pm=this;
		PersistentSet<java.util.Map.Entry<Integer, V>> entries=values.entrySet();
		for (java.util.Map.Entry<Integer, V> entry:entries) {
			pm=pm.include(entry.getKey(),entry.getValue());
		}
		return pm;
	}

	@Override
	public IntMap<V> delete(Integer key) {
		IMNode<V> newRoot=root.delete(key);
		if (root==newRoot) return this;
		return new IntMap<V>(newRoot);
	}
	
	public IntMap<V> delete(int key) {
		IMNode<V> newRoot=root.delete(key);
		if (root==newRoot) return this;
		return new IntMap<V>(newRoot);
	}
	
	public boolean allowsNullKey() {
		return false;
	}
	
	@Override
	public void validate() {
		super.validate();
		root.validate();
	}
}
