package mikera.persistent;

import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import mikera.annotations.Immutable;
import mikera.persistent.impl.BasePersistentSet;
import mikera.util.Arrays;
import mikera.util.HashCache;
import mikera.util.Tools;

/**
 * Immutable small set of integers, stored as a sorted array
 * Stored hashcode designed to enable fast hashtable lookups
 * 
 * Should never contain duplicates
 * 
 * @author Mike
 *
 */
@Immutable
public final class IntSet extends BasePersistentSet<Integer> {
	private static final long serialVersionUID = 2677550392326589873L;
	private static final HashCache<IntSet> cache=new HashCache<>(401);

	public static final IntSet EMPTY_INTSET=intern(new IntSet(mikera.util.Arrays.NULL_INTS));

	/**
	 * data field contains an ordered list of unique integers
	 */
	private final int[] data;
	private final int hash;	
	
	private IntSet(int[] values) {
		data =values;
		hash=calcHashCode();
	}
	
	public boolean containsAll(IntSet a) {
		int[] adata=a.data;
		int[] sdata=data;
		int ai=0;
		int si=0;
		while (ai<adata.length) {
			int needed=adata[ai++];
			while (true) {
				int s=sdata[si++];
				if (s>needed) return false; // not found
				if (s==needed) break;
				if (si>=sdata.length) return false;
			}
		}
		return true;
	}
	
	/**
	 * Testing method to check for duplicates, should always be false
	 * 
	 * @return
	 */
	public boolean hasProblem() {
		for (int i=0; i<data.length-1; i++) {
			if (data[i]>=data[i+1]) return true;
		}
		return false;
	}

	public boolean contains (int v) {
		return findIndex(v,0,data.length)>=0;
	}
	
	public int[] toIntArray() {
		return data.clone();
	}
	
	public int findIndex(int v) {
		return findIndex(v,0,data.length);
	}
	
	public int getIndex(int i) {
		return data[i];
	}

	public int findIndex(int v, int lo, int hi) {
		while (lo<hi) {
			int m=(lo+hi)>>>1;
			int dv=data[m];
			if (dv==v) return m;
			if (dv<v) {
				lo = m+1;
			} else {
				hi = m;
			}
		} 
		return -1;
	}

	/**
	 * Creates an empty IntSet
	 * 
	 * @return
	 */
	public static IntSet create() {
		return EMPTY_INTSET;
	}

	/**
	 * Creates an IntSet with a single int value
	 * 
	 * @param value
	 * @return
	 */
	public static IntSet create(int value) {
		int hc=Tools.hashCode(value);
		IntSet is=cache.getCachedValueForHashCode(hc);
		if ((is!=null)&&(is.size()==1)&&(is.data[0]==value)) return is;
		return createLocal(new int[] {value});
	}

	/**
	 * Creates an IntSet using a copy of an int array
	 * 
	 * @param data
	 * @return
	 */
	public static IntSet create(int[] data) {
		return create(data,0,data.length);
	}
	
	/**
	 * Creates an IntSet from a Set of integers
	 * 
	 * @param data
	 * @return
	 */
	public static IntSet create(Set<Integer> data) {
		int[] idata=new int[data.size()];
		int i=0;
		for (Integer it: data) {
			idata[i++]=it.intValue();
		}
		java.util.Arrays.sort(idata);
		return createLocal(idata);
	}

	public static IntSet createMerged(IntSet a, IntSet b) {
		int ai=0;
		int bi=0;
		int nsize=0;
		while ((ai<a.data.length)&&(bi<b.data.length)) {
			int c=a.data[ai]-b.data[bi];
			if (c>0) {
				bi++;
			} else if (c<0) {
				ai++;
			} else {
				ai++;
				bi++;
			}
			nsize++;
		}
		nsize+=a.data.length+b.data.length-ai-bi;	
		int[] ndata=new int[nsize];
		ai=0;
		bi=0;
		for (int i=0; i<nsize; i++) {
			if (ai>=a.data.length) {ndata[i]=b.data[bi++]; continue;}
			if (bi>=b.data.length) {ndata[i]=a.data[ai++]; continue;}
			int c=a.data[ai]-b.data[bi];
			if (c>0) {
				ndata[i]=b.data[bi++];
			} else if (c<0) {
				ndata[i]=a.data[ai++];
			} else {
				ndata[i]=a.data[ai++];
				bi++;
			}
		}
		return createLocal(ndata);
	}
	
