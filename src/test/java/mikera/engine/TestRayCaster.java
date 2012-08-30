package mikera.engine;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

import mikera.engine.RayCaster;
import mikera.engine.RayCaster.CastFunction;
import mikera.engine.TreeGrid;
import mikera.math.Point3i;
import mikera.util.Maths;

import org.junit.Test;

public class TestRayCaster {
	@Test public void testPoints() {
		HashSet<Point3i> hm=new HashSet<Point3i>();
		
		hm.add(new Point3i(1,1,1));
		hm.add(new Point3i(1,1,1));
		hm.add(new Point3i(1,1,1));
		hm.add(new Point3i(1,1,1));
		hm.add(new Point3i(10,10,10));
		hm.add(new Point3i(10,10,10));
		
		assertEquals(2,hm.size());
		assertEquals(new Point3i(1,1,1),new Point3i(1,1,1));
	}
	
	@Test public void testCast() {
		final TreeGrid<Integer> tg=new TreeGrid<Integer>();
		final int RANGE=5;
		int D=(RANGE*2)+1;
		
		RayCaster rc=new RayCaster();
		final int[] is=new int[RANGE];
		
		rc.setCastFunction(new CastFunction() {
			@Override
			public boolean visit(int x, int y, int z) {
				Integer i=tg.get(x, y, z);
				if (i==null) i=Integer.valueOf(0);
				is[0]++;
				tg.set(x,y,z,i+1);
				return (Maths.abs(x)<RANGE)&&(Maths.abs(y)<RANGE)&&(Maths.abs(z)<RANGE);
			}	
		});
		
		rc.cast(0, 0, 0);
		
		for (int z=-RANGE; z<=RANGE; z++) {
			for (int y=-RANGE; y<=RANGE; y++) {
				for (int x=-RANGE; x<=RANGE; x++) {
					Integer i=tg.get(x,y,z);
					if (i==null) {
						System.out.println(x+","+y+","+z+" missed");
					} else {
						if (i>1) {
							System.out.println(x+","+y+","+z+" visited "+i+ " times");
						}
					}
				}		
			}		
		}
		
		assertEquals((long)D*D*D,tg.countNonNull());
		assertEquals((long)D*D*D,is[0]);
	}
}
