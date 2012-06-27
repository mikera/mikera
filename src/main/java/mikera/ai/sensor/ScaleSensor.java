package mikera.ai.sensor;

/**
 * Sensor that scales a double input
 * @author Mike
 *
 */
public final class ScaleSensor extends AbstractSensor {
	private final double mean;
	private final double invSD;
	
	public static ScaleSensor create(double mean, double sd) {
		return new ScaleSensor(mean,sd);
	}
	
	private ScaleSensor(double mean, double sd) {
		this.mean=mean;
		this.invSD=1.0/sd;
	}
	
	
	public int getOutputLength() {
		return 1;
	}

	public void sense(Object a, double[] dest, int offset) {
		double d=(Double)a;
		
		dest[offset]=(d-mean)*invSD;
	}

}
