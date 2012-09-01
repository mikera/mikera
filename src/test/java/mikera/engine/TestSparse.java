package mikera.engine;

import static org.junit.Assert.assertEquals;
import mikera.engine.SparseArray;
import mikera.engine.SparseGrid;
import mikera.math.Bounds4i;
import mikera.persistent.SparseMap;
import mikera.util.Rand;

import org.junit.Test;

public class TestSparse {
	@Test public void testSparseArray() {
		
		SparseArray<Integer> sa=new SparseArray<>();
		
		sa.set(0, 1);
		assertEquals(1,(int)sa.get(0));
		
		sa=sa.grow();
		assertEquals(1,(int)sa.get(0));
		
	}
	
	@Test public void testSparseGrid() {
		assertEquals(29120,SparseGrid.calculateIndex(10, 10, 10));
	}
	
	
	@Test public void testSparseMap() {
		assertEquals(-1,SparseMap.baseOffset(2));
		assertEquals(-5,SparseMap.baseOffset(4));
		assertEquals(-21,SparseMap.baseOffset(6));
		assertEquals(-85,SparseMap.baseOffset(8));
		
		SparseMap<Integer> sm=new SparseMap<>();
		
		sm=sm.update(1, 1, 2);
		assertEquals(null,sm.get(-10, -10));
		assertEquals(null,sm.get(10, 10));
		assertEquals(2,(int)sm.get(1, 1));
		assertEquals(null,sm.get(0, 0));
		
		assertEquals("SparseMap contents:\n[1,1] -> 2\n",sm.toString());
		
		sm=sm.update(10, 10, 3);
		assertEquals(2,(int)sm.get(1, 1));
		assertEquals(null,sm.get(0, 0));
		assertEquals(null,sm.get(-10, -10));
		assertEquals(3,(int)sm.get(10, 10));
		
		sm=sm.update(10, -10, 4);
		sm=sm.clear(10, 10);
		assertEquals(2,(int)sm.get(1, 1));
		assertEquals(null,sm.get(0, 0));
		assertEquals(4,(int)sm.get(10, -10));
		assertEquals(null,sm.get(10, 10));
		
		assertEquals(2,sm.countNotNull());	
		sm=sm.update(5, 5, 3);
		assertEquals(3,sm.countNotNull());
		
		sm=new SparseMap<>();
		assertEquals(0,sm.countNotNull());
		for (int y=-5; y<5; y++) {
			for (int x=-5; x<5; x++) {
				sm=sm.update(x, y, x*y);				
			}					
		}
		assertEquals(100,sm.countNotNull());
		
		for (int y=-50; y<50; y++) {
			for (int x=-50; x<50; x++) {
				sm=sm.update(x+Rand.d(6), y+Rand.d(6), x*y);				
				sm=sm.update(x+Rand.d(6), y+Rand.d(6), null);				
			}					
		}
	}
	
	@Test public void testSparseMapBounds() {
		Bounds4i b=null;
		SparseMap<Integer> sm=new SparseMap<>();
		sm=sm.update(1,5,2);
		sm=sm.update(1,109,2);
		b=sm.getNonNullBounds();
		assertEquals(1,b.xmin);
		assertEquals(1,b.xmax);
		assertEquals(5,b.ymin);
		assertEquals(109,b.ymax);

	}

}
