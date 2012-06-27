package mikera.ai.sensor;

import java.util.Arrays;

import mikera.ai.sensor.AbstractSensor;
import mikera.ai.Sensor;

public class MaybeSensor extends AbstractSensor {
	final Sensor source;
	final int length;
	
	public MaybeSensor(Sensor source) {
		this.source=source;
		this.length=source.getOutputLength()+1;
	}
	
	public int getOutputLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void sense(Object a, double[] dest, int offset) {
		if (a==null) {
			dest[0]=1;
			Arrays.fill(dest, 1, length, 0);
		} else {
			dest[0]=0;
			source.sense(a,dest,offset+1);
		}
	}

}
