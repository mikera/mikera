package mikera.engine;

import mikera.util.Maths;
import mikera.util.Rand;

/**
 * Approximate fast ray casting for 3d grids
 * 
 * 1. Visits each cell once
 * 2. Each cell assumed to have exactly one source
 * 3. Halts when the CastFunction visitor returns false
 * 
 * Not intended to be visually accurate, but should
 * perform well for locality searching / detection etc.
 * 
 * @author Mike Anderson
 *
 */
public class RayCaster {
	public static final int TOTAL_MAX_RANGE=100;
	public static final int DEFAULT_MAX_RANGE=10;
	
	private CastFunction castFunction;
	private int maxRange=DEFAULT_MAX_RANGE;
	
	
	public static abstract class CastFunction {
		public abstract boolean visit(int x, int y, int z);
	}
	
	public void setCastFunction(CastFunction castFunction) {
		this.castFunction = castFunction;
	}

	public CastFunction getCastFunction() {
		return castFunction;
	}	
	
	
	
	public void cast(int startX, int startY, int startZ) {
		castLocal(startX,startY,startZ,0,0,0);
	}
	
	private void castLocal(int cx, int cy, int cz, int dx, int dy, int dz) {
		boolean shouldContinue=castFunction.visit(cx+dx, cy+dy, cz+dz);
		
		if (!shouldContinue) return;
		
		int ax=Maths.abs(dx);
		int ay=Maths.abs(dy);
		int az=Maths.abs(dz);
		if ((ax>=maxRange)||(ay>=maxRange)||(az>=maxRange)) {
			return;
		}
		byte dirx=((dx>=0)?Dir.E:Dir.W);
		byte diry=((dy>=0)?Dir.N:Dir.S);
		byte dirz=((dz>=0)?Dir.U:Dir.D);
		
		int dirs=0;
		if ((dx==0)&&(dy==0)&&(dz==0)) {
			dirs=Dir.ALL_DIRS_MASK;
		} else {
			// process in direction(s) with maximum length along axis
			if ((ax>=ay)&&(ax>=az)) dirs|=getDir(ax,ay,az,dirx,diry,dirz);
			if ((ay>=ax)&&(ay>=az)) dirs|=getDir(ay,ax,az,diry,dirx,dirz);
			if ((az>=ax)&&(az>=ay)) dirs|=getDir(az,ax,ay,dirz,dirx,diry);
		}
		
		for (byte dir=1; dir<=Dir.MAX_DIR; dir++) {
			dirs=dirs>>1;
			if ((dirs&1)!=0) {
				castLocal(cx,cy,cz,dx+Dir.dx(dir),dy+Dir.dy(dir),dz+Dir.dz(dir));
			}
		}
	}

	private int getDir(int a1, int a2, int a3, byte d1, byte d2, byte d3) {
		int dirs=0;
		dirs|=getDir2(a1,a2,a3,d1,d2,d3);

		// There are potentially up to 4 quadrants to consider
		// if point is on a2 or a3 axis
		if (a2==0) {
			dirs|=getDir2(a1,a2,a3,d1,Dir.reverse(d2),d3);
		}
		if (a3==0) {
			dirs|=getDir2(a1,a2,a3,d1,d2,Dir.reverse(d3));
		}
		if ((a3==0)&&(a2==0)) {
			dirs|=getDir2(a1,a2,a3,d1,Dir.reverse(d2),Dir.reverse(d3));
		}

		return dirs;
	}
	
	private int getDir2(int a1, int a2, int a3, byte d1, byte d2, byte d3) {
		// point at which split occurs (distance from a1 axis)
		// note this can be at any point in range 0 to a1
		// but must be a function of a1 only
		int splitPoint;
		//splitPoint=Bits.roundUpToPowerOfTwo(a1+2)-2-a1;
		//splitPoint=Maths.mod(Rand.xorShift32(a1),a1);
		splitPoint=splitPoints[a1];
		
		int dirs=0;
		
		// straight ahead
		if ((a2<=splitPoint)&&(a3<=splitPoint)) dirs|=Dir.dirMask(d1);
		
		// fan out
		if ((a2>=splitPoint)&&(a3<=splitPoint)) dirs|=Dir.dirMask(d1+d2);
		if ((a3>=splitPoint)&&(a2<=splitPoint)) dirs|=Dir.dirMask(d1+d3);
	
		// diagonal
		if ((a3>=splitPoint)&&(a2>=splitPoint)) dirs|=Dir.dirMask(d1+d2+d3);
		
		return dirs;
	}

	public void setMaxRange(int maxRange) {
		this.maxRange = maxRange;
	}

	public int getMaxRange() {
		return maxRange;
	}
	
	
	private static final int[] splitPoints=new int[TOTAL_MAX_RANGE];

	
	static {
		// TODO: figure out how to make a nice pattern
		for (int i=1; i<splitPoints.length; i++) {
			splitPoints[i]=Maths.mod(Rand.xorShift32(Rand.xorShift32(Rand.xorShift32(i+137))),i+1);
		}
	}
}
