package mikera.util;

public class Statistics {
	public double productOfNormalsStandardDeviation(double u1, double sd1, double u2, double sd2) {
		return Math.sqrt((u1*u1)*(sd2*sd2)+(u2*u2)*(sd1*sd1)+(sd1*sd1)*(sd2*sd2));
	}
}
