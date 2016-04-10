package mikera.engine;

import mikera.annotations.Mutable;
import mikera.util.Maths;
/**
 * Class for storing flexible 3D bit arrays
 * Stores bits in arrays of ints (4*4*2 aligned blocks)
 * 
 * @author Mike Anderson
 *
 */
@Mutable
public final class BitGrid extends BaseGrid<Boolean> {
	private static final int GROW_BORDER=1;
	
	private static final int XLOWBITS=2;
	private static final int YLOWBITS=2;
	private static final int ZLOWBITS=1;	
	
	public static final int BITS_USED=1<<(XLOWBITS+YLOWBITS+ZLOWBITS);
	
	private static final int XLOWMASK=(1<<XLOWBITS)-1;
	private static final int YLOWMASK=(1<<YLOWBITS)-1;
	private static final int ZLOWMASK=(1<<ZLOWBITS)-1;	

	private static final int XHIGHMASK=~XLOWMASK;
	private static final int YHIGHMASK=~YLOWMASK;
	private static final int ZHIGHMASK=~ZLOWMASK;	

	
	public static final int XBLOCKSIZE = 1<<XLOWBITS;
	public static final int YBLOCKSIZE = 1<<YLOWBITS;
	public static final int ZBLOCKSIZE = 1<<ZLOWBITS;
	
	// aligned block coordinates
	private int gx;
	private int gy;
	private int gz;
	
	// width, height and depth in blocks (not coordinates!!)
	private int gw;
	private int gh;
	private int gd;
	
	private int[] data=null;
	
	public BitGrid() {
	}
	
	public BitGrid(int x, int y, int z) {
		init(x,y,z);
	}
	
	public int width() {
		return gw<<XLOWBITS;
	}
	
	public int volume() {
		return width()*height()*depth();
	}
	
	public int height() {
		return gh<<YLOWBITS;
	}
	
	public int depth() {
		return gd<<ZLOWBITS;
	}
	
	public int countSetBits() {
		int[] dt=data;
		if (dt==null) return 0;
		int result=0;
		for (int i=0; i<dt.length; i++) {
			//result+=Bits.countSetBits(dt[i]);
			result+=Integer.bitCount(dt[i]);
		}
		return result;
	}
	
	public int countSetBitsUsingVisitor() {
		return countSetBits(gx,gy,gz,gx+width(),gy+height(),gz+depth());
	}
	
	public int countSetBits(int x1,int y1, int z1, int x2,int y2, int z2) {
		final int[] counter=new int[1];
		visitSetBits(new BlockVisitor<Boolean>(){

			@Override
			public Object visit(int x1, int y1, int z1, int x2, int y2, int z2,
					Boolean value) {
				counter[0]++;
				return null;
			}
			
		},x1,y1,z1, x2,y2, z2);
		return counter[0];
	}
	
	@Override
	public Boolean get(int x, int y, int z) {
		return test(x,y,z) ? Boolean.TRUE:Boolean.FALSE;
	}
	
	public boolean test(int x, int y, int z) {
		if (data==null) return false;
		if ((x<gx)||(y<gy)||(z<gz)) return false;
		x-=gx; if (x>=width()) return false;
		y-=gy; if (y>=height()) return false;
		z-=gz; if (z>=depth()) return false;
		int i=dataIndex(x,y,z);
		int bi=bitPos(x,y,z);
		return ((data[i]>>bi)&1)!=0;
	}
	
	public void visitSetBits(BlockVisitor<Boolean> pv) {
		visitSetBits(pv,gx,gy,gz,gx+width(),gy+height(),gz+depth());
	}
	
