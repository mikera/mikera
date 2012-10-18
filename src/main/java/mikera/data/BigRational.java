package mikera.data;

import java.math.BigInteger;

import mikera.annotations.Immutable;

@Immutable
public class BigRational extends Number implements Comparable<BigRational> {
	private static final long serialVersionUID = 4831114521130586940L;

	public final BigInteger numerator;
	public final BigInteger denominator;
	
	public static final BigRational ZERO=new BigRational(0,1);
	public static final BigRational ONE=new BigRational(1,1);
	
	public BigRational create(int num, int denom) {
		return new BigRational(num,denom);
	}
	
	public BigRational create(long num, long denom) {
		return new BigRational(BigInteger.valueOf(num),BigInteger.valueOf(denom));
	}
	
	public BigRational create(String s) {
		String[] ss=s.split("/");
		if (ss.length==0) {
			return create(
					BigInteger.valueOf(Long.parseLong(ss[0])),
					BigInteger.ONE);
		}
		return create(
				Long.parseLong(ss[0]),
				Long.parseLong(ss[1]));
	}
	
	public BigRational create(BigInteger num, BigInteger denom) {
		return new BigRational(num,denom);
	}
	
	public BigRational(int num, int denom) {
		this(BigInteger.valueOf(num), BigInteger.valueOf(denom));
	}
	
	private BigRational(BigInteger num, BigInteger denom) {
		if (denom.compareTo(BigInteger.ZERO)<0) {
			num=num.negate();
			denom=denom.negate();
		}
		BigInteger gcd=gcd(num.abs(),denom.abs());
		if (gcd.compareTo(BigInteger.ONE)>0) {
			num=num.divide(gcd);
			denom=denom.divide(gcd);
		}
		numerator=num;
		denominator=denom;
	}
	
	public BigRational(long a) {
		this(BigInteger.valueOf(a),BigInteger.ONE);
	}

	public BigRational multiply(BigRational b) {
		return create(numerator.multiply(b.numerator), denominator.multiply(b.denominator)); 
	}
	
	public BigRational divide(BigRational b) {
		return create(numerator.multiply(b.denominator), denominator.multiply(b.numerator)); 
	}


	private BigInteger gcd(BigInteger a, BigInteger b) {
		if (a.compareTo(b)>0) {
			BigInteger t=a;
			a=b;
			b=t;
		}
		while (!b.equals(BigInteger.ZERO)) {
			BigInteger t=b;
			b=a.mod(b);
			a=t;
		} 
		return a;
	}

	@Override
	public double doubleValue() {
		return (numerator.doubleValue()/denominator.doubleValue());
	}

	@Override
	public float floatValue() {
		return (numerator.floatValue()/denominator.floatValue());
	}

	@Override
	public int intValue() {
		return (numerator.divide(denominator)).intValue();
	}

	@Override
	public long longValue() {
		return (numerator.divide(denominator)).longValue();
	}
	
	public BigInteger bigIntegerValue() {
		return (numerator.divide(denominator));
	}
	
	@Override
	public String toString() {
		if (denominator.equals(BigInteger.ONE)) {
			return numerator.toString();
		}
		return numerator.toString()+"/"+denominator.toString();
	}
	
	public BigRational subtract(BigRational b) {
		return create(
				this.numerator.multiply(b.denominator).subtract(b.numerator.multiply(this.denominator)),
				this.denominator.multiply(b.denominator)
		);
	}
	
	public BigRational add(BigRational b) {
		return create(
				this.numerator.multiply(b.denominator).add(b.numerator.multiply(this.denominator)),
				this.denominator.multiply(b.denominator)
		);
	}
	
	@Override 
	public int hashCode() {
		return numerator.hashCode()^denominator.hashCode();
	}
	
	@Override 
	public boolean equals(Object o) {
		if (o instanceof BigRational) {
			BigRational b=(BigRational)o;
			return this.numerator.equals(b.numerator)&&this.denominator.equals(b.denominator);
		}
		return false;
	}

	public int compareTo(BigRational b) {
		return this.subtract(b).numerator.compareTo(BigInteger.ZERO);
	}
}
