package mikera.engine;

import java.util.Arrays;

import mikera.util.Maths;
/**
 * Class for storing flexible 3D int arrays
 * Stores arrays of ints
 * 
 * @author Mike Anderson
 *
 */
public class ArrayGrid<T> extends BaseGrid<T> {
	// base coordinates
	private int gx;
	private int gy;
	private int gz;
	
	// width, height and depth 
	private int gw;
	private int gh;
	private int gd;
	
	private Object[] data=null;
	
	public ArrayGrid() {
	}
	
	public ArrayGrid(int x, int y, int z) {
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
	
	public int countNonNull() {
		Object[] dt=data;
		if (dt==null) return 0;
		int result=0;
		for (int i=0; i<dt.length; i++) {
			if (dt[i]!=null) result++;
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public T get(int x, int y, int z) {
		if (data==null) return null;
		x-=gx;
		y-=gy;
		z-=gz;
		if ((x<0)||(y<0)||(z<0)||(x>=gw)||(y>=gh)||(z>=gd)) return null;
		int i=dataIndexRelative(x,y,z);
		Object[] dt=data;
		return (T)(dt[i]);
	}
	
	@SuppressWarnings("unchecked")
	public void visitNonNull(PointVisitor<T> pv) {
		if (data==null) return;
		int si=0;
		int tgw=gw; int tgh=gh; int tgd=gd; // make local copy to enable loop optimisation?
		for (int z=0; z<tgd; z++) {
			for (int y=0; y<tgh; y++) {
				for (int x=0; x<tgw; x++) {
					T bv=(T)(data[si++]);
					if (bv==null) continue;
					pv.visit(x,y,z,bv);
				}					
			}
		}		
	}
	
	@SuppressWarnings("unchecked")
	public void visitGrid(PointVisitor<T> pv) {
		if (data==null) return;
		int si=0;
		int tgw=gw; int tgh=gh; int tgd=gd; // make local copy to enable loop optimisation?
		for (int z=0; z<tgd; z++) {
			for (int y=0; y<tgh; y++) {
				for (int x=0; x<tgw; x++) {
					T bv=(T)(data[si++]);
					pv.visit(x,y,z,bv);
				}					
			}
		}	
	}
	
	@SuppressWarnings("unchecked")
	public void visitBlocks(BlockVisitor<T> bf) {
		if (data==null) return;
		int si=0;
		int tgw=gw; int tgh=gh; int tgd=gd; // make local copy to enable loop optimisation?
		for (int z=0; z<tgd; z++) {
			for (int y=0; y<tgh; y++) {
				for (int x=0; x<tgw; x++) {
					T bv=(T)(data[si++]);
					bf.visit(x+gx,y+gy,z+gz,x+gx,y+gy,z+gz,bv);
				}					
			}
		}			
	}
	
	@SuppressWarnings("unchecked")
	public ArrayGrid<T> clone() {
		ArrayGrid<T> nbg;
		try {
			nbg = (ArrayGrid<T>)super.clone();
			Object[] dt=data;
			if (dt!=null) {
				
				Object[] ndt=new Object[dt.length];
				System.arraycopy(dt, 0, ndt, 0,ndt.length);
				nbg.data=ndt;
			}
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
		return nbg;
	}
	
	@Override
	public ArrayGrid<T> clear() {
		data=null;
		return this;
	}
	
	@Override
	public ArrayGrid<T> clearContents() {
		final Object[] dt=data;
		for (int i=0; i<dt.length; i++) {
			dt[i]=null;
		}
		return this;
	}	
	public int dataLength() {
		return data.length;
	}
	
	private void init(int x, int y, int z) {
		gx=x;
		gy=y;
		gz=z;
		data=new Object[1];
		gw=1; gh=1; gd=1;
	}
	
	public void growToInclude(int x, int y, int z) {
		if (data==null) {init(x,y,z); return;}
		
		// check if array needs resizing
		if ((x<gx)||(y<gy)||(z<gz)) { growToIncludeLocal(x,y,z); return; }
		
		// ok providing we don't need
		if (x>=(gx+width())) {growToIncludeLocal(x,y,z); return; }
		if (y>=(gy+height())) {growToIncludeLocal(x,y,z); return; }
		if (z>=(gz+depth())) {growToIncludeLocal(x,y,z); return; }	
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
		Object[] ndata=new Object[nl];
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
	
	public ArrayGrid<T> paste(Grid<T> t) {
		return paste(t,0,0,0);
	}
	
	public ArrayGrid<T> paste(Grid<T> t, final int dx, final int dy, final int dz) {
		return (ArrayGrid<T>) super.paste(t,dx,dy,dz);
	}
	
	public ArrayGrid<T> set(int x, int y, int z, T v) {
		if (data==null) {
			init(x,y,z);
			x-=gx; y-=gy; z-=gz;
		} else {
			if ((x<gx)||(y<gy)||(z<gz)) growToIncludeLocal(x,y,z);
			x-=gx; if (x>=width()) growToIncludeLocal(x+gx,y,z);
			y-=gy; if (y>=height()) growToIncludeLocal(x+gx,y+gy,z);
			z-=gz; if (z>=depth()) growToIncludeLocal(x+gx,y+gy,z+gz);		
		}
		if (!inRange(x+gx,y+gy,z+gz)) throw new Error("Range error: "+(x+gx)+","+(y+gy)+","+(z+gz));
		
		setLocalRelative(x,y,z,v);
		return this;
	}
	
	@Override
	public ArrayGrid<T>  setBlock(int x1, int y1, int z1, int x2, int y2, int z2, T v) {
		return (ArrayGrid<T>)super.setBlock(x1,y1,z1,x2,y2,z2,v);
	}	
	
	public ArrayGrid<T>  setBlock(int x1, int y1, int z1, int x2, int y2, int z2, int v) {
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
	private void setLocalRelative(int x, int y, int z, T v) {
		int i=dataIndexRelative(x,y,z);
		data[i]=v;
	}
	
	/**
	 * Check if coordinate is within the existing range
	 */
	public boolean inRange(int x, int y, int z) {
		if ((x<gx)||(y<gy)||(z<gz)) return false;
		if ((x>=gx+gw)||(y>=gy+gh)||(z>=gz+gd)) return false;
		return true;
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
