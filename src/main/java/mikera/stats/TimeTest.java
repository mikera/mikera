package mikera.stats;

import java.util.Random;

import mikera.engine.TreeGrid;
import mikera.util.TextUtils;

/**
 * Testing class to test a specific function
 * @author Mike
 *
 */
public class TimeTest {

	public static final int PRERUNS=100;
	public static final int RUNS=100;
	static int count=0;
	
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

		System.out.println("time = "+TextUtils.leftPad(Long.toString((atime-btime)/RUNS),12)+" ns");
	}
	
	static Random rand=new Random();
	static TreeGrid<Integer> tg=new TreeGrid<Integer>();
	static int[] ia=new int[100];
	
	private static void setup() {
		tg.set(1,1,1,2);
	}
	
	private static void a() {
		//long l=rand.nextLong();
		tg.get(2,2,2);
		//ia[20]=5;
		count++;
	}
	
	private static void b() {
		count++;
	}
}
