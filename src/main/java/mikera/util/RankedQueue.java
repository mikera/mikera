package mikera.util;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.NoSuchElementException;

import mikera.annotations.Mutable;
import mikera.util.emptyobjects.NullArrays;

/**
 * Priority queue with a double rank for each element
 * 
 * @author Mike Anderson
 *
 * @param <T>
 */
@Mutable
public final class RankedQueue<T> extends AbstractQueue<T>{

	private int size;
	
	@SuppressWarnings("unchecked")
	private T[] objects=(T[])NullArrays.NULL_OBJECTS;
	private double[] ranks=NullArrays.NULL_DOUBLES;
	
	/**
	 * Iterator for RankedQueue
	 */
	protected class RankedQueueIterator implements Iterator<T> {
		int i;
		
		public RankedQueueIterator() {
			i=0;
		}
		
		public void add(T e) {
			throw new UnsupportedOperationException();
		}

		public boolean hasNext() {
			return (i<size());
		}

		public T next() {
			if (i>=size) throw new NoSuchElementException();
			return getObjectAtIndex(i++);
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public void set(T e) {
			throw new UnsupportedOperationException();
		}			
	}
	
	@SuppressWarnings("unchecked")
	protected void ensureCapacity(int s) {
		int currentCapacity=objects.length;
		if (s<=currentCapacity) return;
		int newSize=Math.max(s, currentCapacity*2+10);
		double[] nr=new double[newSize];
		T[] no=(T[]) new Object[newSize];
		
		System.arraycopy(objects, 0, no, 0, size);
		System.arraycopy(ranks, 0, nr, 0, size);
		
		objects=no;
		ranks=nr;
	}
	
	public int size() {
		return size;
	}
	
	private T getObjectAtIndex(int i) {
		return objects[i];
	}
	
	@Override
	public Iterator<T> iterator() {
		return new RankedQueueIterator();
	}

	public boolean offer(T e) {
		throw new UnsupportedOperationException();
	}

	public T peek() {
		if (size==0) return null;
		return objects[0];
	}
	
	public double peekRank() {
		if (size==0) throw new NoSuchElementException();
		return ranks[0];
	}
	
	@Override
	public T element() {
		if (size==0) throw new NoSuchElementException();
		return objects[0];
	}
	
	@Override
	public boolean isEmpty() {
		return (size==0);
	}
	
	//*******************************
	// functions to calcuilate parent and child indexes for any given position
	//
	
	public static final int child1(int i) {
		return ((i+1)<<1)-1;
	}
	
	public static final int child2(int i) {
		return ((i+1)<<1);
	}
	
	public static final int parent(int i) {
		return ((i-1)>>1);
	}

	public T poll() {
		if (size==0) return null;
		T result=objects[0];
		
		deleteObjectAtIndex(0);
		
		return result;
	}
	
	public void deleteAll(T object) {
		while (delete(object)!=null) {
			// loop
		}
	}
	
	public T delete(T object) {
		int n=size();
		for (int i=0; i<n; i++) {
			if (Tools.equalsWithNulls(object, objects[i])) {
				T obj=objects[i];
				deleteObjectAtIndex(i);
				return obj;
			}
		}
		return null;
	}
	
	@Override
	public boolean contains(Object object) {
		int n=size();
		for (int i=0; i<n; i++) {
			if (Tools.equalsWithNulls(object, objects[i])) {
				return true;
			}
		}
		return false;
	}
	
	public double getFirstRank(T object) {
		int n=size();
		for (int i=0; i<n; i++) {
			if (Tools.equalsWithNulls(object, objects[i])) {
				return ranks[i];
			}
		}
		return Double.NaN;
	}
	
	public int getFirstIndex(T object) {
		int n=size();
		for (int i=0; i<n; i++) {
			if (Tools.equalsWithNulls(object, objects[i])) {
				return i;
			}
		}
		return -1;
	}

	
	public void deleteObjectAtIndex(int i) {
		if (size==1) {
			if (i==0) {
				size=0; return;
			}
			throw new IllegalArgumentException();
		}
		
		// get the object to place from end of list
		T to=objects[size-1];
		double tr=ranks[size-1];
		size--;
		
		int ch=child1(i);
		int cc=size-ch; // number of child elements to consider
		while (cc>0) {
			// advance if needed to point at lowest ranked child
			if ((cc>1)&&(ranks[ch]>ranks[ch+1])) ch++;
			
			// check if we are correctly ranked at position i
			if (tr<ranks[ch]) break;
			
			// move child down
			objects[i]=objects[ch];
			ranks[i]=ranks[ch];
			
			// update indexes
			i=ch;
			ch=child1(i);
			cc=size-ch;
		}
		objects[i]=to;
		ranks[i]=tr;		
	}

	@Override
	public boolean add(T o) {
		add(o,0.0);
		return true;
	}
	
	protected void swap(int ai, int bi) {
		T to=objects[ai];
		objects[ai]=objects[bi];
		objects[bi]=to;
		
		double td=ranks[ai];
		ranks[ai]=ranks[bi];
		ranks[bi]=td;
		
	}
	
	/**
	 * Promotes an entry up towards the front (highest priority)
	 * from its current position until in right place in heap
	 * 
	 * 
	 * @param i
	 */
	private void percolate(int i) {
		T o=objects[i];
		double rank=ranks[i];
		while (i!=0) {
			int pi=parent(i);
			double prank=ranks[pi];
			if (prank<=rank) break;
			objects[i]=objects[pi];
			ranks[i]=prank;
			i=pi;
		}
		objects[i]=o;
		ranks[i]=rank;
	}
	
	/**
	 * Adds an object with a given priority
	 * @param o Object to add
	 * @param d Priority to assign
	 */
	public void add(T o, double d) {
		ensureCapacity(size+1);
		objects[size]=o;
		ranks[size]=d;
		size++;
		percolate(size-1);
	}

	public void validate() {
		for (int i=0; i<size; i++) {
			int ch=child1(i);
			if ((ch<size)&&(ranks[i]>ranks[ch])) throw new Error();
			ch++;
			if ((ch<size)&&(ranks[i]>ranks[ch])) throw new Error();
		}
	}

}
