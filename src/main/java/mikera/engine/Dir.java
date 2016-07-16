package mikera.engine;

import java.util.Arrays;
import java.util.Comparator;

import mikera.util.Maths;
import mikera.util.Rand;

public class Dir {
	public static final byte C=0;
	public static final byte N=1;
	public static final byte S=2;
	public static final byte E=3;
	public static final byte W=6;
	public static final byte NE=N+E;
	public static final byte NW=N+W;
	public static final byte SE=S+E;
	public static final byte SW=S+W;	
	
	public static final byte U=9;
	public static final byte UN=U+N;
	public static final byte US=U+S;
	public static final byte UE=U+E;
	public static final byte UW=U+W;
	public static final byte UNE=UN+E;
	public static final byte UNW=UN+W;
	public static final byte USE=US+E;
	public static final byte USW=US+W;	
	
	public static final byte D=18;
	public static final byte DN=D+N;
	public static final byte DS=D+S;
	public static final byte DE=D+E;
	public static final byte DW=D+W;
	public static final byte DNE=DN+E;
	public static final byte DNW=DN+W;
	public static final byte DSE=DS+E;
	public static final byte DSW=DS+W;	
	
	public static final byte MAX_DIR=DSW+1;
	public static final int ALL_DIRS_MASK=(1<<MAX_DIR)-1;

