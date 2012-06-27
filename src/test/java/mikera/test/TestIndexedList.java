package mikera.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import mikera.util.IndexedList;
import mikera.util.Rand;

import org.junit.Test;

public class TestIndexedList {
	
	@Test public void testAdds() {
		IndexedList<Integer,String> list = new IndexedList<Integer,String>();
		
		assertEquals(0,list.size());
		
		for (int i=0; i<100; i++) {
			list.put(i,"vdedvfde");
		}
		
		assertEquals(100,list.size());
		
		for (int i=0; i<100; i++) {
			list.put(i,"regre");
		}
		
		assertEquals(100,list.size());
		assertEquals("regre",list.get(50));
		
	}
	
	@Test public void testRandomly() {
		IndexedList<Integer,Integer> list = new IndexedList<Integer,Integer>();

		for (int i=0; i<1000; i++) {
			list.put(Rand.d(10),i);
		}
		
		assertEquals(10,list.size());

	}
	
	@Test public void testFindIndex() {
		IndexedList<Integer,Integer> list = new IndexedList<Integer,Integer>();

		for (int i=0; i<100; i++) {
			list.put(i,i+100);
		}
		
		for (int i=0; i<100; i++) {
			assertEquals(i,list.findIndex(i));
			assertEquals(i+100,(int)list.get(i));
		}
	}
	
	@Test public void testMerge() {
		IndexedList<Integer,Integer> list1 = new IndexedList<Integer,Integer>();
		IndexedList<Integer,Integer> list2 = new IndexedList<Integer,Integer>();
		IndexedList<Integer,Integer> dest = new IndexedList<Integer,Integer>();
		
		// can also add from other Maps!
		HashMap<Integer,Integer> hm=new HashMap<Integer,Integer>();
		
		for (int i=0; i<50; i++) {
			list1.put(i*2,i*2);
			list1.put(i*2+1,i*2+1);
			
			int n=Rand.r(100);
			hm.put(n, 1000000);
		}
		
		dest.putAll(list1);
		dest.putAll(list2);
		dest.putAll(hm);
		assertEquals(100,dest.size());
		
		// should overwrite the values from hm
		dest.putAll(list1);
		dest.putAll(list2);
		
		for (int i=0; i<100; i++) {
			assertEquals(i,(int)dest.get(i));
		}
		
		assertEquals(100,dest.size());
	}
	
	@Test public void testIterator() {
		IndexedList<Integer,Integer> list = new IndexedList<Integer,Integer>();

		for (int i=0; i<100; i++) {
			list.put(i,i);
		}
		
		for (int n: list.keySet()) {
			assertEquals(n,(int)list.get(n));
		}
	}
}