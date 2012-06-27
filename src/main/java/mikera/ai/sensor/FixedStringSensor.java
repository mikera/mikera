  package mikera.ai.sensor;

import mikera.util.Arrays;
import mikera.util.Maths;

public final class FixedStringSensor extends AbstractSensor {

	private final int length;
	
	public static FixedStringSensor create(int fixedLength) {
		return new FixedStringSensor(fixedLength);
	}

	private FixedStringSensor(int fixedLength) {
		length=fixedLength;
	}
	
	public int getOutputLength() {
		return length*8;
	}

	public void sense(Object a, double[] dest, int offset) {
		String s=(String)a;
		
		int stringLength=s.length(); 
		
		int scanLength=Maths.min(length, stringLength);
		
		for (int i=0; i<scanLength; i++) {
			int c=s.charAt(i);
		
			CharSensor.sense8BitChar(c,dest,offset+i*8);
		}
		
		for (int di=scanLength*8; di<length*8; di++) {
			dest[offset+di]=0;
		}
	}


}
