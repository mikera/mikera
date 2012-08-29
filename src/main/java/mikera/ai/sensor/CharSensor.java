package mikera.ai.sensor;


public final class CharSensor extends AbstractSensor {

	public int getOutputLength() {
		return 8;
	}

	public void sense(Object a, double[] dest, int offset) {
		Character ch=(Character)a;
		
		int c=ch;
		
		sense8BitChar(c,dest,offset);
	}
	
	public static final void sense8BitChar(int c, double[] dest, int offset) {
		for (int i=0; i<8; i++) {
			dest[i+offset]=c&1;
			c>>=1;
		}		
	}
}
