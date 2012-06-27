package mikera.stats;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import mikera.persistent.PersistentHashMap;
import mikera.persistent.PersistentMap;
import mikera.util.Bits;
import mikera.util.Maths;
import mikera.util.Rand;
import mikera.util.TextUtils;

public class PerformanceTest {

	public static final int PRERUNS=10;
	public static final int RUNS=100;
	
	public static void main(String[] args) {
		setup();
		
		for (int i=0; i<PRERUNS; i++) {
			a();
			b();
		}
		
		long astart=System.nanoTime();
		for (int i=0; i<RUNS; i++) {
			a();
		}		
		long atime=System.nanoTime()-astart;
		
		long bstart=System.nanoTime();
		for (int i=0; i<RUNS; i++) {
			b();
		}		
		long btime=System.nanoTime()-bstart;

		System.out.println("a time = "+TextUtils.leftPad(Long.toString(atime/RUNS),12)+" ns");
		System.out.println("b time = "+TextUtils.leftPad(Long.toString(btime/RUNS),12)+" ns");
	}
	
	/**
	 * Setup code here
	 */
	
	/*
	static Octreap<Integer> o1=new Octreap<Integer>();
	static Grid<Integer> g1;
	static Grid<Integer> g2;
	private static void setup() {
		g1=new Octreap<Integer>();
		g2=new TreeGrid<Integer>();
	}
	*/
	
	/**
	 * Enter code to time here
	 */
	
	/*
	private static void a() {
		g1.set(0,0,0,1);
		g1.get(0,0,0);
		//testGrid(g1);
	}
	
	private static void b() {
		g2.set(0,0,0,1);
		g2.get(0,0,0);
		//testGrid(g2);
	}
	
	public static void testGrid(Grid<Integer> g) {
		g.clear();
		g.setBlock(0, 0, 0, 100, 100, 100, 1);
		g.setBlock(0, 0, 0, 3, 3, 3, 2);
		g.set(2,2,2,3);
	}
	
	*/
	
	static Map<Integer,String> hm;
	static PersistentMap<Integer,String> pm;
	private static void setup() {
		hm=new HashMap<Integer,String>();
		pm=PersistentHashMap.create();
	}
	
	public static void a1() {
		hm.clear();
		for (int i=0; i<100; i++) {
			int key=Rand.r(100);
			String value=Rand.nextString();
			hm.put(key, value);
			
			int delKey=Rand.r(100);
			hm.remove(delKey);
			
			for (int l=0; l<50; l++) {
				hm.get(Rand.r(100));
			}
			
			for (String s: hm.values()) {
				if (s.equals("fvfieuvgfeoivboi")) throw new Error();
			}
			((HashMap<Integer, String>)hm).clone();
		}
	}
	
	static int one=1;

	public static int addMillion() {
		int result=0;
		for (int i=0; i<1000000; i++) {
			result+=one;
		}
		return result;
	}
	
	public static void b1() {
		pm=PersistentHashMap.create();
		for (int i=0; i<100; i++) {
			int key=Rand.r(100);
			String value=Rand.nextString();
			pm=pm.include(key,value);
			
			int delKey=Rand.r(100);
			pm=pm.delete(delKey);
			
			for (int l=0; l<50; l++) {
				pm.get(Rand.r(100));
			}
			
			for (String s: pm.values()) {
				if (s.equals("fvfieuvgfeoivboi")) throw new Error();				
			}
			pm.clone();
		}
	}
	
	public static void a2() {
		Bits.countSetBits(0xCAFEBABE);
		
	}
	
	public static void b2() {
		Integer.bitCount(0xCAFEBABE);

	}
	
	static int z=0;
	static Random random=new Random();
	
	static volatile String sa = "gyu";
	static volatile String sb = sa;
	
	public static void a() {
        long count = 0;
        for (long i = 0; i < 10000000; i++) {
            if (sa==sb) count++;
        }
    }	
	

	
	public static void b() {
        int count =addMillion();
	}
	
}
