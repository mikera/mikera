package mikera.ai;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

import mikera.ai.sensor.*;


public class TestSensor {
	@Test public void testCharSensor() {
		CharSensor c=new CharSensor();
		
		double[] ds=new double[8];
		
		c.sense(' ',ds);
		assertArrayEquals(ds,new double[] {0,0,0,0,0,1,0,0},0.00001);

		try {
			c.sense(null,ds);
			Assert.fail();
		} catch (Throwable t) {
			// OK
		}

	}
	
	@Test public void testFixedStringSensor() {
		FixedStringSensor ss=FixedStringSensor.create(4);
		
		double[] ds=new double[80];
		
		ds[31]=10;
		ds[32]=10;
		
		ss.sense(" ",ds);
		assertEquals(0,ds[0],0.00001);
		assertEquals(1,ds[5],0.00001);
		assertEquals(0,ds[8],0.00001);
		assertEquals(0,ds[31],0.00001);
		assertEquals(10,ds[32],0.00001);
		
		ds[31]=10;
		ds[32]=10;
		ss.sense("        ",ds);
		assertEquals(0,ds[0],0.00001);
		assertEquals(1,ds[5],0.00001);
		assertEquals(0,ds[8],0.00001);
		assertEquals(1,ds[13],0.00001);
		assertEquals(0,ds[31],0.00001);
		assertEquals(10,ds[32],0.00001);

		ss.sense("",ds);
		assertEquals(0,ds[0],0.00001);

		try {
			ss.sense(null,ds);
			Assert.fail();
		} catch (Throwable t) {
			// OK
		}

	}
}
