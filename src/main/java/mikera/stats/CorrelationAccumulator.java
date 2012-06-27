package mikera.stats;

public final class CorrelationAccumulator implements Comparable<CorrelationAccumulator>, Cloneable {
	int n;
	double sum_x;
	double sum_y;
	double sum_x2;
	double sum_y2;
	double sum_xy;
		
	public void recordValues(float x, float y) {
		sum_x+=x;
		sum_y+=y;
		sum_x2+=x*x;
		sum_y2+=y*y;
		sum_xy+=x*y;
		n++;
	}

	public int n() {
		return n;
	}
	
	public float meanX() {
		double mean=sum_x/n;
		return (float)(mean);
	}
	
	public float meanY() {
		double mean=sum_y/n;
		return (float)(mean);
	}
	
	public float varX() {
		double value=(n*sum_x2-sum_x*sum_x)/(n*(n-1));
		return (float)value;
	}
	
	public float sdX() {
		return (float)Math.sqrt(varX());
	}
	
	public float varY() {
		double value=(n*sum_y2-sum_y*sum_y)/(n*(n-1));
		return (float)value;
	}
	
	public float sdY() {
		return (float)Math.sqrt(varY());
	}
	
	public float corr() {
		double num=n*sum_xy-sum_x*sum_y;
		double den=Math.sqrt( (n*sum_x2-sum_x*sum_x) * (n*sum_y2-sum_y*sum_y) );
		return (float)(num/den);
	}
	
	public float t() {
		return t(corr(),n);
	}
	
	public boolean isSignificant(float t_threshold) {
		return Math.abs(t(corr(),n))>t_threshold;
	}

	public static float t(float r, int n) {
		float rr=1-r*r;
		if (rr==0.0) return r*Float.MAX_VALUE;
		double sqr=Math.sqrt((n-2)/(rr));
		double value=r*sqr;
		
		return (float)value;	
	}
	
	@Override
	public int hashCode() {
		return (int)Double.doubleToLongBits(this.corr());
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CorrelationAccumulator)) return false;
		
		return ((CorrelationAccumulator)o).corr()==this.corr();
	}
	
	public int compareTo(CorrelationAccumulator b) {
		CorrelationAccumulator bcc=b;
		
		float ac=corr();
		float bc=bcc.corr();
		if (ac<bc) return -1;
		if (ac>bc) return 1;
		return 0;
	}
	
	public CorrelationAccumulator clone() {
		CorrelationAccumulator acc=null;
		try {
			acc=(CorrelationAccumulator)super.clone();
		} catch (CloneNotSupportedException x) {}

		return acc;	
	}
	
	public void clear() {
		n=0;
		sum_x=0;
		sum_x2=0;
		sum_y=0;
		sum_y2=0;
		sum_xy=0;
	}
}
