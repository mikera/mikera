package mikera.util;


import mikera.annotations.Mutable;

/**
 * Immutable pair class
 *
 * @param <A>
 * @param <B>
 */
@Mutable
public final class MutablePair<A,B> implements Cloneable, Comparable<MutablePair<A,B>> {
	static final long serialVersionUID = -7930545169533958038L;
	
	public A a;
	public B b;
	
	public MutablePair(A a, B b) {
		this.a=a;
		this.b=b;
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (o instanceof MutablePair<?,?>) {
			return equals((MutablePair<A,B>)o);
		}
		return false;
	}
	
	public boolean equals(MutablePair<A,B> p) {
		if (p==null) return false;
		
		if (a==null) {
			if (p.a!=null) return false;
		} else {
			if (!a.equals(p.a)) return false;
		}
		
		if (b==null) {
			if (p.b!=null) return false;
		} else {
			if (!b.equals(p.b)) return false;
		}
		
		return true;
	}

	@SuppressWarnings("unchecked")
	public int compareTo(MutablePair<A,B> p) {
		if (a instanceof Comparable<?>) {
			if (a!=null) {
				Comparable<A> c=(Comparable<A>)a;
				int v=c.compareTo(p.a);
				if (v!=0) return v;
			}	
		} else {
			throw new Error("Can't compare Pair: first component type not comparable");
		}
		
		if (b instanceof Comparable) {
			if (b!=null) {
				Comparable<B> c=(Comparable<B>)b;
				int v=c.compareTo(p.b);
				if (v!=0) return v;
			}	
		}
		return 0;
	}
	
	public MutablePair<B,A> swap() {
		return new MutablePair<B,A>(b,a);
	}
	
	public MutablePair<A,B> clone() {
		return this;
	}
	
	public int hashCode() {
		int result=0;
		if (a!=null) result+=a.hashCode();
		if (b!=null) result+=Bits.rollLeft(b.hashCode(),16);
		return result;
	}
}
