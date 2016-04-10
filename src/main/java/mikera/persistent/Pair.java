package mikera.persistent;


import mikera.annotations.Immutable;
import mikera.util.Bits;

/**
 * Immutable pair class
 *
 * @param <A>
 * @param <B>
 */
@Immutable
public final class Pair<A,B> extends PersistentObject implements Comparable<Pair<A,B>> {
	private static final long serialVersionUID = -7930545169533958038L;
	public final A a;
	public final B b;
	
	public Pair(A a, B b) {
		this.a=a;
		this.b=b;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (o instanceof Pair<?,?>) {
			return equals((Pair<A,B>)o);
		}
		return false;
	}
	
	public boolean equals(Pair<A,B> p) {
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

	@Override
	@SuppressWarnings("unchecked")
	public int compareTo(Pair<A,B> p) {
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
	
	public Pair<B,A> swap() {
		return new Pair<B, A>(b,a);
	}
	
	@Override
	public Pair<A,B> clone() {
		return this;
	}
	
	@Override
	public int hashCode() {
		int result=0;
		if (a!=null) result+=a.hashCode();
		if (b!=null) result+=Bits.rollLeft(b.hashCode(),16);
		return result;
	}
}