	public void visitSetBits(BlockVisitor<Boolean> pv,int x1, int y1, int z1, int x2,int y2, int z2) {
		// take current references to data, in case data array is replaced
		int[] dat=data;
		if (dat==null) return;	
		int tgx=gx;
		int tgy=gy;
		int tgz=gz;
		int tgw=gw;
		int tgh=gh;
		
		int rx1=Math.max((x1-tgx)&(XHIGHMASK), 0);
		int ry1=Math.max((y1-tgy)&(YHIGHMASK), 0);
		int rz1=Math.max((z1-tgz)&(ZHIGHMASK), 0);
		int rx2=Math.min((x2-tgx+XBLOCKSIZE)&(XHIGHMASK), width());
		int ry2=Math.min((y2-tgy+YBLOCKSIZE)&(YHIGHMASK), height());
		int rz2=Math.min((z2-tgz+ZBLOCKSIZE)&(ZHIGHMASK), depth());
		
		for (int rz=rz1; rz<rz2; rz+=ZBLOCKSIZE) {
			for (int ry=ry1; ry<ry2; ry+=YBLOCKSIZE) {
				int sibase =(rz>>ZLOWBITS)*tgw*tgh + (ry>>YLOWBITS)*tgw;
				for (int rx=rx1; rx<rx2; rx+=XBLOCKSIZE) {
					int si= sibase + (rx>>XLOWBITS);
					int bv=dat[si];
					if (bv==0) continue;
					for (int i=0; i<32; i++) {
						if ((bv&15)==0) {
							i+=3;
							bv>>=4;
							continue;
						}
						if ((bv&1)!=0) {
							int bx=((tgx+ rx)&(XHIGHMASK)) + bitXOffset(i);
							int by=((tgy+ ry)&(YHIGHMASK)) + bitYOffset(i);
							int bz=((tgz+ rz)&(ZHIGHMASK)) + bitZOffset(i);
							if ((bx>=x1)&&(bx<=x2)&&(by>=y1)&&(by<=y2)&&(bz>=z1)&&(bz<=z2)){
								pv.visit(
										bx,by,bz,
										bx,by,bz,
										Boolean.TRUE);
							}
						}
						bv>>=1;
					}
				}					
			}
		}		
	}
	
	public void visitBits(BlockVisitor<Boolean> bf) {
		if (data==null) return;
		int si=0;
		int tgw=gw; int tgh=gh; int tgd=gd; // make local copy to enable loop optimisation?
		for (int z=0; z<(tgd<<ZLOWBITS); z+=ZBLOCKSIZE) {
			for (int y=0; y<(tgh<<YLOWBITS); y+=YBLOCKSIZE) {
				for (int x=0; x<(tgw<<XLOWBITS); x+=XBLOCKSIZE) {
					int bv=data[si++];
					for (int i=0; i<32; i++) {
						int bx=((gx+x)&(XHIGHMASK)) + bitXOffset(i);
						int by=((gy+y)&(YHIGHMASK)) + bitYOffset(i);
						int bz=((gz+z)&(ZHIGHMASK)) + bitZOffset(i);
						bf.visit(
								bx,by,bz,
								bx,by,bz,
								((bv&1)==1)?Boolean.TRUE:Boolean.FALSE);
						bv>>=1;
					}
				}					
			}
		}	
	}
	
	public static int bitXOffset(int bitIndex) {
		return (bitIndex&XLOWMASK);
	}
	
	public static int bitYOffset(int bitIndex) {
		return ((bitIndex>>XLOWBITS)&YLOWMASK);
	}
	
	public static int bitZOffset(int bitIndex) {
		return ((bitIndex>>(XLOWBITS+YLOWBITS))&ZLOWMASK);
	}
	
