package mikera.engine;

import java.util.Arrays;

import mikera.annotations.Mutable;
import mikera.util.Maths;
/**
 * Class for storing flexible 3D int arrays
 * Stores arrays of ints
 * 
 * @author Mike Anderson
 *
 */
@Mutable
public final class IntGrid  implements Cloneable {
	// base coordinates
	private int gx;
	private int gy;
	private int gz;
	
	// width, height and depth in blocks (not coordinates!!)
	private int gw;
	private int gh;
	private int gd;
	
	private int[] data=null;
	
	public IntGrid() {
	}
	
	public IntGrid(int x, int y, int z) {
		init(x,y,z);
	}
	
	public int width() {
		return gw;
	}
	
	public int height() {
		return gh;
	}
	
	public int depth() {
		return gd;
	}
	
	public int countNonZero() {
		int[] dt=data;
		int result=0;
		for (int i=0; i<dt.length; i++) {
			if (dt[i]!=0) result++;
		}
		return result;
	}
	
	public int get(int x, int y, int z) {
		int i=dataIndexRelative(x-gx,y-gy,z-gz);
		if (i<0) return 0;
		int[] dt=data;
		if (i>dt.length) return 0;
		return (dt[i]);
	}
	
	public void visitNonZero(PointVisitor<Integer> pv) {
		if (data==null) return;
		int si=0;
		int tgw=gw; int tgh=gh; int tgd=gd; // make local copy to enable loop optimisation?
		for (int z=0; z<tgd; z++) {
			for (int y=0; y<tgh; y++) {
				for (int x=0; x<tgw; x++) {
					int bv=data[si++];
					if (bv==0) continue;
					pv.visit(x,y,z,bv);
				}					
			}
		}		
	}
	
	public void visitGrid(PointVisitor<Integer> pv) {
		if (data==null) return;
		int si=0;
		int tgw=gw; int tgh=gh; int tgd=gd; // make local copy to enable loop optimisation?
		for (int z=0; z<tgd; z++) {
			for (int y=0; y<tgh; y++) {
				for (int x=0; x<tgw; x++) {
					int bv=data[si++];
					pv.visit(x,y,z,bv);
				}					
			}
		}	
	}
	
	public IntGrid clone() {
		IntGrid nbg;
		try {
			nbg = (IntGrid)super.clone();
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
	
	public IntGrid clear() {
		data=null;
		return this;
	}
	
	public IntGrid clearContents() {
		final int[] dt=data;
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
		gx=x;
		gy=y;
		gz=z;
		data=new int[1];
		gw=1; gh=1; gd=1;
	}
	
	public IntGrid growToInclude(int x, int y, int z) {
		if (data==null) {init(x,y,z); return this;}
		
		// check if array needs resizing
		if ((x<gx)||(y<gy)||(z<gz)) { growToIncludeLocal(x,y,z); return this; }
		
		// ok providing we don't need
		if (x>=(gx+width())) {growToIncludeLocal(x,y,z); return this; }
		if (y>=(gy+height())) {growToIncludeLocal(x,y,z); return this; }
		if (z>=(gz+depth())) {growToIncludeLocal(x,y,z); return this; }	
		return this;
	}
	
	private void growToIncludeLocal(int x, int y, int z) {	
		// assumes a change in size
		int ngx=Maths.min(gx,x);
		int ngy=Maths.min(gy,y);
		int ngz=Maths.min(gz,z);
		int ngw=(Maths.max(gx+width(), x+1)-ngx);
		int ngh=(Maths.max(gy+height(),y+1)-ngy);
		int ngd=(Maths.max(gz+depth(), z+1)-ngz);
		resize(ngx,ngy,ngz,ngw,ngh,ngd);
	} 
		
	private void resize(int ngx, int ngy, int ngz, int ngw, int ngh, int ngd) {
		int nl=ngw*ngh*ngd;
		int[] ndata=new int[nl];
		int si=0;
		int di=(gz-ngz)*ngw*ngh+(gy-ngy)*ngw+(gx-ngx);
		for (int z=0; z<gd; z++) {
			for (int y=0; y<gh; y++) {
				System.arraycopy(data, si, ndata, di, gw);
				si+=gw;
				di+=ngw;
			}
			di+=ngw*(ngh-gh);
		}
		data=ndata;
		gx=ngx;
		gy=ngy;
		gz=ngz;
		gw=ngw;
		gh=ngh;
		gd=ngd;
	}
	
	public IntGrid set(int x, int y, int z, Integer v) {
		return set(x,y,z,v.intValue());
	}

	public IntGrid set(int x, int y, int z, int v) {
		if (data==null) {
			init(x,y,z);
			x-=gx; y-=gy; z-=gz;
		} else {
			if ((x<gx)||(y<gy)||(z<gz)) growToIncludeLocal(x,y,z);
			x-=gx; if (x>=width()) growToIncludeLocal(x+gx,y,z);
			y-=gy; if (y>=height()) growToIncludeLocal(x+gx,y+gy,z);
			z-=gz; if (z>=depth()) growToIncludeLocal(x+gx,y+gy,z+gz);		
		}
		setLocalRelative(x,y,z,v);
		return this;
	}
	
	public IntGrid setBlock(int x1, int y1, int z1, int x2, int y2, int z2, int v) {
		growToInclude(x1,y1,z1);
		growToInclude(x2,y2,z2);
		int w=x2-x1+1;
		int h=y2-y1+1;
		int d=z2-z1+1;
		int di=(x1-gx)+(y1-gy)*gw+(z1-gz)*gw*gh;
		for (int z=0; z<d; z++) {
			for (int y=0; y<h; y++) {
				Arrays.fill(data,di,di+w,v);
				di+=gw;
			}
			di+=gw*(gh-h);
		}		
		return this;
	}
	
	/**
	 * Sets the grid cell, assumes bounds are already checked
	 * and that relative to grid coordinates are used
	 */
	private void setLocalRelative(int x, int y, int z, int v) {
		int i=dataIndexRelative(x,y,z);
		data[i]=v;
	}
	
	/**
	 * Get data array index relative to grid origin
	 * @param rx x-coord relative to gx
	 * @param ry y-coord relative to gy
	 * @param rz z-coord relative to gz
	 * @return
	 */
	private int dataIndexRelative(int rx, int ry, int rz) {
		return rx+gw*(ry+gh*rz);
	}

}
