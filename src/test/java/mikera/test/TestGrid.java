package mikera.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import mikera.engine.ArrayGrid;
import mikera.engine.BlockVisitor;
import mikera.engine.Grid;
import mikera.engine.Octreap;
import mikera.engine.PersistentTreeGrid;
import mikera.engine.SparseGrid;
import mikera.engine.TreeGrid;
import mikera.util.Rand;

import org.junit.Test;

public class TestGrid {
	@Test public void testAll() {
		testGrid(new Octreap<Integer>());
		testGrid(new ArrayGrid<Integer>());
		testGrid(new TreeGrid<Integer>());
		testGrid(new SparseGrid<Integer>());
		testGrid(new PersistentTreeGrid<Integer>());
	}
	
	public void testGrid(Grid<Integer> g) {
		testAllNull(g);
		testEmptyGrid(g);
		testSet(g);
		testSetBlock(g);
		testVisitBlock(g);
		testPaste(g);
		
		// finally check all clear
		testAllNull(g);
	}
	
	public void testEmptyGrid(Grid<Integer> g) {
		assertEquals(null,g.get(0, 0, 0));
		assertEquals(null,g.get(-10, -10, -10));
		g.validate();
	}
	
	public void testSet(Grid<Integer> g) {
		g=g.set(10,10,10, 1);
		assertEquals(1,g.countNonNull());
		g=g.set(-1,-1,-1, 1);
		assertEquals(2,g.countNonNull());

		assertEquals(1,(int)g.get(10, 10, 10));
		assertEquals(1,(int)g.get(-1, -1, -1));

		g=g.clear();
		assertEquals(null,g.get(0, 0, 0));
		assertEquals(null,g.get(10, 10, 10));
	}
	
	public void testSetBlock(Grid<Integer> g) {
		g=g.setBlock(0,0,0,0,0,1,1);
		assertEquals(2,g.countNonNull());

		g=g.setBlock(0,0,0,1,1,1,1);
		assertEquals(8,g.countNonNull());

		g=g.setBlock(0,0,0,10,10,10,1);
		assertEquals(1331,g.countNonNull());
		
		assertEquals(1,(int)g.get(10, 10, 10));
		assertEquals(null,g.get(-1, -1, -1));
		assertEquals(1,(int)g.get(Rand.r(11), Rand.r(11), Rand.r(11)));
		
		g=g.setBlock(-5,-5,-5,5,5,5,2);
		assertEquals(1,(int)g.get(10, 10, 10));
		assertEquals(2,(int)g.get(0, 0, 0));
		assertEquals(2,(int)g.get(-1, -1, -1));
		assertEquals(null,g.get(-6, -6, -6));

		g=g.setBlock(-2,-2,-2,2,2,2,null);
		assertEquals(2,(int)g.get(-3, -3, -3));
		assertEquals(null,g.get(-1, -1, -1));
		assertEquals(2,(int)g.get(3, 2, 2));
			
		g.validate();
		g.clear();
	}
	
	public void testAllNull(Grid<Integer> g) {
		assertEquals(0,g.countNonNull());
		assertNull(g.get(0, 0, 0));
		
	}

	
	public void testPaste(Grid<Integer> g) {
		ArrayGrid<Integer> ag=new ArrayGrid<Integer>();
		ag=ag.setBlock(0, 0, 0, 5,5, 5, 1);
		assertEquals(216,ag.dataLength());
		
		g=g.paste(ag);	
		g=g.paste(ag,-2,-2,-2);
		
		assertNull(g.get(-3, -3, 3));
		assertEquals(1,(int)g.get(-2, -2, -2));
		assertEquals(1,(int)g.get(5, 5, 5));
		assertNull(g.get(6, 6, 6));
		assertEquals(216+216-64,g.countNonNull());
		
		ag=ag.clear();
		ag=ag.paste(g);
		assertEquals(512,ag.dataLength());
		assertNull(ag.get(-3, -3, 3));
		assertEquals(1,(int)ag.get(-2, -2, -2));
		assertEquals(1,(int)ag.get(5, 5, 5));
		assertNull(ag.get(6, 6, 6));
		assertEquals(216+216-64,ag.countNonNull());

		g.validate();
		g.clear();
	}
	
	public void testVisitBlock(Grid<Integer> g) {
		BCounter bc=new BCounter();	
		g=g.setBlock(-5,-5,-5,4,4,4,1);
		
		g.visitBlocks(bc);
		assertEquals(1000,bc.size);
		
		bc=new BCounter();
		g.visitBlocks(bc,0,0,0,9,9,9);
		assertEquals(125,bc.size);
		
		bc=new BCounter();
		g.visitBlocks(bc,-9,-9,-9,0,0,0);
		assertEquals(216,bc.size);
		
		bc=new BCounter();
		g.visitBlocks(bc,-1,-1,-1,1,1,1);
		assertEquals(27,bc.size);
		
		g.clear();
	}
	
	@SuppressWarnings("unused")
	private static class BCounter extends BlockVisitor<Integer>  {
		public long count=0;
		public long size=0;
		
		public Object visit(int x1, int y1, int z1, int x2, int y2, int z2,
				Integer value) {
			count+=1;
			size+=((long)(x2-x1+1))*(y2-y1+1)*(z2-z1+1);
			
			return null;
		}	
	};

	
	@Test public void testTreeGidBlockSet() {
		TreeGrid<Integer> tg=new TreeGrid<Integer>();
		
		tg.set(0,0,0, 1);
	}

}
