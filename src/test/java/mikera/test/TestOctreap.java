package mikera.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Iterator;

import mikera.engine.BlockVisitor;
import mikera.engine.Octreap;
import mikera.engine.Octreap.ZNode;
import mikera.util.Rand;

import org.junit.Test;

public class TestOctreap {
	@Test public void testZ() {
		assertEquals(1,Octreap.split3(1));
		assertEquals(9,Octreap.split3(3));
		assertEquals(65,Octreap.split3(5));
		
		long zz=Octreap.calculateZ(1,1,1);
		assertEquals(7,zz);
	}
	
	@Test public void testZ2() {
		assertEquals(01777,Octreap.compressInt3(01111111111));
		
		int x=Rand.d(100)-50;
		int y=Rand.d(100)-50;
		int z=Rand.d(100)-50;
		
		long zz=Octreap.calculateZ(x,y,z);
		
		assertEquals(Integer.toOctalString(x),Integer.toOctalString(Octreap.extractX(zz)));
		assertEquals(y,Octreap.extractY(zz));
		assertEquals(z,Octreap.extractZ(zz));
	}
	
	@Test public void testZSpeed() {
		int x=Rand.d(10000)-5000;
		int y=Rand.d(10000)-5000;
		int z=Rand.d(10000)-5000;
		
		for (int i=0; i<10000; i++) {
			x=Rand.d(10000)-5000;
			y=Rand.d(10000)-5000;
			z=Rand.d(10000)-5000;

			long zz=Octreap.calculateZ(x,y,z);
			if(x!=Octreap.extractX(zz)) fail();
			if(y!=Octreap.extractY(zz)) fail();
			if(z!=Octreap.extractZ(zz)) fail();
		}
	}
	
	@Test public void testNulls() {
		Octreap<Integer> m=new Octreap<Integer>();
		
		m.set(10, 10, 10, 1);
		assertEquals(1,m.countNodes());
		assertEquals(1,(int)m.get(10,10,10));
		
		m.set(10, 10, 10, null);
		assertEquals(0,m.countNodes());
		assertEquals(null,m.get(10,10,10));
		
		m.set(10, 10, 10, 1);
		m.set(-10, -10, -10, 2);
		assertEquals(2,m.countNodes());
		assertEquals(null,m.get(11,11,11));
		assertEquals(null,m.get(9,9,9));
		assertEquals(2,(int)m.get(-10,-10,-10));
		assertEquals(null,m.get(-11,-11,-11));
		assertEquals(null,m.get(-9,-9,-9));
		
		assertTrue(m.check());
	}
	
	@Test public void testCut() {
		Octreap<Integer> m=new Octreap<Integer>();
		m.setRange(1, 10, 1);
		assertEquals(1,(int)m.get(5));
		
		m.setRange(4,6,null);
		assertEquals(null,m.get(5));
		assertEquals(2,m.countNodes());
		assertTrue(m.check());

		m.setRange(8,10,null);
		assertEquals(2,m.countNodes());
		assertTrue(m.check());
		
		m.setRange(5,9,null);
		assertEquals(1,m.countNodes());
		assertTrue(m.check());
	}
	
	@Test public void testSet() {
		Octreap<Integer> m=new Octreap<Integer>();
		m.setRange(1,2,1);
		assertEquals(1,m.countLevels());
		m.setRange(2,3,2);
		m.setRange(3,7,3);
		m.setRange(4,6,4);
		m.setRange(5,5,5);
		assertTrue(m.check());		
		assertTrue(1<m.countLevels());
		
		for (int i=1; i<5; i++) {
			assertEquals(i,(int)m.get(i));
		}
		
		for (int i=1; i<10; i++) {
			m.setRange(i, i, null);
			assertTrue(m.check());	
		}
		
		assertEquals(0,m.countNodes());
	}
	
	@Test public void testClone() {
		Octreap<Integer> m=new Octreap<Integer>();
		m.setRange(1,2,1);
		m.setRange(2,3,2);
		m.setRange(3,7,3);
		m.setRange(4,6,4);
		m.setRange(5,5,5);

		Octreap<Integer> c=m.clone();
		
		
		assertEquals(m.countNodes(),c.countNodes());
		assertEquals(m.countArea(),c.countArea());
		
		assertTrue(m.equals(c));
		
		m.delete(c);
		assertEquals(0,m.countNodes());
	}
	
