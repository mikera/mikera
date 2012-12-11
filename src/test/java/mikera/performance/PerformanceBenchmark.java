package mikera.performance;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;


public class PerformanceBenchmark extends SimpleBenchmark {
	public long r=0;
	
	public void timeOriginal(int runs) {
		for (int i=0; i<runs; i++) {
			long input = i;
			long result=
					(input & 0x000000FF)       | 
					(input & 0x0000FF00) <<  8 | 
					(input & 0x00FF0000) << 16 | 
					(input & 0xFF000000) << 24;		
			r=result;
		}
	}
	
	public void timeImproved(int runs) {
		for (int i=0; i<runs; i++) {
			long input = i;
			long a = (input | (input << 16));
			long result = (a & 0xFF000000FFL) + ((a & 0xFF000000FF00L) <<8);
			r=result;		
		}
	}
	
	public void timeImprovedWithMultiply(int runs) {
		for (int i=0; i<runs; i++) {
			long input = i;
			long a = (input * 0x10001);
			long result = (a & 0xFF000000FFL) + ((a & 0xFF000000FF00L) <<8);
			r=result;		
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PerformanceBenchmark().run();
	}

	private void run() {
		Runner runner=new Runner();
		runner.run(new String[] {this.getClass().getCanonicalName()});
	}

}
