package mikera.persistent.impl;

import mikera.persistent.ListFactory;
import mikera.persistent.PersistentList;
import mikera.util.Tools;

/**
 * Persistent list that implements a repeating single value
 * 
 * @author Mike
 *
 * @param <T>
 */
public class RepeatList<T> extends BasePersistentList<T> {
	private static final long serialVersionUID = -4991558599811750311L;

	final T value;
	final int count;
	
	private RepeatList(T object, int num) {
		value=object;
		count=num;
	}
	
	public static <T> RepeatList<T> create(T object, int number) {
		return new RepeatList<>(object,number);
	}
	
	public int size() {
		return count;
	}
	
	public T get(int i) {
		if ((i<0)||(i>=count)) throw new IndexOutOfBoundsException();
		return value;
	}
	
	public PersistentList<T> subList(int start, int end) {
		if ((start<0)||(end>count)) throw new IndexOutOfBoundsException();
		if (start>=end) {
			if (start==end) return ListFactory.emptyList();
			throw new IllegalArgumentException();
		}
		int num=end-start;
		if (num==count) return this;
		return create(value,num);
	}
	
	public PersistentList<T> deleteRange(int start, int end) {
		if ((start<0)||(end>count)) throw new IndexOutOfBoundsException();
		if (start>=end) {
			if (start==end) return this;
			throw new IllegalArgumentException();
		}
		int numDeleted=end-start;
		if (numDeleted==count) return ListFactory.emptyList();
		if (numDeleted==0) return this;
		return create(value,count-numDeleted);
	}
	
	public PersistentList<T> append(PersistentList<T> values) {
		if (values instanceof RepeatList<?>) {
			RepeatList<T> ra=(RepeatList<T>)values;
			if (Tools.equalsWithNulls(ra.value, value)) {
				return create(value,ra.count+count);
			}
		}
		return super.append(values);
	}
	
	public PersistentList<T> delete(final T v) {
		if (Tools.equalsWithNulls(v,value)) {
			return ListFactory.emptyList();
		} else {
			return this;
		}
	}
}