	@Test public void testFillSpace() {
		Octreap<Integer> m=new Octreap<Integer>();
		m.fillSpace(3);
		assertEquals((Integer)3,m.get(100,1000,10000));
		assertEquals((Integer)3,m.get(-100,-10000,-1000));
		assertEquals(1,m.countNodes());
	}
	
	@Test public void testSlice() {
		Octreap<Integer> m=new Octreap<Integer>();
		m.setRange(1, 10, 1);
		assertEquals(1,(int)m.get(5));
		
		m.setRange(1,2,null);
		assertEquals(1,m.countNodes());
		assertTrue(m.check());

		m.setRange(8,10,null);
		assertEquals(1,m.countNodes());
		assertTrue(m.check());
		
		m.setRange(2,9,null);
		assertEquals(0,m.countNodes());
		assertTrue(m.check());
	}
	
	@Test public void testRandom() {
		Octreap<Integer> m=new Octreap<Integer>();
		HashMap<Integer,Integer> h=new HashMap<Integer,Integer>();
		
		for (int i=0; i<3000; i++) {
			int x=Rand.d(11)-6;
			int y=Rand.d(11)-6;
			int z=Rand.d(11)-6;
			m.set(x,y,z,i%10);
			h.put(x+100*y+10000*z,i%10);
			//System.out.println(i);
			//assertTrue(m.check());
		}
		// assertEquals(1331,m.countArea());
		assertTrue(m.countArea()>1000);
		assertEquals(h.size(),m.countArea());
		
		for (int i=0; i<10000; i++) {
			int x=Rand.d(11)-6;
			int y=Rand.d(11)-6;
			int z=Rand.d(11)-6;
			Integer mi=0;
			for (int ii=0; ii<1; ii++) {
				mi=m.get(x,y,z);
			}
			Integer hi=h.get(x+100*y+10000*z);
			assertEquals(hi,mi);
		}
		
		assertTrue(m.check());
		
		int i=0;
		Iterator<ZNode> it=m.getNodeIterator();
		while (it.hasNext()) {
			assertNotNull(it.next());
			i++;
		}
		assertEquals(m.countNodes(),i);
	}
	
	@Test public void testSpeed() {
		Octreap<Integer> m=new Octreap<Integer>();
		
		for (int i=0; i<1000; i++) {
			int x1=Rand.d(110);
			int y1=Rand.d(110);
			int z1=Rand.d(110);
			//int x2=Rand.d(110);
			//int y2=Rand.d(110);
			//int z2=Rand.d(110);
			int x2=x1+Rand.r(4);
			int y2=y1+Rand.r(4);
			int z2=z1+Rand.r(4);
			m.setBlock(x1,y1,z1,x2,y2,z2,Rand.d(10));
		}
		assertTrue(m.check());
		
		//System.out.println(m.countNodes());
		//System.out.println(m.countArea());

		for (int i=0; i<10000; i++) {
			int x1=Rand.d(110);
			int y1=Rand.d(110);
			int z1=Rand.d(110);
			
			m.get(x1,y1,z1);
		}		
		
	}
	
	@Test public void testIterator() {
		Octreap<Integer> m=new Octreap<Integer>();
		
		assertEquals(false,m.getNodeIterator().hasNext());
		assertEquals(null,m.nextNode(0));
		
		m.set(1, 1, 1, 1);
		
		assertNotNull(m.nextNode(0));
		assertEquals(true,m.getNodeIterator().hasNext());
		assertEquals(m.nextNode(0),m.getNodeIterator().next());	
		assertEquals(null,m.nextNode(100));

	}
	
	@Test public void testSeries() {
		Octreap<Integer> m=new Octreap<Integer>();
		
		for (int i=-10; i<10; i++) {
			m.set(i, 10-i, 2*i, i);
		}
		
		assertEquals(20,m.countNodes());
		
		for (int i=-10; i<10; i++) {
			assertEquals(i,(int)m.get(i, 10-i, 2*i));
		}
		
		assertTrue(m.check());

	}
	
	@Test public void testMerge() {
		Octreap<Integer> m=new Octreap<Integer>();
		
		m.set(0, 0, 0, 1);
		assertEquals(1,m.countNodes());

		// this should merge
		m.set(1, 0, 0, 1);
		assertEquals(1,m.countNodes());

		// this should not!
		m.set(1, 1, 0, 1);
		assertEquals(2,m.countNodes());
		
		// this should combine all
		m.set(0, 1, 0, 1);
		assertEquals(1,m.countNodes());
		
		assertTrue(m.check());

	}
	
