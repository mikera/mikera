package mikera.data;

import mikera.annotations.Mutable;

/**
 * Basic mutable reference class
 * 
 * @author Mike Anderson
 *
 * @param <T> Type of reference stored
 */
@Mutable
public final class Ref<T> extends MutableObject {
	private static final long serialVersionUID = -6782282639173995653L;

	private T data;
	
	public T get() {
		return data;
	}
	
	public void set(T newValue) {
		data=newValue;
	}
	
	public T replace(T newValue) {
		T oldValue=data;
		data=newValue;
		return oldValue;
	}
}
