package mikera.persistent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import mikera.persistent.impl.BlockList;
import mikera.persistent.impl.CompositeList;
import mikera.persistent.impl.SingletonList;
import mikera.persistent.impl.Tuple;
import mikera.util.emptyobjects.NullList;

/**
 * Factory class for persistent list types
 * 
 * @author Mike Anderson
 *
 * @param <T>
 */
public class ListFactory<T> {
	public static final int TUPLE_BUILD_BITS=5;
	public static final int MAX_TUPLE_BUILD_SIZE=1<<TUPLE_BUILD_BITS;
	
	public static PersistentList<?>[] NULL_PERSISTENT_LIST_ARRAY=new PersistentList[0];
	
	public static <T> PersistentList<T> create() {
		return emptyList();
	}	
	
	@SuppressWarnings("unchecked")
	public static <T> PersistentList<T> emptyList() {
		return (PersistentList<T>) NullList.INSTANCE;
	}	
	
	public static <T> PersistentList<T> create(T value) {
		return SingletonList.create(value);
	}
	
	public static <T> PersistentList<T> create(T a, T b) {
		return Tuple.create(a,b);
	}
	
	public static <T> PersistentList<T> createFromArray(T[] data) {
		return createFromArray(data,0,data.length);
	}
	
	public static <T> PersistentList<T> createFromArray(T[] data,  int fromIndex, int toIndex) {
		int n=toIndex-fromIndex;
		if (n<=MAX_TUPLE_BUILD_SIZE) {
			// very small cases
			if (n<2) {
				if (n<0) throw new IllegalArgumentException(); 
				if (n==0) return emptyList();
				return SingletonList.create(data[fromIndex]);
			}	
			
			// note this covers negative length case
			return Tuple.create(data,fromIndex,toIndex);
		}	
		
		// otherwise create a block list
		return BlockList.create(data,fromIndex,toIndex);
	}

	@SuppressWarnings("unchecked")
	public static <T> PersistentList<T> createFromCollection(Collection<T> source) {
		if (source instanceof PersistentList<?>) {
			return (PersistentList<T>)source;
		} else if (source instanceof List<?>) {
			return createFromList((List<T>)source,0,source.size());
		} 
		
		Object[] data=source.toArray();
		return createFromArray((T[])data);
	}
	
	public static<T> PersistentList<T> createFromIterator(Iterator<T> source) {
		ArrayList<T> al=new ArrayList<T>();
		while(source.hasNext()) {
			al.add(source.next());
		}
		return createFromCollection(al);
	}
	
	public static<T> PersistentList<T> subList(List<T> list, int fromIndex, int toIndex) {
		return createFromList(list,fromIndex,toIndex);
	}

	public static <T> PersistentList<T> createFromList(List<T> source) {
		return createFromList(source,0,source.size());
	}
	
	public static <T> PersistentList<T> createFromList(List<T> source, int fromIndex, int toIndex) {
		int maxSize=source.size();
		if ((fromIndex<0)||(toIndex>maxSize)) throw new IndexOutOfBoundsException();
		int newSize=toIndex-fromIndex;
		if (newSize<=0) {
			if (newSize==0) return emptyList();
			throw new IllegalArgumentException();
		}
			
		// use sublist if possible
		if (source instanceof PersistentList) {
			if (newSize==maxSize) return (PersistentList<T>)source;
			return createFromList((PersistentList<T>)source,fromIndex, toIndex);
		}
		
		if (newSize==1) return SingletonList.create(source.get(fromIndex));
		if (newSize<=MAX_TUPLE_BUILD_SIZE) {
			// note this covers negative length case
			return Tuple.createFrom(source,fromIndex,toIndex);
		}
		
		// create blocklist for larger lists
		return BlockList.create(source, fromIndex, toIndex);
	}
	
	public static <T> PersistentList<T> createFromList(PersistentList<T> source, int fromIndex, int toIndex) {
		return source.subList(fromIndex, toIndex);
	}

	public static <T> PersistentList<T> concat(PersistentList<T> a, T v) {
		return concat(a,ListFactory.create(v));
	}
	
	public static <T> PersistentList<T> concat(T v, PersistentList<T> a) {
		return concat(ListFactory.create(v),a);
	}
	
	public static <T> PersistentList<T> concat(PersistentList<T> a, PersistentList<T> b) {
		return CompositeList.concat(a, b);
	}
}
