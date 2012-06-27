package mikera.stats;

import java.util.HashMap;

public class Stats {
	private static final HashMap<String,Double> stats=new HashMap<String,Double>();
	
	public static void addStat(String s, double v) {
		setStat(s,getStat(s)+v);
	}
	
	public static void setStat(String s, double v) {
		stats.put(s,Double.valueOf(v));
	}
	
	public static double getStat(String s) {
		Double d=stats.get(s);
		if (d==null) return 0.0;
		return d.doubleValue();
	}
	
	public static void printStat(String s) {
		System.out.println(s+"\t = "+getStat(s));
	}
}
