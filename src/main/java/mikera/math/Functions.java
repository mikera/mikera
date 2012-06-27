package mikera.math;

public class Functions {
	public static VectorFunction createLandscapeFunction(int d) {
		// TODO: Solve bug in this code leading to funny lines
		
		VectorFunction f=VF.tiledCloudFunction(2,3,d);
		
		f=VF.zeroExtendComponents(f, 4); // make into 4-vector
		f=VF.add(f, new Vector(0,0,0,1));
		f=VF.perturb(f,VF.tiledCloudFunction(2, 2,d),1.0f);
		f=VF.scale(f, d);
		
		//f=VF.madd(f, VF.scale(VF.noiseFunction(2, 4),30),0.03);

		return f;
	}
}
