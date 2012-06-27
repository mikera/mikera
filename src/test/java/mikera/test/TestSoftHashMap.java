package mikera.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import mikera.util.HashCache;
import mikera.util.SoftHashMap;

import org.junit.Test;

public class TestSoftHashMap {
	@Test public void test1() {
		SoftHashMap<Integer,String> sh=new SoftHashMap<Integer,String>();
		
		sh.put(1,"dfevev");
		sh.put(2,"gbnfrvev");
		sh.put(3,"nhvev");
		sh.put(4,"hntmev");
		sh.put(5,"mtyuytmev");
		
		assertEquals("mtyuytmev",sh.get(5));
		
		assertEquals(5,sh.size());
		sh.clear();
		assertEquals(0,sh.size());
		assertEquals(null,sh.get(5));
	}
	
	/*
	@Test public void testMemoryLimits() {
		SoftHashMap<Integer,String> sh=new SoftHashMap<Integer,String>();
		
		String ws=Text.createWhiteSpace(1000000);
		StringBuffer sb=new StringBuffer();
		sb.append(ws);
		
		// we add 10GB of data to the map - this should work!
		
		for (int i=0; i<5000; i++) {
			sh.put(i,sb.substring(0,sb.length()-1));
		};
	}
	*/
	
	@Test public void testHashCache() {
		HashCache<Integer> h=new HashCache<Integer>(1000);
	
		Integer a = Integer.valueOf(100);
		Integer b = Integer.valueOf(200);
		Integer c = Integer.valueOf(200);
		
		Integer ca=h.cache(a);
		Integer cb=h.cache(b);
		Integer cc=h.cache(c);
		
		assertTrue(cb==cc);
		assertTrue(cc==b);
		assertTrue(ca!=cb);
		assertTrue(cc!=c);
	}
}
