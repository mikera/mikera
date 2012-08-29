package mikera.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestImage {

	@Test
	public void test() {
		int i = 0xFFFFFFFF;
		assertEquals(-1,i);
		
		Object o=0xFFFFFFFF;
		assertEquals(Integer.class,o.getClass());
	}

}
