package mikera.data;

import static org.junit.Assert.assertEquals;
import mikera.data.BigRational;

import org.junit.Test;

public class TestBigRational {
	@Test public void testBR() {
		BigRational b1=new BigRational(1);
		BigRational b2=new BigRational(1,1);
		
		assertEquals(b1,b2);
	}
	
	
	@Test public void testBigRational() {
		BigRational br=new BigRational(10,2);
		assertEquals("5",br.toString());
		
		BigRational br2=new BigRational(1,-2);
		assertEquals("-1/2",br2.toString());
		assertEquals(-0.5,br2.doubleValue(),0.0001);
		
		assertEquals("9/2",br.add(br2).toString());
	}
	

}
