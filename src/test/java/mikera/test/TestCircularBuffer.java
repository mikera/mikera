package mikera.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.TreeMap;

import mikera.util.CircularBuffer;
import mikera.util.Rand;

import org.junit.Test;

public class TestCircularBuffer {
	
	// get a empty integer buffer with random position
	public CircularBuffer<Integer> getIntegerBuffer(int i) {
		CircularBuffer<Integer> cb=new CircularBuffer<Integer>(i);
		
		for (int ii=Rand.r(10*i); ii>0; ii-- ) {
			cb.add(ii);
		}
		
		assertTrue(cb.sanityCheck());
		cb.clear();
		assertTrue(cb.sanityCheck());
		return cb;
	}
	
	@Test public void testRandomly() {
		CircularBuffer<Integer> cb=getIntegerBuffer(10);
		
		for (int i=0; i<100; i++) {
			if (Rand.d(3)==1) cb.setMaxSize(Rand.r(20));
			assertTrue(cb.sanityCheck());
			if (Rand.d(3)==1) cb.add(Rand.d(100));
			assertTrue(cb.sanityCheck());
			if (Rand.d(3)==1) cb.removeFirstAdded();
			assertTrue(cb.sanityCheck());
			if (Rand.d(3)==1) cb.removeLastAdded();
			assertTrue(cb.sanityCheck());
			if (Rand.d(3)==1) cb.poll();
			assertTrue(cb.sanityCheck());
		}
	}
	
	@Test public void testSize() {
		CircularBuffer<Integer> cb=getIntegerBuffer(10);
		
		for (int i=0; i<107; i++) {
			cb.add(i);
		}
		assertEquals(10,cb.getCount());
		assertEquals(106,(int)cb.get(0));
		assertEquals(97,(int)cb.get(9));
		assertEquals(null,cb.get(10));
		assertEquals(null,cb.get(1000));
		
		cb.setMaxSize(5);
		assertEquals(106,(int)cb.get(0));
		assertEquals(102,(int)cb.get(4));
		assertEquals(null,cb.get(5));		
		assertEquals(null,cb.get(1000));
		
		for (int i=0; i<65; i++) {
			cb.add(i);
		}
		assertEquals(64,(int)cb.get(0));
		assertEquals(60,(int)cb.get(4));
		assertEquals(null,cb.get(5));
		assertEquals(null,cb.get(1000));
		
		// expand buffer to 20
		cb.setMaxSize(20);
		assertEquals(5,cb.getCount());
		assertEquals(64,(int)cb.get(0));
		assertEquals(60,(int)cb.get(4));
		assertEquals(null,cb.get(5));
		assertEquals(null,cb.get(1000));
		
		for (int i=0; i<107; i++) {
			cb.add(i);
		}
		assertEquals(20,cb.getCount());
		assertEquals(106,(int)cb.get(0));
		assertEquals(97,(int)cb.get(9));
		assertEquals(87,(int)cb.get(19));
		assertEquals(null,cb.get(20));
		assertEquals(null,cb.get(1000));
		
		// make very small
		cb.setMaxSize(2);
		assertEquals(2,cb.getCount());
		assertEquals(106,(int)cb.get(0));
		assertEquals(105,(int)cb.get(1));
		assertEquals(null,cb.get(2));
		
		// zero size buffer should also work!!
		cb.setMaxSize(0);
		assertEquals(0,cb.getCount());
		assertEquals(null,cb.get(10));
		assertEquals(null,cb.get(0));
		assertEquals(null,cb.peek());
		cb.add(100);
		assertEquals(0,cb.getCount());
		assertEquals(null,cb.poll());
		assertEquals(0,cb.getCount());
		
		assertTrue(cb.sanityCheck());
		
	}
	
	@Test public void testClear() {
		CircularBuffer<Integer> cb=getIntegerBuffer(10);
		for (int i=0; i<8; i++) {
			cb.add(i);
		}
		cb.clear();
		assertEquals(null,cb.get(0));
		assertEquals(0,cb.getCount());
		for (int i=0; i<8; i++) {
			cb.add(i);
		}
		assertEquals(7,(int)cb.get(0));
		assertEquals(8,cb.getCount());
		
		cb.clear();
		assertEquals(null,cb.get(0));
		assertEquals(0,cb.getCount());
		
		assertTrue(cb.sanityCheck());
	}
	
	
	@Test public void testTreeMap() {
		TreeMap<Integer,String> tm=new TreeMap<Integer,String>();
		
		tm.put(1,"A");
		tm.put(2, "B");
		
		assertTrue(tm.size()==2);
	}
	
