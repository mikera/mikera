package mikera.engine;

import mikera.image.generation.ImageUtils;
import mikera.math.BaseVectorFunction;
import mikera.math.Vector;
import mikera.math.VectorFunction;
import mikera.util.Maths;

/**
 * Utility functions for hexagonal grids
 * 
 * @author Mike
 *
 */
public class Hex {
	/**
	 *  ratio of height/width of hexagon = 0.8660254037844386
	 */
	private static final float RATIO=(float)(Maths.sqrt(3)*0.5);
	
	public static final int NW=0;
	public static final int N=1;
	public static final int NE=2;
	public static final int SE=3;
	public static final int S=4;
	public static final int SW=5;
	
	public static final int[] HEX_DX={-1,  0,  1, 1,  0, -1};
	public static final int[] HEX_DY={ 0, -1, -1, 0 , 1,  1};
	
	// Location functions are hex-grid coordinates
	// Positions are logical float-coordinates with (0,0) at centre of unit-height hex at (0,0)
	// screen values are then simple multiples of logical positions
	
	public static int dx(int dir) {
		return HEX_DX[Maths.mod(dir, 6)];
	}
	public static int dy(int dir) {
		return HEX_DY[Maths.mod(dir, 6)];
	}
	
	public static int toLocationX(double px, double py) {
		double b=Math.floor(py+px/(RATIO*2.0/3.0));
		double c=Math.floor(py-px/(RATIO*2.0/3.0));	
		
		return (int)Math.floor((1+b-c)/3);
	}
	
	public static int toLocationY(double px, double py) {
		double a=Math.floor(py*2.0f);
		double b=Math.floor(py+px/(RATIO*2.0/3.0));
		double c=Math.floor(py-px/(RATIO*2.0/3.0));	
		
		return (int)Math.floor((4+3*a-b+c)/6);
	}
	
	public static int direction (int sx, int sy, int tx, int ty) {
		return direction (tx-sx,ty-sy);
	}

	
	public static int direction (int dx, int dy) {
		int a = (dx*2+dy);
		int b = (dx -dy);
		int c = (dx +2*dy);
		if (b>=0) {
			if (c>=0) {
				// South or south east
				return (b==0)?S:SE;
			}
			if (a>=0) {
				// North east
				return NE;
			}
			// North
			return N;
		}
		if (c<=0) {
			// North west
			return NW;
		}
		if (a<=0) {
			// South West
			return SW;
		}
		// South
		return S;
	}
	
	public static float toPositionX (int lx, int ly) {
		return lx*RATIO;
	}
	
	public static float toPositionY (int lx, int ly) {
		return ly+lx*0.5f;
	}
	
	/**
	 *  Hex distance calculation
	 *  
	 *  Assumes positive x and y axes are 60 degrees apart
	 */
	public static int distance(int x1, int y1, int x2, int y2) {
		int dx=x2-x1;
		int dy=y2-y1;
		
		if (dx*dy>0) {
			// dx and dy have same sign (i.e. within 60 degrees between same-signed x and y axes)
			return Math.abs(dx)+Math.abs(dy);
		}
		// dx and dy have opposite signs (i.e. in 120 degrees between opposite signed x and y axes)
		return Maths.max(Math.abs(dx),Math.abs(dy));
	}
	
	
	// Main function for testing
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		VectorFunction vf=new BaseVectorFunction(2,1) {
			@Override
			public void calculate(Vector input, Vector output) {
				float x=input.data[0]*16.0f;
				float y=input.data[1]*16.0f;
				int lx=Hex.toLocationX(x,y);
				int ly=Hex.toLocationY(x,y);
				float px=Hex.toPositionX(lx, ly);
				float py=Hex.toPositionY(lx, ly);
				int d55=Hex.distance(lx,ly,5,5);
				boolean centre=(Vector.lengthSquared(px-x,py-y)<0.02f); 
				//output.data[0]=Maths.frac(0.3f*lx + 0.085f*ly +(centre?0.2f:0));
				output.data[0]=Maths.frac(d55*0.05f);
			}			
		};
		
		ImageUtils.displayAndExit(vf);
	}
}
