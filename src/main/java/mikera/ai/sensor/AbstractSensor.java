package mikera.ai.sensor;

import mikera.ai.Sensor;

public abstract class AbstractSensor implements Sensor {
	public double[] sense(Object a) {
		double[] output=new double[getOutputLength()];
		sense(a,output,0);
		return output;
	}
	
	public void sense(Object a, double[] dest) {
		sense(a,dest,0);
	}
	
	
}
	

