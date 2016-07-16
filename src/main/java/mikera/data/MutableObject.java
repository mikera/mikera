package mikera.data;

import java.io.Serializable;

import mikera.annotations.Mutable;

@Mutable
public class MutableObject implements Cloneable, Serializable {
	private static final long serialVersionUID = -4651948305928784088L;

	@Override
	public MutableObject clone() {
		try {
			return (MutableObject) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}
}
