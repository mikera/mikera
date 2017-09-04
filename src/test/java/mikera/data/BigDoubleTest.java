package mikera.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BigDoubleTest {

    public static final double PRECISION = 0.000001;
    private double[] caseNumbers;

    @Test public void testBigDouble() {
		BigDouble bf=new BigDouble(2.0);
		bf=bf.multiply(2);
		bf=bf.subtract(1.5);
		bf=bf.add(0.5);
		bf=bf.divide(3);
		assertEquals(1,bf.doubleValue(),PRECISION);
		
		bf=BigDouble.exp(9);
		bf=bf.multiply(bf);
		assertEquals(18,bf.log(),PRECISION);
	}

    @Test public void shouldExponentiate(){
        double value = .5;
        long sign = (Double.doubleToLongBits(value) >> 63 & (1L));
        long mantisa = (Double.doubleToLongBits(value) >> 52 & (((1L << 11) - 1)));
        long factor = (Double.doubleToLongBits(value) & (((1L << 52) - 1)));
        long reversedFactor = Long.reverse((factor|(1L<<52)) << 11);
        long factor1 = (factor|(1L<<52));
        int mantisaN = ((int)mantisa - (1<<10)+1);
        System.out.println("value:   "+Long.toBinaryString(Double.doubleToLongBits(value)));
        System.out.println("sign:    "+Long.toBinaryString(sign));
        System.out.println("mantisa: "+Long.toBinaryString(mantisa));
        System.out.println("mantisaN:"+Integer.toBinaryString(mantisaN) + "("+(mantisaN)+")");
        System.out.println("factor1: "+Long.toBinaryString(factor1));
        System.out.println("factor:  "+Long.toBinaryString(factor));
        System.out.println("revFctr: "+Long.toBinaryString(reversedFactor));
        System.out.println((((sign == 1) ? -1 : 1) * (factor1>>(51-mantisaN+1))) );
        System.out.println(new BD(value).toDouble());
        //System.out.println(Long.toBinaryString(Double.doubleToLongBits(new BD(45).toDouble())));
        BigDouble bigDouble = BigDouble.valueOf(2);

        BigDouble result = bigDouble.pow(2);

        assertEquals(2*2, result.doubleValue(), PRECISION);
    }
    @Test public void shouldConvertToAndFromDouble(){
        caseNumbers = new double[]{0,0.01,0.02,0.03,0.25,0.5,0.75,1,1.2,1.5,2,3,4,6,10,100,1234};

        for(double d : caseNumbers){
            assertEquals(new BD(d).toDouble(),d,PRECISION);
            assertEquals(new BD(-d).toDouble(),-d,PRECISION);
        }
    }
    @Test public void shouldMultiplyAndDivide(){
        for(double d : caseNumbers){
            assertEquals(new BD(d).multiply(d).divide(d).toDouble(),d,PRECISION);
            assertEquals(new BD(-d).divide(d).multiply(d).toDouble(),-d,PRECISION);
        }
        double[] caseMultiplication
    }
    public static class BD{
        double mantissa;
        long exponent;

        private BD(double mantissa, long exponent) {
            this.mantissa = mantissa;
            this.exponent = exponent;
        }

        public BD(double number){
            long longBits = Double.doubleToLongBits(number);
            mantissa = Double.longBitsToDouble(((longBits | (((1L<<11)-1)<<52)) - (1L<<62)));
            exponent = ((longBits >> 52 & (((1L << 11) - 1)))+1 - (1<<10));
            System.out.println(Long.toBinaryString(longBits));
            System.out.println(Long.toBinaryString(Double.doubleToLongBits(mantissa)));
            System.out.println("exp="+exponent);
            System.out.println("man="+mantissa);
        }

        public BD add(BD b){
            return add(this,b);
        }

        private BD add(BD a, BD b) {
            if(a.exponent==b.exponent){
                return new BD(a.mantissa+b.mantissa,exponent);
            }
            return null;
        }
        public BD multiply(BD b){
            return multiply(this,b);
        }

        private BD multiply(BD a, BD b) {
            return new BD(a.mantissa * b.mantissa, a.exponent + b.exponent);
        }
        public BD divide(BD b){
            return divide(this,b);
        }

        private BD divide(BD a, BD b) {
            return new BD(a.mantissa / b.mantissa, a.exponent - b.exponent);
        }

        public double toDouble(){
            long zerosInMantissa = Double.doubleToLongBits(mantissa)-(((1L<<10)-1)<<52);
            long exp = ((exponent + (1 << 10) - 1)&((1<<12)-1));
            System.out.println("zeroM: "+Long.toBinaryString(zerosInMantissa));
            System.out.println("expR : "+Long.toBinaryString(exp));
            return Double.longBitsToDouble(
                            zerosInMantissa|(exp<<52)
                    );
        }
    }

}
