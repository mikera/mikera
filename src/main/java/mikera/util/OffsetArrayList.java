package mikera.util;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

/**
 * ArrayList implementation that supports structural sharing with offsets into sublists.
 * 
 * Mutating an OffsetArrayList has undefined results on views of the same data.
 * 
 * @author Mike
 *
 * @param <T>
 */
public class OffsetArrayList<T> extends AbstractList<T> {

	private static final int DEFAULT_CAPACITY = 8;
	
	int offset;
	int size;
	Object[] data;
	
	public OffsetArrayList() {
		offset=0;
		size=0;
		data=new Object[DEFAULT_CAPACITY];
	}
	
	public OffsetArrayList(Collection<T> data) {
		this.offset=0;
		this.data=new Object[data.size()];
		this.addAll(data);
	}
	

	public OffsetArrayList(Object[] data, int offset, int size) {
		this.data=data;
		this.offset=offset;
		this.size=size;
	}


	private List<T> wrap(Object[] data, int offset, int size) {
		return new OffsetArrayList<T>(data,offset,size);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T get(int index) {
		if ((index<0)||(index>=size)) throw new IndexOutOfBoundsException("Index: "+index+ " on array of size: "+size);
		return (T) data[index+offset];
	}

	@Override
	public int size() {
		return size;
	}
	
	public void ensureCapacity(int capacity) {
		if (data.length-offset>=capacity) return;
		
		int newSize=Math.max((size*3/2)+10, capacity);
		Object[] newData=new Object[newSize];
		System.arraycopy(data, 0, newData, 0, size);
		data=newData;
		offset=0;
	}

	@Override
	public boolean isEmpty() {
		return size==0;
	}

	@Override
	public Object[] toArray() {
		Object[] result=new Object[size];
		System.arraycopy(data, offset, result, 0, size);
		return result;
	}

	@SuppressWarnings("hiding")
	@Override
	public <T> T[] toArray(T[] a) {
		System.arraycopy(data, offset, a, 0, size);
		return a;
	}

	@Override
	public boolean add(T e) {
		ensureCapacity(size+1);
		data[offset+size]=e;
		size++;
		return true;
	}

	@Override
	public boolean remove(Object o) {
		for (int i=0; i<size; i++) {
			Object t=get(i);
			if (o==null ? t==null : o.equals(t)) {
				remove(i);
				return true;
			}
		}
		return false;
	}



	@Override
	public void clear() {
		size=0;
		offset=0;
		data=new Object[DEFAULT_CAPACITY];
	}

	@SuppressWarnings("unchecked")
	@Override
	public T set(int index, T element) {
		if ((index<0)||(index>=size)) throw new IndexOutOfBoundsException("Index: "+index+ " on array of size: "+size);
		T old=(T)data[offset+index];
		data[offset+index]=element;
		return old;
	}

	@Override
	public void add(int index, T element) {
		if ((index<0)||(index>=size)) throw new IndexOutOfBoundsException("Index: "+index+ " on array of size: "+size);
		
	}

	@Override
	public T remove(int index) {
		if ((index<0)||(index>=size)) throw new IndexOutOfBoundsException("Index: "+index+ " on array of size: "+size);
		@SuppressWarnings("unchecked")
		T result=(T) data[index];
		System.arraycopy(data, offset+index+1, data, offset+index, size-index-1);
		data[size]=null;
		return result;
	}

	@Override
	public int indexOf(Object o) {
		for (int i=0; i<size; i++) {
			Object t=get(i);
			if (o==null ? t==null : o.equals(t)) {
				return i;
			}			
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		for (int i=size-1; i>=0; i--) {
			Object t=get(i);
			if (o==null ? t==null : o.equals(t)) {
				return i;
			}			
		}
		return -1;
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return wrap(data,offset+fromIndex,toIndex-fromIndex);
	}

}