	@Test public void testIterator() {
		CircularBuffer<Integer> cb=getIntegerBuffer(10);
		for (int i=0; i<20; i++) {
			cb.add(i);
		}

		int total=0;
		Iterator<Integer> it=cb.iterator();
		while (it.hasNext()) {
			total+=it.next();
		}
		assertEquals(145,total);
		
		assertTrue(cb.sanityCheck());
	}
	
	@Test public void testRemoveEnds() {
		CircularBuffer<Integer> cb=new CircularBuffer<Integer>(10);
		for (int i=0; i<2; i++) {
			cb.add(i+3);
		}

		assertEquals(4,(int)cb.get(0));
		assertEquals(3,(int)cb.get(1));
		
		assertTrue(cb.tryRemoveEnd());
		assertEquals(1,cb.getCount());

		assertEquals(4,(int)cb.get(0));
		assertEquals(null,cb.get(1));
		
		assertTrue(cb.tryRemoveEnd());		
		assertEquals(0,cb.getCount());
		assertFalse(cb.tryRemoveEnd());
		assertEquals(0,cb.getCount());
		
		assertTrue(cb.sanityCheck());
		
		cb.clear();
		for (int i=1; i<=10; i++) {
			cb.add(i);
		}
		assertEquals(10,(int)cb.removeLastAdded());
		assertEquals(1,(int)cb.removeFirstAdded());
		assertEquals(9,(int)cb.removeLastAdded());
		assertEquals(2,(int)cb.removeFirstAdded());
		assertEquals(6,cb.getCount());
		
		assertTrue(cb.sanityCheck());
		
		assertEquals(8,(int)cb.removeLastAdded());
		assertEquals(3,(int)cb.removeFirstAdded());
		assertEquals(7,(int)cb.removeLastAdded());
		assertEquals(4,(int)cb.removeFirstAdded());
		assertEquals(6,(int)cb.removeLastAdded());
		assertEquals(5,(int)cb.removeFirstAdded());
		assertEquals(0,cb.getCount());
		assertEquals(null,cb.removeLastAdded());
		assertEquals(null,cb.removeFirstAdded());
		assertEquals(0,cb.getCount());
		
		assertTrue(cb.sanityCheck());
	}
	
	@Test public void testRemove() {
		CircularBuffer<Integer> cb=getIntegerBuffer(10);
		for (int i=1; i<=10; i++) {
			cb.add(i);
		}
		
		assertEquals(3,cb.removeRange(5,3));
		assertEquals(7,cb.size());
		
		assertTrue(cb.sanityCheck());
		
		assertEquals(10,(int)cb.get(0));
		assertEquals(6,(int)cb.get(4));
		assertEquals(2,(int)cb.get(5));
		assertEquals(1,(int)cb.get(6));
		assertEquals(null,cb.get(7));
		
		assertEquals(6,(int)cb.remove(4));
		assertEquals(2,(int)cb.remove(4));
		assertEquals(1,(int)cb.remove(4));
		assertEquals(4,cb.size());
		
		assertTrue(cb.sanityCheck());
	}
	
	@Test public void testQueue() {
		CircularBuffer<Integer> cb=getIntegerBuffer(10);
		for (int i=1; i<=10; i++) {
			cb.add(i);
		}

		assertEquals(false, cb.offer(15));
		
		assertEquals(10,cb.getCount());

		assertEquals(1,(int)cb.peek());
		assertEquals(1,(int)cb.element());
		
		assertEquals(1,(int)cb.remove());
		assertEquals(2,(int)cb.poll());
		assertEquals(8,cb.getCount());
		
		assertEquals(true, cb.offer(11));
		
		assertTrue(cb.sanityCheck());

		for (int i=3; i<=10; i++) {
			assertEquals(i,(int)cb.poll());
		}
		
		assertEquals(11,(int)cb.peek());
		assertEquals(11,(int)cb.remove());
		assertEquals(null,cb.peek());
		assertEquals(0,cb.getCount());
		
		assertTrue(cb.sanityCheck());
		
	}
}
