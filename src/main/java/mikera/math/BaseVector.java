package mikera.math;

public abstract class BaseVector implements Cloneable {
	private static final long serialVersionUID = 4591459378854897907L;

	public BaseVector clone() {
		try {
			return (BaseVector)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}	
	}
	
	public abstract int size();
	
	public abstract float get(int i);
	
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append('(');
		int s=size();
		for (int i=0; i<s; i++) {
			if (i>0) sb.append(", ");
			sb.append(get(i));
		}		
		sb.append(')');
		return sb.toString();
	}
}
