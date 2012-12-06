package mikera.engine;


public class FluidSystem {
	public boolean isWaterPassable(int x, int y, int z) {
		return ((z>=0)&&(y==0)
				&&((Math.abs(x)==1))||((z>0)&&(x==0)));
	}
	
	public static class FluidNode {
		public int flowDirs=0;
		public int flowX=0;
		public int flowY=0;
		public int flowZ=0;
		public int type=0;
		public int volume=0;
		public int topPressure=0;
 	}
	
	public void fluidStep() {
		
		// TODO: move fluids
	}
	
	public PointVisitor<Integer> pressureCalc=new PointVisitor<Integer>() {

		@Override
		public Object visit(int x, int y, int z, Integer value) {
			
			return null;
		}
		
	};
}
