package mikera.test;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestException {

	@Test
	public void test() {
		boolean a=false;
		boolean b=false;
		boolean c=false;
		
		try {
			try {
				String s=null;
				s.toString();
				
			} catch (Throwable t) {
				a=true;
				throw t;
			}
			
			
		} catch (NullPointerException nx) {
			
			b=true;
		} catch (Throwable t) {
			c=true;
		}
		
		assertTrue(a);
		assertTrue(b);
		assertFalse(c);
	}

}
