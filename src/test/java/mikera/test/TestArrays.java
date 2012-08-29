package mikera.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import mikera.util.Arrays;

import org.junit.Test;

public class TestArrays {
	@Test public void testMerge() {
		double[] a=new double[100];
		double[] b=new double[100];
		
		a[0]=10;
		b[1]=10;
		b[10]=100;
		Arrays.mergeCopy(a, 0, b, 0, 10, 0.7);
		
	
		assertEquals(10,a[0],0.0001);
		assertEquals(7,b[0],0.0001);
		assertEquals(3,b[1],0.0001);
		assertEquals(100,b[10],0.0001);
	}
	
	@Test public void testMergeLinear() {
		double[] ds=new double[10];
		double[] es=new double[10];
		
		ds[0]=10;
		es[0]=2;
		
		Arrays.mergeLinear(ds, es, 10, 0.25, 0.75);
		assertEquals(4.0,es[0],0.0001);
	}
	
	@Test public void testAverage() {
		double[][] ds = {{0,0,0,1}, {0,0,1,0}, {1,1,0,0}, {1,1,1,1}};
		
		assertTrue(java.util.Arrays.equals(new double[] {0.5,0.5,0.5,0.5}, Arrays.calcAverage(ds)));
	}

}