	@Test public void testBlock() {
		Octreap<Integer> m=new Octreap<Integer>();
		
		m.setBlock(0,0,0,1,1,1,2);
		assertEquals(null,m.get(-1,-1,-1));
		assertEquals(null,m.get(2,2,2));
		assertEquals(2,(int)m.get(0,0,0));
		assertEquals(2,(int)m.get(0,0,1));
		assertEquals(2,(int)m.get(0,1,0));
		assertEquals(2,(int)m.get(0,1,1));
		assertEquals(2,(int)m.get(1,0,0));
		assertEquals(2,(int)m.get(1,0,1));
		assertEquals(2,(int)m.get(1,1,0));
		assertEquals(2,(int)m.get(1,1,1));
		assertEquals(8,m.countArea());
		assertEquals(1,m.countNodes());

		BCounter bcounter=new BCounter();
		m.visitBlocks(bcounter);
		assertEquals(8,m.blockSize(0, 0, 0));
		assertEquals(8,bcounter.size);
		assertEquals(1,bcounter.count);
		
		assertEquals(8,bcounter.oct.countArea());
		assertEquals(1,bcounter.oct.countNodes());
		
		m.clear();
		assertEquals(0,m.countNodes());
		
		assertEquals(73,Octreap.fillBits3(64));
	}
	
	@Test public void testSetBlock() {
		Octreap<Integer> m=new Octreap<Integer>();
		
		m.setBlock(-2,-2,-2,1,1,1,2);
		assertEquals(2,(int)m.get(-1,-1,-1));
		assertEquals(2,(int)m.get(-2,-2,-2));
		assertEquals(null,m.get(2,2,2));
		assertEquals(2,(int)m.get(0,0,0));
		assertEquals(2,(int)m.get(0,0,1));
		assertEquals(2,(int)m.get(0,1,0));
		assertEquals(2,(int)m.get(0,1,1));
		assertEquals(2,(int)m.get(1,0,0));
		assertEquals(2,(int)m.get(1,0,1));
		assertEquals(2,(int)m.get(1,1,0));
		assertEquals(2,(int)m.get(1,1,1));
		assertEquals(64,m.countArea());
		assertEquals(8,m.countNodes());
		
		assertEquals(1,m.blockSize(1, 1, 1));
		assertEquals(8,m.blockSize(0, 0, 0));
	}
	
	private static class BCounter extends BlockVisitor<Integer>  {
		long count=0;
		long size=0;
		Octreap<Integer> oct=new Octreap<Integer>();
		
		public Object visit(int x1, int y1, int z1, int x2, int y2, int z2,
				Integer value) {
			count+=1;
			size+=((long)(x2-x1+1))*(y2-y1+1)*(z2-z1+1);
			
			oct.setBlock(x1, y1, z1, x2, y2, z2, value);
			return null;
		}	
	};

	@Test public void testBlock2x() {
		Octreap<Integer> m=new Octreap<Integer>();
			
		m.setBlock(-1,0,0,1,0,0,3);
		assertEquals(2,m.countNodes());
		assertEquals(3,m.countArea());
		assertEquals(null,m.get(-1,-1,-1));
		assertEquals(3,(int)m.get(1,0,0));
		assertEquals(3,(int)m.get(0,0,0));
		assertEquals(3,(int)m.get(-1,0,0));
		assertEquals(null,m.get(2,2,2));
		assertEquals(3,(int)m.get(0,0,0));
	}
	
	@Test public void testBlock2y() {
		Octreap<Integer> m=new Octreap<Integer>();
			
		m.setBlock(0,-1,0,0,1,0,3);
		assertEquals(3,m.countNodes());
		assertEquals(3,m.countArea());
		assertEquals(null,m.get(-1,-1,-1));
		assertEquals(3,(int)m.get(0,1,0));
		assertEquals(3,(int)m.get(0,0,0));
		assertEquals(3,(int)m.get(0,-1,0));
		assertEquals(null,m.get(2,2,2));
		assertEquals(3,(int)m.get(0,0,0));
		
		int i=0;
		Iterator<ZNode> it=m.getNodeIterator();
		while (it.hasNext()) {
			assertNotNull(it.next());
			i++;
		}
		assertEquals(m.countNodes(),i);
	}
	
