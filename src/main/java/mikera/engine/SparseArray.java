package mikera.engine;

import java.util.AbstractList;

public final class SparseArray<T> extends AbstractList<T> {
	public static final int BLOCK_BITS=9;
	public static final int BLOCK_SIZE=1<<BLOCK_BITS;
	
	private final int shift;
	private final Object[] data;
	
	public SparseArray(int size, int shift) {
		data=new Object[size];
		this.shift=shift;
	}
	
	public SparseArray() {
		data=new Object[BLOCK_SIZE];
		shift=0;
	}
	
	@Override
	public T get(int index) {
		return get((long)index);
	}

	public int getShift() {
		return shift;
	}
	
	public int shiftSize() {
		return data.length;
	}
	
	public Object getSubObject(int i) {
		return data[i];
	}
	
	@SuppressWarnings("unchecked")
	public T get(long index) {
		if (shift==0) {
			return (T)data[(int)index];
		} else {
			int si=(int)(index>>shift);
			Object d=data[si];
			if (d==null) return null;
			if (d instanceof SparseArray) {
				return ((SparseArray<T>)d).get(index-(((long)si)<<shift));
			} else {
				return (T)d;
			}
		}
	}
	
	@Override
	public T set(int index, T value) {
		set((long)index,value);
		return null;
	}

	public SparseArray<T> grow() {
		SparseArray<T> sa=new SparseArray<T>(BLOCK_SIZE,shift+BLOCK_BITS);
		sa.data[0]=this;
		return sa;
	}
	
	
	@SuppressWarnings("unchecked")
	public void set(long index, T value) {
		if (shift==0) {
			data[(int)index]=value;
		} else {
			int si=(int)(index>>shift);
			Object d=data[si];
			if (d instanceof SparseArray<?>) {
				((SparseArray<T>)d).set(index-(((long)si)<<shift),value);
			} else {
				growChild(si,d).set(index-(((long)si)<<shift),value);
				
			}
		}
	}

	private SparseArray<T> growChild(int si, Object d) {
		int childShift=Math.max(0, shift-BLOCK_BITS);
		SparseArray<T> child=new SparseArray<T>(1<<(shift-childShift), childShift);
		data[si]=child;
		return child;
	}

	@Override
	public int size() {
		return (int)longSize();
	}

	public long longSize() {
		return (1<<shift)*data.length;
	}

	@SuppressWarnings("unchecked")
	public int countNonNull() {
		int result=0;
		int n=data.length;
		for (int i=0; i<n; i++) {
			Object d=data[i];
			if (d instanceof SparseArray<?>) {
				result+=((SparseArray<T>)d).countNonNull();
			} else {
				if (d!=null) result+=(1<<shift);		
			}			
		}
		return result;
	}

}
