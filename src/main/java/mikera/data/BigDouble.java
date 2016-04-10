package mikera.data;

import mikera.annotations.Immutable;
import mikera.annotations.Mutable;
import mikera.util.Maths;

@Mutable
@Immutable
public final class BigDouble extends Number {
	private static final long serialVersionUID = 8944436596909296283L;

	final double factor;
	final double exponent;
	
	public BigDouble(double d) {
		this(d,0);
	}
	
	public BigDouble(Number d) {
		this(d.doubleValue(),0);
	}
	
	private BigDouble(double d, double exp) {
		double af=Math.abs(d);
		if ((af!=0)&&((af<0.001)||(af>=10000))) {
			// normalise
			double e=Math.log(af);
			d=Maths.sign(d);
			exp+=e;
		}
		factor=d;
		exponent=exp;
	}
	
	public BigDouble multiply(BigDouble bf) {
		return new BigDouble(factor*bf.factor,exponent+bf.exponent);
	}
	
	public BigDouble multiply(double d) {
		return new BigDouble(factor*d,exponent);
	}
	
	public BigDouble divide(double d) {
		return new BigDouble(factor/d,exponent);
	}
	
	public BigDouble divide(BigDouble bf) {
		return new BigDouble(factor/bf.factor,exponent-bf.exponent);
	}
	
	public BigDouble add(double d) {
		return add(d,0);
	}
	
	public BigDouble add(BigDouble bf) {
		return add(bf.factor,bf.exponent);
	}
	
	public BigDouble subtract(double d) {
		return add(-d,0);
	}
	
	public BigDouble subtract(BigDouble bf) {
		return add(-bf.factor,bf.exponent);
	}
	
	private BigDouble add(double d, double e) {
		if (e<exponent) {
			d=d*(Math.exp(e-exponent))+factor;
			e=exponent;
		} else {
			d=d+factor*Math.exp(exponent-e);
		}
		return new BigDouble(d,e);
	}
	
	public static BigDouble exp(Number n) {
		return new BigDouble(1,n.doubleValue());
	}
	
	public double log() {
		return Math.log(factor)+exponent;
	}


	@Override
	public double doubleValue() {
		return factor*Math.exp(exponent);
	}

	@Override
	public float floatValue() {
		return (float)doubleValue();
	}

	@Override
	public int intValue() {
		return (int)doubleValue();
	}

	@Override
	public long longValue() {
		return (long)doubleValue();
	}
	
	@Override
	public String toString() {
		return Double.toString(doubleValue());
	}
}