	@Test public void testBlock2z() {
		Octreap<Integer> m=new Octreap<Integer>();
			
		m.setBlock(0,0,-1,0,0,1,3);
		assertEquals(3,m.countNodes());
		assertEquals(3,m.countArea());
		assertEquals(null,m.get(-1,-1,-1));
		assertEquals(3,(int)m.get(0,0,1));
		assertEquals(3,(int)m.get(0,0,0));
		assertEquals(3,(int)m.get(0,0,-1));
		assertEquals(null,m.get(2,2,2));
		assertEquals(3,(int)m.get(0,0,0));
	}
	
	@Test public void testExpand() {
		Octreap<Integer> m=new Octreap<Integer>();
			
		m.setBlock(0,0,0,0,0,1,1); // 2 cells
		m.setBlock(100,0,0,100,0,0,1);  // 1 cell

		Octreap<Integer> e=m.expand();
		
		assertEquals(36+27,e.countArea());
	}
	
	@Test public void testChange() {
		Octreap<Integer> m=new Octreap<Integer>();
			
		m.set(0,0,0,1); 
		m.set(0,0,1,2); 
		m.set(0,1,0,3); 
		m.set(0,1,1,4); 
		m.set(1,0,0,5); 
		m.set(1,0,1,6); 
		m.set(1,1,0,7); 
		m.set(1,1,1,8); 

		assertEquals(8,m.countNodes());

		// one change should result in merge
		m.changeAll(1,5);
		m.check();
		assertEquals(7,m.countNodes());
		
		m.changeAll(10);
		
		m.check();
		assertEquals(10,(int)m.get(0,0,1));
		assertEquals(8,m.countArea());
		assertEquals(1,m.countNodes());
	}
	
	@Test public void testBlock3() {
		Octreap<Integer> m=new Octreap<Integer>();
		
		m.setBlock(-1,-1,-1,1,1,1,2);
		assertEquals(14,m.countNodes());
		assertEquals(27,m.countArea());
		assertEquals(2,(int)m.get(-1,-1,-1));
		assertEquals(null,m.get(2,2,2));
		assertEquals(null,m.get(-2,-2,-2));
		assertEquals(2,(int)m.get(0,0,0));
		assertEquals(2,(int)m.get(0,0,1));
		assertEquals(2,(int)m.get(0,1,0));
		assertEquals(2,(int)m.get(0,1,1));
		assertEquals(2,(int)m.get(1,0,0));
		assertEquals(2,(int)m.get(1,0,1));
		assertEquals(2,(int)m.get(1,1,0));
		assertEquals(2,(int)m.get(1,1,1));
		assertEquals(27,m.countArea());

		m.clear();
		assertEquals(0,m.countNodes());
	}
		
	@Test public void testRandomBlock() {
		Octreap<Integer> m=new Octreap<Integer>();
		HashMap<Integer,Integer> h=new HashMap<Integer,Integer>();
		
		for (int i=0; i<3000; i++) {
			int x=Rand.d(11)-6;
			int y=Rand.d(11)-6;
			int z=Rand.d(11)-6;
			m.setBlock(x,y,z,x,y,z,i%10);
			h.put(x+100*y+10000*z,i%10);
			//System.out.println(i);
			//assertTrue(m.check());
		}
		// assertEquals(1331,m.countArea());
		assertTrue(m.countArea()>1000);
		assertEquals(h.size(),m.countArea());
		
		for (int i=0; i<10000; i++) {
			int x=Rand.d(11)-6;
			int y=Rand.d(11)-6;
			int z=Rand.d(11)-6;
			Integer mi=0;
			for (int ii=0; ii<1; ii++) {
				mi=m.get(x,y,z);
			}
			Integer hi=h.get(x+100*y+10000*z);
			assertEquals(hi,mi);
		}
		
		assertTrue(m.check());
		
		
		for (int i=0; i<100; i++) {
			int x1=Rand.d(11)-6;
			int y1=Rand.d(11)-6;
			int z1=Rand.d(11)-6;
			int x2=Rand.d(11)-6;
			int y2=Rand.d(11)-6;
			int z2=Rand.d(11)-6;
			m.setBlock(x1,y1,z1,x2,y2,z2,i%10);
			assertTrue(m.check());
			assertEquals(i%10,(int)m.get(Rand.range(x1, x2),Rand.range(y1, y2),Rand.range(z1, z2)));

		}
	}
}