	public static IntSet createMerged(IntSet is, int v) {
		if (is.contains(v)) return is;
		int[] data=is.data;
		int ol=data.length;
		int[] ndata=new int[ol+1];
		int i=0;
		while ((i<ol)) {
			int dv=data[i];
			if (dv>v) {
				break;
			}
			ndata[i++]=dv;
		}
		ndata[i++]=v;
		while (i<=ol) {
			ndata[i]=data[i-1];
			i++;
		}
		return createLocal(ndata);
	}
	
	public static IntSet createWithout(IntSet is, int v) {
		int pos=is.findIndex(v);
		if (pos<0) return is; // no removal
		int[] data=is.data;
		int ol=data.length;
		int[] ndata=new int[ol-1];
		System.arraycopy(data, 0, ndata, 0, pos);
		System.arraycopy(data, pos+1, ndata, pos, ol-pos-1);
		return createLocal(ndata);
	}
	
	public static IntSet createWithout(IntSet source, IntSet values) {
		if ((source.size()==0)||source.equals(values)) return IntSet.EMPTY_INTSET;
		if (values.size()==0) return source;
		int sourceLength=source.data.length;
		int valuesLength=values.data.length;
		int[] td=new int[sourceLength];
		int tdi=0;
		int vi=0;
		for (int i=0; i<sourceLength; i++) {
			int s=source.data[i];
			if (vi<valuesLength) {
				int v=values.data[vi];
				while (v<s) {
					vi++;
					if (vi<valuesLength) {
						v=values.data[vi];
					} else {
						break;
					}
				}
				if (s==v) continue;	
			}
			td[tdi++]=s;
		}
		if (tdi==sourceLength) return source;
		int[] ndata=new int[tdi];
		System.arraycopy(td, 0, ndata, 0, tdi);
		return createLocal(ndata);
	}
	
	public static IntSet createIntersection(IntSet a, IntSet b) {
		if (a.equals(b)) return a;
		int alen=a.size(); if (alen==0) return IntSet.EMPTY_INTSET;
		int blen=b.size(); if (blen==0) return IntSet.EMPTY_INTSET;
		
		int[] td=new int[Math.min(alen, blen)];
		int ii=0;
		int ai=0;
		int bi=0;
		while ((ai<alen)&&(bi<blen)) {
			int av=a.data[ai];
			int bv=b.data[bi];
			if (av==bv) {
				td[ii++]=av;
				ai++;
				bi++;
			} else if (av<bv){
				ai++;
			} else {
				bi++;
			}
		}
		
		int[] ndata=new int[ii];
		System.arraycopy(td, 0, ndata, 0, ii);
		return createLocal(ndata);		
	}
	
	/**
	 * Creates an IntSet from a subset of an int array.
	 * 
	 * Sorts and removes duplicates as needed.
	 * 
	 * Creates a copy of the input array.
	 * 
	 * @param data
	 * @param offset
	 * @param size
	 * @return
	 */
	private static IntSet create(int[] data, int offset, int size) {
		if (size==0) return EMPTY_INTSET;
		int[] ndata=new int[size];
		System.arraycopy(data, offset, ndata, 0, size);
		java.util.Arrays.sort(ndata);
		ndata=Arrays.deduplicate(ndata);
		return createLocal(ndata);
	}
		
