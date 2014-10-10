package mikera.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BigDoubleTest {

    public static final double PRECISION = 0.000001;

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
        BigDouble bigDouble = BigDouble.valueOf(2);

        BigDouble result = bigDouble.pow(2);

        assertEquals(2*2, result.doubleValue(), PRECISION);
    }

}
