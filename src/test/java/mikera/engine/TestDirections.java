package mikera.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import mikera.engine.Dir;
import mikera.engine.Octreap;
import mikera.engine.PathFinder;
import mikera.engine.PathFinder.PathNode;
import mikera.engine.TreeGrid;

import org.junit.Test;

public class TestDirections {
	@Test public void testDirArrays() {
		byte[] td=new byte[27];
		
		for (int i=0; i<Dir.MAX_DIR; i++) {
			td[i]++;
		}
		for (int i=0; i<Dir.MAX_DIR; i++) {
			td[Dir.distanceOrderedDirection(i)]++;
		}
		
		for (int i=0; i<td.length; i++) {
			assertEquals(100*i+2, 100*i+td[i]);
		}
	}
	
	@Test public void testDirDirections() {
		Octreap<Integer> o=new Octreap<Integer>();
		
		for (int i=0; i<Dir.MAX_DIR; i++) {
			int d=i;
			int dx=Dir.DX[d];
			int dy=Dir.DY[d];
			int dz=Dir.DZ[d];
			o.set(dx,dy,dz,d);
		}
		assertEquals(27,o.countArea());
		
		Dir.validate();
	}
	
	@Test public void testDirCalcs() {
		for (int i=0; i<Dir.ALL_DIRECTIONS_3D.length; i++) {
			int d=Dir.ALL_DIRECTIONS_3D[i];
			int dx=Dir.DX[d];
			int dy=Dir.DY[d];
			int dz=Dir.DZ[d];

			int dcalc=Dir.getDir(dx, dy, dz);
			
			assertEquals(d,dcalc);
		}
	}
	
	@Test public void testPathFinding() {
		PathFinder pf=new PathFinder();
		
		final TreeGrid<Float> costs=new TreeGrid<>();
		pf.setCostFunction(new PathFinder.CostFunction() {
			@Override
			public float moveCost(int x, int y, int z, int tx, int ty, int tz) {
				Float f=costs.get(tx, ty, tz);
				if (f==null) return -1;
				return f.floatValue();
			}
		});
		costs.setBlock(0,0,0,10,10,0, 1.0f); // area
		costs.setBlock(5,0,0,5,9,0, -1.0f); // wall 1
		costs.setBlock(7,1,0,7,10,0, -1.0f); // wall 2
		
		pf.pathFind(0, 0, 0, 0,0,0);
		assertTrue(pf.isFound());
		assertEquals(0,pf.foundNode().travelled,0.01f);
		
		
		pf.pathFind(0, 0, 0, 10,10,0);
		//System.out.println("Nodes: "+pf.nodeCount);
		//System.out.println("Costs: "+pf.costCount);
		assertTrue(pf.isFound());
		assertEquals(30,pf.foundNode().travelled,0.01f);
		
		PathNode dpn=pf.getLastDirect();
		assertEquals(4,dpn.x);
		assertEquals(9,dpn.y);
		
		ArrayList<PathNode> al=pf.getPathNodes();
		assertEquals(31,al.size());
		assertEquals(pf.foundNode(),al.get(al.size()-1));
		assertTrue(al.get(0).last==null); // first node
		
		costs.setBlock(5,0,0,5,10,0, -1.0f); // full wall
		pf.pathFind(0, 0, 0, 10,10,0);
		assertTrue(!pf.isFound());
		//System.out.println("Nodes: "+pf.nodeCount);
		//System.out.println("Costs: "+pf.costCount);
	}
}