	@Override
	public BitGrid clone() {
		BitGrid nbg;
		try {
			nbg = (BitGrid)super.clone();
			int[] dt=data;
			if (dt!=null) {
				int[] ndt=new int[dt.length];
				System.arraycopy(dt, 0, ndt, 0,ndt.length);
				nbg.data=ndt;
			}
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
		return nbg;
	}
	
	@Override
	public BitGrid clear() {
		data=null;
		gw=0;
		gh=0;
		gd=0;
		return this;
	}
	
	@Override
	public BitGrid clearContents() {
		final int[] dt=data;
		if (dt==null) return this;
		for (int i=0; i<dt.length; i++) {
			dt[i]=0;
		}
		return this;
	}
	
	public int dataLength() {
		if (data==null) return 0;
		return data.length;
	}
	
	private void init(int x, int y, int z) {
		gx=x&(XHIGHMASK);
		gy=y&(YHIGHMASK);
		gz=z&(ZHIGHMASK);
		data=new int[1];
		gw=1; gh=1; gd=1;
	}
	
	public void growToInclude(int x, int y, int z) {
		if (data==null) {init(x,y,z); return;}
		
		// check if array needs resizing
		if (
				( (x<gx) || (y<gy) || (z<gz) ) ||
				( x>= ((gx+width())|XLOWMASK) ) ||
				( y>= ((gy+height())|YLOWMASK) ) ||
				( z>= ((gz+depth())|ZLOWMASK) )
		) 
		{
			growToIncludeLocal(x-GROW_BORDER,y-GROW_BORDER,z-GROW_BORDER,x+GROW_BORDER,y+GROW_BORDER,z+GROW_BORDER); return; 	
		}	
	}
	
	/**
	 * Grow BitGrid to ensure a given volume is included
	 */
	private void growToIncludeLocal(int x1, int y1, int z1, int x2, int y2, int z2) {	
		// assumes a change in size
		int ngx=Maths.min(gx,x1)&(XHIGHMASK);
		int ngy=Maths.min(gy,y1)&(YHIGHMASK);
		int ngz=Maths.min(gz,z1)&(ZHIGHMASK);
		int ngw=(Maths.max(gx+width(), x2+XBLOCKSIZE)-ngx)>>XLOWBITS;
		int ngh=(Maths.max(gy+height(),y2+YBLOCKSIZE)-ngy)>>YLOWBITS;
		int ngd=(Maths.max(gz+depth(), z2+ZBLOCKSIZE)-ngz)>>ZLOWBITS;
		resize(ngx,ngy,ngz,ngw,ngh,ngd);
	} 
		
	private void resize(int ngx, int ngy, int ngz, int ngw, int ngh, int ngd) {
		//int bc=countSetBits();
		int nl=ngw*ngh*ngd;
		int[] ndata=new int[nl];
		int si=0;
		for (int dz=0; dz<gd; dz++) {
			for (int dy=0; dy<gh; dy++) {
				
				// set up destination index for each row	
				int di=0;
				di+=(dz+((gz-ngz)>>ZLOWBITS))*ngw*ngh;
				di+=(dy+((gy-ngy)>>YLOWBITS))*ngw;
				di+=((gx-ngx)>>XLOWBITS);
				
				for (int dx=0; dx<gw; dx++) {
					ndata[di++]=data[si++];
				}					
			}
		}
		//if (si!=gw*gh*gd) throw new Error();
		
		// update fields
		data=ndata;
		gx=ngx;
		gy=ngy;
		gz=ngz;
		gw=ngw;
		gh=ngh;
		gd=ngd;
		//if(bc!=countSetBits()) throw new Error();
	}
	
	public void set(int x, int y, int z, int v) {
		set(x,y,z,v!=0);
	}
	
	public BitGrid set(int x, int y, int z, boolean v) {
		if (data==null) {
			if (!v) return this;
			init(x,y,z);
			setLocal(x-gx,y-gy,z-gz,v);
		} else {
			int rx=x-gx;
			int ry=y-gy;
			int rz=z-gz;
			if ((rx<0)||(ry<0)||(rz<0)||(rx>=width())||(ry>=height())||(rz>=depth())) {
				if (!v) return this;
				growToIncludeLocal(x,y,z,x,y,z);
				// update (rx,ry,rz) because (gx,gy,gz) may have changed
				rx=x-gx;
				ry=y-gy;
				rz=z-gz;
			}
			setLocal(rx,ry,rz,v);
		}
		return this;
	}
	
	// set BitGrid cell using coordinates relative to (gx,gy,gz);
	private void setLocal(int rx, int ry, int rz, boolean v) {
		int i=dataIndex(rx,ry,rz);
		int current=data[i];
		int bi=bitPos(rx,ry,rz);
		int bv=1<<bi;
		if (v) {
			data[i]=current|bv;
		} else {
			data[i]=current&~bv;
		}
	}
	
	// get index 
	// using coordinates relative to grid origin
	private int dataIndex(int rx, int ry, int rz) {
		return (rx>>XLOWBITS)+gw*((ry>>YLOWBITS)+gh*(rz>>ZLOWBITS));
	}
	
	public static int bitPos(int x, int y, int z) {
		// fine to use either relative or absolute x,y,z
		// since makes no difference to low bits
		return (x&XLOWMASK)+((y&YLOWMASK)<<XLOWBITS)+((z&ZLOWMASK)<<(XLOWBITS+YLOWBITS));
	}

	@Override
	public int countNonNull() {
		// return countSetBits();
		throw new UnsupportedOperationException();
	}

	@Override
	public BitGrid set(int x, int y, int z, Boolean value) {
		return set(x,y,z,value.booleanValue());
	}

	@Override
	public void visitBlocks(BlockVisitor<Boolean> bf) {
		visitBits(bf);
	}

	@Override
	public void validate() {
		if (data.length!=gw*gh*gd) throw new Error();
		if ((gx&XLOWMASK)!=0) throw new Error();
		if ((gy&YLOWMASK)!=0) throw new Error();
		if ((gz&ZLOWMASK)!=0) throw new Error();
		if (countSetBits()!=countSetBitsUsingVisitor()) throw new Error();
	}
}
