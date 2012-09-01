package mikera.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import mikera.util.Rand;
import mikera.util.RankedQueue;

import org.junit.Test;

public class TestRankedQueue {
	
	@Test public void testAdds() {
		RankedQueue<Integer> rq=new RankedQueue<>();
		
		rq.add(1,0.2);
		rq.add(2,0.3);
		rq.add(3,0.1);
		
		rq.validate();
		
		assertEquals(3,(int)rq.poll());
		assertEquals(1,(int)rq.poll());
		assertEquals(2,(int)rq.poll());
		assertEquals(null,rq.poll());

		rq.validate();
	}
	
	@Test public void testIndexes() {
		for (int i=0 ; i<100; i++) {
			assertTrue(RankedQueue.parent(i)<i);
			assertEquals(i,RankedQueue.parent(RankedQueue.child1(i)));
			assertEquals(i,RankedQueue.parent(RankedQueue.child2(i)));
		}
	}
	
	@Test public void testRandom() {
		RankedQueue<Integer> rq=new RankedQueue<>();
		
		Integer[] is=new Integer[100];
		
		for (int i=0 ; i<100; i++) {
			is[i]=i;
		}
		
		Rand.shuffle(is);
		
		for (int i=0 ; i<100; i++) {
			rq.add(is[i],is[i]);
		}
		
		rq.validate();
		
		for (int i=0 ; i<100; i++) {
			assertEquals(i,(int)rq.poll());
		}
		assertEquals(null,rq.poll());
	}

}