package mikera.engine;

import static org.junit.Assert.assertEquals;
import mikera.engine.ArrayGrid;
import mikera.engine.IntGrid;

import org.junit.Test;

public class TestIntGrid {
	@Test public void test1() {
		IntGrid ig=new IntGrid();
		
		ig.set(0, 0, 0, 1);
		ig.set(10, 10, 10, 1);
		ig.setBlock(-10, -10, -10,-10,-10,-10, 1);
		assertEquals(1,ig.get(0,0,0));
		assertEquals(1,ig.get(10,10,10));
		assertEquals(1,ig.get(-10,-10,-10));
		assertEquals(0,ig.get(5,5,5));
		
		assertEquals(3,ig.countNonZero());	
		assertEquals(21*21*21,ig.dataLength());
	}
	
	@Test public void test2() {
		IntGrid ig=new IntGrid();
		
		ig.setBlock(3, 3, 3,10,10,10, 1);
		assertEquals(0,ig.get(0,0,0));
		assertEquals(1,ig.get(10,10,10));
		assertEquals(1,ig.get(3,3,3));
		assertEquals(0,ig.get(11,11,11));
		
		assertEquals(512,ig.countNonZero());	
		assertEquals(512,ig.dataLength());
	}
	
	@Test public void testGrid() {
		ArrayGrid<Integer> ig=new ArrayGrid<Integer>();
		
		ig.setBlock(3, 3, 3,10,10,10, 1);
		assertEquals(null,ig.get(0,0,0));
		assertEquals(1,(int)ig.get(10,10,10));
		assertEquals(1,(int)ig.get(3,3,3));
		assertEquals(null,ig.get(11,11,11));
		
		assertEquals(512,ig.countNonNull());	
		assertEquals(512,ig.dataLength());
	}
}
