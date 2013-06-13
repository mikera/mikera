package mikera.util;

import java.util.List;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestLists {

	@Test public void testOffsetArrayList() {
		OffsetArrayList<Integer> a=new OffsetArrayList<Integer>();
		
		testList(a);
		a.add(1);
		testList(a);
		for (int i=0; i<100; i++) {
			a.add(i);
		}
		assertEquals(101,a.size());
		assertEquals(0,a.indexOf(1));
		assertEquals(2,a.lastIndexOf(1));
		assertEquals(100,a.indexOf(99));
		testList(a);
		
		testList(a.subList(10, 20));
		
		a.remove(0);
		testList(a);	
	}
	
	public static <T> void testList(List<T> list) {
		testImmutableList(list);
	}
	
	public static <T> void testImmutableList(List<T> list) {
		testIterator(list);
	}

	
	public static <T> void testIterator(List<T> list) {
		int i=0;
		for (T t:list) {
			T gt=list.get(i++);
			assertEquals(gt,t);
		}
		assertEquals(list.size(),i);
	}
}