	public static final byte[] DX={0, 0, 0, 1, 1, 1,-1,-1,-1, 0, 0, 0, 1, 1, 1,-1,-1,-1, 0, 0, 0, 1, 1, 1,-1,-1,-1};
	public static final byte[] DY={0, 1,-1, 0, 1,-1, 0, 1,-1, 0, 1,-1, 0, 1,-1, 0, 1,-1, 0, 1,-1, 0, 1,-1, 0, 1,-1};
	public static final byte[] DZ={0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
	
	protected static final float[] DIST=new float[MAX_DIR];
	
	public static final byte[] ALL_DIRECTIONS_3D   ={C,N,S,E,NE,SE,W,NW,SW,U,UN,US,UE,UNE,USE,UW,UNW,USW,D,DN,DS,DE,DNE,DSE,DW,DNW,DSW};
	public static final byte[] ALL_DIRECTIONS_2D   ={C,N,S,E,NE,SE,W,NW,SW};
	
	protected static final byte[] DISTORDER_DIRECTIONS={C,N,S,E,W,U,D,NE,NW,SE,SW,UN,US,DN,DS,UE,UW,DE,DW,UNE,UNW,DNE,DNW,USE,USW,DSE,DSW};
	protected static final byte[] ORTHOGONAL_DIRECTIONS={N,S,E,W,U,D};
	protected static final byte[] REVERSE_DIRECTIONS  =new byte[MAX_DIR];
	protected static final Integer[] ALL_DIRECTIONS_INTEGER =new Integer[MAX_DIR];
	protected static final byte[] CLOSEST_DIRECTIONS =new byte[MAX_DIR*MAX_DIR];
	
	public static long addToZ(long z, int dir) {
		return Octreap.calculateZ(Octreap.extractX(z)+DX[dir], Octreap.extractY(z)+DY[dir], Octreap.extractZ(z)+DZ[dir]);
	}
	
	public static int dirMask(int dir) {
		return 1<<dir;
	}
	
	public static byte orthogonalDirection(int i) {
		return ORTHOGONAL_DIRECTIONS[i];
	}
	
	public static byte reverse(int dir) {
		return REVERSE_DIRECTIONS[dir];
	}
	
	public static byte distanceOrderedDirection(int i) {
		return DISTORDER_DIRECTIONS[i];
	}
	
	public static int manhattanDistance(int dx, int dy, int dz) {
		return Math.abs(dx)+Math.abs(dy)+Math.abs(dz);
	}
	
	public static int rogueDistance(int dx, int dy, int dz) {
		return Maths.max(Math.abs(dx),Math.abs(dy),Math.abs(dz));
	}
	
	public static void visitDirections(int dirSet, PointVisitor<Integer> p, int x, int y, int z) {
		int mask=1;
		for (int i=0; i<MAX_DIR; i++) {
			if ((dirSet&mask)!=0) {
				p.visit(x+DX[i], y+DY[i], z+DZ[i], ALL_DIRECTIONS_INTEGER[i]);
			}
			mask<<=1;
		}	
	}
	
	public static void visitAllDirections(PointVisitor<Integer> p, int x, int y, int z) {
		for (int i=0; i<MAX_DIR; i++) {
			p.visit(x+DX[i], y+DY[i], z+DZ[i], ALL_DIRECTIONS_INTEGER[i]);
		}	
	}

	public static final byte getDir(int x, int y, int z, int tx, int ty, int tz) {
		return getDir(tx-x, ty-y, tz-z);
	}
	
	public static final int dx(byte dir) {
		return DX[dir];
	}
	
	// alternate dx() implementation using bit operations
	public static final int dx2(byte dir) {
		long dd=0x3F540FD503F540L;
		dd = (dd<<(62-(dir<<1))) >> 62; // exploit sign extend!
		return (int) dd;
	}
	
	public static final int dy(byte dir) {
		return DY[dir];	
	}
	
	public static final int dz(byte dir) {
		return DZ[dir];
	}

	
	public static final byte getDir(int dx, int dy, int dz) {
		byte d=0;
		
		if (dx<0) d+=W;
		else if (dx>0) d+=E;
		
		if (dy<0) d+=S;
		else if (dy>0) d+=N;
		
		if (dz<0) d+=D;
		else if (dz>0) d+=U;
		
		return d;
	}
	
	public static boolean isValidDir(byte dir) {
		return ((dir>0)&&(dir<MAX_DIR));
	}
	
	public static boolean isValidDir(int dx, int dy, int dz) {
		return ((dx>=-1)&&(dx<=1)&&(dy>=-1)&&(dy<=1)&&(dz>=-1)&&(dx<=1));
	}
	
	public static final byte getClosestDir(int dir, int seqIndex) {
		return CLOSEST_DIRECTIONS[dir*MAX_DIR+seqIndex];
	}
	 
	static {
		for (int i=0; i<MAX_DIR; i++) {
			DIST[i]=Maths.sqrt(Maths.square(DX[i])+Maths.square(DY[i])+Maths.square(DZ[i]));
		}

		for (int i=0; i<MAX_DIR; i++) {
			REVERSE_DIRECTIONS[i]=getDir(-DX[i],-DY[i],-DZ[i]);
		}
		
		for (int i=0; i<MAX_DIR; i++) {
			ALL_DIRECTIONS_INTEGER[i]=Integer.valueOf(i);
		}
		
		Integer[] ds=new Integer[MAX_DIR];
		for (int i=0; i<MAX_DIR; i++) {
			for (int j=0; j<MAX_DIR; j++) {
				ds[j]=ALL_DIRECTIONS_INTEGER[j];
			}
			// sort sub array by distance
			final byte b1=(byte)i;
			Arrays.sort(ds, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return Float.compare(sqdist(o1),sqdist(o2));
				}	
				
				public float sqdist(int index) {
					byte b2=(byte)index;
					return 
						Maths.square(DX[b1]-DX[b2])+
						Maths.square(DY[b1]-DY[b2])+
						Maths.square(DZ[b1]-DZ[b2]);
				}
			});
			
			for (int j=0; j<MAX_DIR; j++) {
				CLOSEST_DIRECTIONS[i*MAX_DIR+j]=(byte)(ds[j].intValue());
			}
		}

	}

	public static byte random() {
		return (byte) Rand.r(MAX_DIR);
	}

	public static void validate() {
		for (byte i=0; i<MAX_DIR; i++) {
			
			if (dx2(i)!=dx(i)) throw new Error(Byte.toString(i));
			
			if (i!=ALL_DIRECTIONS_3D[i]) throw new Error();
			if (i!=ALL_DIRECTIONS_INTEGER[i]) throw new Error();
			
			Integer[] ds=new Integer[MAX_DIR];
			for (int j=0; j<MAX_DIR; j++) {
				ds[j]=(int)CLOSEST_DIRECTIONS[i*MAX_DIR+j];
			}
			// check all directions are included
			Arrays.sort(ds);
			for (int j=0; j<MAX_DIR; j++) {
				if (j!=ds[j]) throw new Error();
			}
		}
		
		// check distances are ordered
		for (int i=1; i<Dir.DISTORDER_DIRECTIONS.length; i++) {
			if (Dir.DIST[Dir.DISTORDER_DIRECTIONS[i]]<Dir.DIST[Dir.DISTORDER_DIRECTIONS[i-1]]) {
				throw new Error();
			}
		}

	}
}
