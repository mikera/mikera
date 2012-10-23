package mikera.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BigDoubleTest {

	@Test public void testBigDouble() {
		BigDouble bf=new BigDouble(2.0);
		bf=bf.multiply(2);
		bf=bf.subtract(1.5);
		bf=bf.add(0.5);
		bf=bf.divide(3);
		assertEquals(1,bf.doubleValue(),0.000001);
		
		bf=BigDouble.exp(9);
		bf=bf.multiply(bf);
		assertEquals(18,bf.log(),0.000001);
	}

}
