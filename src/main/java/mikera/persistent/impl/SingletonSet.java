package mikera.persistent.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

import mikera.persistent.PersistentSet;
import mikera.persistent.SetFactory;
import mikera.util.Tools;

/**
 * Singleton set instance
 * 
 * @author Mike Anderson
 *
 * @param <T>
 */
public final class SingletonSet<T> extends BasePersistentSet<T> {
	private static final long serialVersionUID = -8579831785484446664L;

	
	final T value;
	
	
	public static <T> SingletonSet<T> create(T object) {
		return new SingletonSet<T>(object);
	}
	
	@Override
	public int size() {
		return 1;
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	private SingletonSet(T object) {
		value=object;
	}

	@Override
	public PersistentSet<T> include(T value) {
		return SetFactory.concat(this,value);
	}
	
	@Override
	public boolean contains(Object o) {
		return Tools.equalsWithNulls(value, o);
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private int pos=0;
			@Override
			public boolean hasNext() {
				return (pos<1);
			}

			@Override
			public T next() {
				if (pos>0) throw new NoSuchElementException();
				pos++;
				return value;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
