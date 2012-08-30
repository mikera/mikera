package mikera.persistent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import mikera.persistent.IntSet;
import mikera.util.Rand;

import org.junit.Test;

public class TestIntSet {
	@Test public void test1() {
		int[] array=new int[] {0 , 5, -6, 100, -50, 10, -2};
		IntSet is1=IntSet.create(array);
		
		assertEquals(-50,is1.toIntArray()[0]);
		assertEquals(0,is1.findIndex(-50));
		assertEquals(true,is1.contains(-6));
		assertEquals(false,is1.contains(-7));
		
		assertFalse(is1.hasProblem());
	}
	
	@Test public void test2() {
		int[] array=new int[] {1,2,3};
		IntSet is1=IntSet.create(array);
		assertEquals("{1, 2, 3}",is1.toString());
		IntSet is2=null;
		
		IntSet is3=IntSet.create(104);
		
		for (int i=0; i<50; i++) {
			int v=Rand.r(50);
			boolean contains=is1.contains(v);
			is2=IntSet.createMerged(is1, v);
			assertTrue(is2.containsAll(is1));
			assertTrue(is2.contains(v));
			assertFalse(is2.containsAll(is3));
			assertFalse(is2.hasProblem());
			
			IntSet ist=IntSet.createWithout(is2, v);
			assertFalse(ist.hasProblem());
			if (!contains) assertTrue(is1.equals(ist)); // must get back to where we were
			
			is1=is2;
		}
		
		int c=0;
		for (Integer v: is1) {
			assertTrue(is1.contains(v));
			assertEquals(c,is1.findIndex(v));
			c++;
		}
		assertTrue(is1.size()==c);
	}
	
	@Test public void test2without() {
		int[] array=new int[] {1,2,3};
		IntSet is1=IntSet.create(array);
		IntSet is2=IntSet.createWithout(is1, 3);

		IntSet is3=IntSet.create(new int[]{1,2});
		assertTrue(is2.equals(is3));
	}
	
	@Test public void test3() {
		int[] array=new int[] {1,2,3};
		IntSet is1=IntSet.create(array);
		IntSet is2=IntSet.create(array);
		IntSet is3=is1.clone();

		assertTrue(is1==is2); // should get cached version
		assertTrue(is1==is3);
		assertTrue(is1.equals(is3));
	}
	
	@Test public void testWithout() {
		IntSet is1=IntSet.create(new int[] {1,2,3});
		IntSet is2=IntSet.create(new int[] {1,3,5});
		IntSet is3=IntSet.create(new int[] {1,2,3,4});
		IntSet is4=IntSet.create(new int[] {2,3});

		assertEquals(IntSet.createWithout(is1, is2),IntSet.create(new int[] {2}));
		assertEquals(IntSet.createWithout(is1, is3),IntSet.create(new int[] {}));
		assertEquals(IntSet.createWithout(is3, is1),IntSet.create(new int[] {4}));
		assertEquals(IntSet.createWithout(is3, is4),IntSet.create(new int[] {1,4}));
	}
	
	@Test public void testIntersect() {
		IntSet is1=IntSet.create(new int[] {1,2,3});
		IntSet is2=IntSet.create(new int[] {1,3,5});
		IntSet is3=IntSet.create(new int[] {1,2,3,4});
		IntSet is4=IntSet.create(new int[] {2,3});
		IntSet is5=IntSet.create(new int[] {});

		assertEquals(IntSet.createIntersection(is1, is2),IntSet.create(new int[] {1,3}));
		assertEquals(IntSet.createIntersection(is1, is3),IntSet.create(new int[] {1,2,3}));
		assertEquals(IntSet.createIntersection(is3, is1),IntSet.create(new int[] {1,2,3}));
		assertEquals(IntSet.createIntersection(is3, is4),IntSet.create(new int[] {2,3}));
		assertEquals(IntSet.createIntersection(is3, is5),IntSet.create(new int[] {}));
	}
	
	@Test public void test4() {
		IntSet is1=IntSet.create(new int[] {1,2,3});
		IntSet is2=IntSet.create(new int[] {});
		IntSet is3=IntSet.create(new int[] {3,4});

		IntSet rs1=IntSet.createMerged(is1, is2);
		IntSet rs2=IntSet.createMerged(is1, is3);
		IntSet rs3=IntSet.create(new int[] {1,2,3,4});
		IntSet rs4=IntSet.createMerged(is1, is1);
		
		assertTrue(is1.equals(rs1)); 
		assertTrue(is2.equals(IntSet.EMPTY_INTSET)); 
		assertTrue(rs2.equals(rs3));
		assertTrue(is1.equals(rs4)); 
	}
	
	@Test public void test5() {
		int[] array=new int[] {4,4,1,2,2,2,3};
		IntSet is1=IntSet.create(array);
		IntSet is2=IntSet.create(new int[] {4,2,1,3});

		assertEquals(4,is1.size());
		assertTrue(is1.equals(is2));
	}


}
