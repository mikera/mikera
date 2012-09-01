package mikera.util;

import java.lang.ref.SoftReference;

public class HashCache<T> {
	private SoftReference<T>[] data;
	
	// TODO: Implement cache resizing
	@SuppressWarnings("unchecked")
	public HashCache(int size) {
		data=new SoftReference[size];
	}
	
	public int getMaxSize() {
		return data.length;
	}
	
	public T cache(T value) {
		int hc=value.hashCode();
		int i=getIndex(hc);
		SoftReference<T> sr= data[i];
		T result=(sr==null)?null:sr.get();
		
		if ((result==null)||(!result.equals(value))) {
			// cache new value
			data[i]=new SoftReference<>(value);
			return value;
		} else {
			// return cached value
			return result;
		}
	}
	
	public T getCachedValueForHashCode(int hc) {
		int i=getIndex(hc);	
		SoftReference<T> sr= data[i];
		if (sr==null) return null;
		
		T result=sr.get();
		if (result==null) {
			data[i]=null;
		}
		return result;
	}
	
	private int getIndex(int hc) {
		hc=hc&0x7FFFFFFF;
		int i=hc %(data.length);
		return i;
	}
}
