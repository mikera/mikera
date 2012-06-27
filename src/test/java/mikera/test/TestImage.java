package mikera.test;

import static org.junit.Assert.*;

import org.junit.Test;

import mikera.image.*;

public class TestImage {

	@Test
	public void test() {
		int i = 0xFFFFFFFF;
		assertEquals(-1,i);
		
		Object o=0xFFFFFFFF;
		assertEquals(Integer.class,o.getClass());
	}

}