	/**
	 * Creates an IntSet using a given local int array.
	 * 
	 * Assumes the array is already sorted, immutable and deduplicated
	 * 
	 * @param sortedData
	 * @return
	 */
	private static IntSet createLocal(int[] sortedData) {
		return intern(new IntSet(sortedData));
	}
	
	/**
	 * Interns the given IntSet in a static fixed-size cache.
	 * 
	 * @param is
	 * @return
	 */
	public static IntSet intern(IntSet is) {
		is=cache.cache(is);
		return is;
	}

	/**
	 * Calculates the hashcode of an IntSet
	 */
	public int hashCode() {
		return hash;
	}
	
	private int calcHashCode() {
		return Tools.hashCode(data);	
	}
	
	@Override 
	public boolean hasFastHashCode() {
		return true;
	}
	
	/**
	 * clone() returns the same IntSet, as it is defined to be immutable
	 */
	public IntSet clone() {
		return this;
	}
	
	public boolean equals(IntSet intset) {
		if (intset==this) return true;
		int size=size();
		if (intset.size()!=size) return false;
		for (int i=0; i<size; i++) {
			if (data[i]!=intset.data[i]) return false;
		}
		return true;
	}
	
	public boolean equals(Object o) {
		if ((o instanceof IntSet)) {
			return equals((IntSet) o);
		}
		return super.equals(o);
	}

	/**
	 * Set<Integer> methods
	 */
	public boolean add(Integer e) {
		throw new UnsupportedOperationException("IntSet is Immutable");
	}

	public boolean addAll(Collection<? extends Integer> c) {
		throw new UnsupportedOperationException("IntSet is Immutable");
	}

	public void clear() {
		throw new UnsupportedOperationException("IntSet is Immutable");
	}

	public boolean contains(Object o) {
		if (!(o instanceof Integer)) return false;
		
		return contains (((Integer)o).intValue());
	}

	public boolean containsAll(Collection<?> c) {
   		for (Object o: c) {
			if (!contains(o)) return false;
		}
		return true;
	}

	public boolean isEmpty() {
		return size()==0;
	}

	/**
	 * Returns an iterator over all Integers in the IntSet.
	 * 
	 * Note that this needs to box each integer.
	 */
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			int pos=0;
			
			public boolean hasNext() {
				return pos<size();
			}

			public Integer next() {
				return Integer.valueOf(data[pos++]);
			}

			public void remove() {
				throw new UnsupportedOperationException("IntSet is Immutable");
			}
		};
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException("IntSet is Immutable");
	}

	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("IntSet is Immutable");
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("IntSet is Immutable");
	}

	public int size() {
		return data.length;
	}
	
	public Object[] toArray() {
		return toArrayLocal(new Integer[size()]);
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		int size=data.length;
		if (a.length<size) {
			a=(T[])new Integer[size];
		}
		return toArrayLocal(a);
	}
	
	private <T> T[] toArrayLocal(T[] a) {
		for (int i=0; i<a.length; i++) {
			a[i]=(T)Integer.valueOf(data[i]);
		}
		return a;
	}

	@Override
	public IntSet include(Integer value) {
		return createMerged(this,value.intValue());
	}
	
	public IntSet include(int value) {
		return createMerged(this,value);
	}
	
	public IntSet include(IntSet values) {
		return createMerged(this,values);
	}
	
	public IntSet delete(Integer value) {
		return createWithout(this,value.intValue());
	}
	
	public IntSet deleteAll(IntSet values) {
		return createWithout(this,values);
	}
	
	public IntSet intersection(IntSet values) {
		return createIntersection(this,values);
	}

	private Object readResolve() throws ObjectStreamException {
		// needed for deserialisation to the correct static instance
		if (size()==0) return EMPTY_INTSET;
		return intern(this);
	}
	
	@Override
	public void validate() {
		super.validate();
		if (hasProblem()) throw new Error();
	}
	
	@Override
	public boolean allowsNulls() {
		return false;
	}

}
