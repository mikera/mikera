package mikera.math;

public class Utils {

	public static double distanceSquared(int x1, int y1, int z1, double x2, double y2, double z2) {
		double dx=x1-x2;
		double dy=y1-y2;
		double dz=z1-z2;
		return (dx*dx)+(dy*dy)+(dz*dz);
	}

	public static double distanceSquared(double dx, double dy, double dz) {
		return (dx*dx)+(dy*dy)+(dz*dz);
	}

}
