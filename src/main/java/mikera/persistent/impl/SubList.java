package mikera.persistent.impl;

import java.util.List;

import mikera.persistent.ListFactory;
import mikera.persistent.PersistentList;

/**
 * Implements a persistent list that is a subset of an existing tuple
 * utilising the same immutable backing array
 * 
 * @author Mike
 *
 * @param <T>
 */
public final class SubList<T> extends BasePersistentList<T>   {	

	private static final long serialVersionUID = 3559316900529560364L;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final SubList<?> EMPTY_SUBLIST = new SubList(ListFactory.emptyList(),0,0);

	private final PersistentList<T> data;
	private final int offset;
	private final int length;
	
	@SuppressWarnings("unchecked")
	public static <T> SubList<T> create(List<T> source, int fromIndex, int toIndex) {
		if ((fromIndex<0)||(toIndex>source.size())) throw new IndexOutOfBoundsException();
		int newSize=toIndex-fromIndex;
		if (newSize<=0) {
			if (newSize==0) return (SubList<T>) SubList.EMPTY_SUBLIST;
			throw new IllegalArgumentException();
		}
		return createLocal(ListFactory.createFromList(source),fromIndex,toIndex);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> SubList<T> create(PersistentList<T> source, int fromIndex, int toIndex) {
		if ((fromIndex<0)||(toIndex>source.size())) throw new IndexOutOfBoundsException();
		int newSize=toIndex-fromIndex;
		if (newSize<=0) {
			if (newSize==0) return (SubList<T>) SubList.EMPTY_SUBLIST;
			throw new IllegalArgumentException();
		}
		if (source instanceof SubList<?>) {
			SubList<T> sl=(SubList<T>)source;
			return createLocal(sl.data,fromIndex+sl.offset,toIndex+sl.offset);
		} else {
			return createLocal(source,fromIndex,toIndex);
		}
	}
	
	private static <T> SubList<T> createLocal(PersistentList<T> source, int fromIndex, int toIndex) {
		return new SubList<>(source,fromIndex,toIndex-fromIndex);
	}
	
	public int size() {
		return length;
	}
	
	private SubList(PersistentList<T> source, int off, int len) {
		data=source;
		offset=off;
		length=len;	
	}
	
	public T get(int i) {
		if ((i<0)||(i>=length)) throw new IndexOutOfBoundsException();
		return data.get(i+offset);
	}
	
	public SubList<T> clone() {
		return this;
	}
	
	/**
	 * Special append version for SubList 
	 * Attempts to merge adjacent sublists
	 */
	public PersistentList<T> append(PersistentList<T> values) {
		if (values instanceof SubList<?>) {
			SubList<T> sl=(SubList<T>)values;
			return append(sl);
		}
		return super.append(values);
	}
	
	
	public PersistentList<T> append(SubList<T> sl) {
		if ((data==sl.data)&&((offset+length)==sl.offset)) {
			int newLength=length+sl.length;
			if (newLength==data.size()) return data;
			return new SubList<>(data,offset,newLength);
		}
		return super.append(sl);
	}
	
	public PersistentList<T> subList(int fromIndex, int toIndex) {
		if ((fromIndex<0)||(toIndex>size())) throw new IndexOutOfBoundsException();
		if (fromIndex>=toIndex) {
			if (fromIndex==toIndex) return ListFactory.emptyList();
			throw new IllegalArgumentException();
		}
		if ((fromIndex==0)&&(toIndex==size())) return this;
		return data.subList(offset+fromIndex, offset+toIndex);
	}
}
