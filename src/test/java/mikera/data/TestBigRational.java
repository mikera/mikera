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
}
