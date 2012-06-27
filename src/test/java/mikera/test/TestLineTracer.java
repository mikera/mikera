package mikera.test;

import static org.junit.Assert.assertEquals;
import mikera.engine.LineTracer;
import mikera.engine.Octreap;
import mikera.engine.PointVisitor;

import org.junit.Test;

public class TestLineTracer {
	@Test public void testPaths() {
		final Octreap<Integer> o=new Octreap<Integer>();
		
		float d=0;
		
		o.fillSpace(1);
		o.set(3,0,0, 101);
		
		final int[] count=new int[1];
		
		PointVisitor<Integer> countingTracer=new PointVisitor<Integer>() {
			public Object visit(int x, int y, int z, Integer v) {
				count[0]+=o.get(x, y, z);
				return null;
			}
		};
		
		count[0]=0;
		d=LineTracer.trace(0.5, 0.5, 0.5, 4.5, 0.5, 0.5, countingTracer);
		assertEquals(105,count[0]);
		assertEquals(4.0f,d,0.0001f);
		
		count[0]=0;
		d=LineTracer.trace(0.5, 0.5, 0.5, 4.5, 4.5, 4.5, countingTracer);
		assertEquals(5,count[0]);
		
		count[0]=0;
		d=LineTracer.trace(0.51, 0.52, 0.53, 4.51, 4.52, 4.53, countingTracer);
		assertEquals(13,count[0]);
		
		count[0]=0;
		d=LineTracer.trace(4, 0.3, 0.6, 0, 0.5, 0.1, countingTracer);
		assertEquals(105,count[0]);
		
		count[0]=0;
		d=LineTracer.trace(0.74, 0, 0.3, 0.6, 4, 0.5, countingTracer);
		assertEquals(4,count[0]);
	}
}
