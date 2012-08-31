package mikera.persistent;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import mikera.persistent.IntMap;
import org.junit.Test;

public class TestIntMap {
	@Test public void testIntMapEquals() {
		
		IntMap<String> im=IntMap.create();
		
		assertEquals(0, im.size());
		IntMap<String> im2=im.include(1,"foo");
		IntMap<String> im3=im.include(1,"foo");
		
		assertEquals(im2,im3);
		
		assertEquals(im2.entrySet(),im3.entrySet());
		
		im2=im.include(1,new String("foo"));
		im3=im.include(1,new String("foo"));
		assertEquals(im2,im3);
		assertFalse(im2==im3);
		
		assertEquals(im2.entrySet().iterator().next(),im3.entrySet().iterator().next());

	}
}
