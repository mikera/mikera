package mikera.engine;

import java.util.ArrayList;
import java.util.Arrays;

import mikera.util.Maths;
import mikera.util.Tools;

/**
 * Grid implemented as a hierarchy of 4*4*4 grids
 * 
 * Top level is offset to centre at (0,0,0)
 * 
 * @author Mike Anderson
 *
 * @param <T>
 */
public class TreeGrid<T> extends BaseGrid<T> {

	private static final int DIM_SPLIT_BITS=2;
	private static final int DIM_SPLIT_MASK=(1<<DIM_SPLIT_BITS)-1;
	private static final int SIGNIFICANT_BITS=DIM_SPLIT_BITS*(20/DIM_SPLIT_BITS);
	private static final int TOP_SHIFT=SIGNIFICANT_BITS-DIM_SPLIT_BITS;
	private static final int DATA_ARRAY_SIZE=1<<(3*DIM_SPLIT_BITS);
	private static final int SIGNIFICANT_MASK=(1<<SIGNIFICANT_BITS)-1;
	private static final int TOP_OFFSET=(1<<(SIGNIFICANT_BITS-1));
	private static final int TOP_MAX=SIGNIFICANT_MASK;
	
	// each cell contains either object of type T or a sub-grid
	private final Object[] data=new Object[DATA_ARRAY_SIZE];
	
	@Override
	public int countNonNull() {
		return countNonNull(TOP_SHIFT);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public int countNodes() {
		int res=0;
		for (int i=0; i<DATA_ARRAY_SIZE; i++) {
			Object d=data[i];
			if (d==null) continue;
			if (d instanceof TreeGrid<?>) {
				TreeGrid<T> tg=(TreeGrid<T>)d;
				res+=tg.countNodes();
			}
		}
		return res+1;
	}
	
	@SuppressWarnings("unchecked")
	private int countNonNull(int shift) {
		int res=0;
		for (int i=0; i<DATA_ARRAY_SIZE; i++) {
			Object d=data[i];
			if (d==null) continue;
			if (d instanceof TreeGrid<?>) {
				if (shift<=0) throw new Error("TreeGrid element where shift="+shift);
				TreeGrid<T> tg=(TreeGrid<T>)d;
				res+=tg.countNonNull(shift-DIM_SPLIT_BITS);
			} else {
				res+=1<<(shift+shift+shift);
			}
		}
		return res;
	}
	
	@Override
	public T get(int x, int y, int z) {
		return getLocal(x+TOP_OFFSET,y+TOP_OFFSET,z+TOP_OFFSET);
	}
	
	@SuppressWarnings("unchecked")
	private T getLocal(final int x, final int y, final int z) {
		int shift=TOP_SHIFT;
		TreeGrid<T> head=this;
		while (shift>=0) {
			int li;
			// for some reason the inline version is much faster!!
			// li=index(x,y,z,shift);
			li= ((x>>shift)&DIM_SPLIT_MASK) + (((y>>shift)&DIM_SPLIT_MASK)<<DIM_SPLIT_BITS) + (((z>>shift)&DIM_SPLIT_MASK)<<(DIM_SPLIT_BITS*2));
			
			//if (li!=li1) System.err.println(((x>>(shift))&3)+","+((y>>(shift))&3)+","+((z>>(shift))&3)+"@"+shift+"   "+li+"->"+li1);
			
			Object d=head.data[li];
			if (d==null) return null;
			if (!(d instanceof TreeGrid<?>)) {
				return (T)d;
			}
			shift-=DIM_SPLIT_BITS;
			head=(TreeGrid<T>)d;
		}
		throw new Error("This shouldn't happen!!");
	}
	
	@Override
	public void visitBlocks(BlockVisitor<T> bf) {
		visitBlocksLocal(bf,
				0,0,0,
				0,0,0,
				TOP_MAX,TOP_MAX,TOP_MAX,
				TOP_SHIFT);
	}
	
	@Override
	public void visitBlocks(BlockVisitor<T> bf,int x1, int y1, int z1,int x2, int y2, int z2) {
		visitBlocksLocal(bf,
				0,0,0,
				x1+TOP_OFFSET,y1+TOP_OFFSET,z1+TOP_OFFSET,
				x2+TOP_OFFSET,y2+TOP_OFFSET,z2+TOP_OFFSET,
				TOP_SHIFT);
	}
	
	@SuppressWarnings("unchecked")
	// cx,cy,cz are offset to bottom left of grid
	// x1,y1,z1,x2,y2,z2 relative to bottom left
	private void visitBlocksLocal(BlockVisitor<T> bf, int cx, int cy, int cz,int x1, int y1, int z1,int x2, int y2, int z2,int shift) {
		int li=0;
		int bsize=1<<shift; // size of sub blocks in this TreeGrid
		
		int max= (bsize<<DIM_SPLIT_BITS); // top limit of this whole TreeGrid
		
		for (int lz=0; lz<max; lz+=bsize) {
			if ((lz>z2)||((lz+bsize)<=z1)) {
				li+=1<<(2*DIM_SPLIT_BITS);
				continue;
			}
			for (int ly=0; ly<max; ly+=bsize) {
				if ((ly>y2)||((ly+bsize)<=y1)) {
					li+=1<<DIM_SPLIT_BITS;
					continue;
				}
				for (int lx=0; lx<max; lx+=bsize) {
					if ((lx>x2)||((lx+bsize)<=x1)) {
						li++;
						continue;
					}
					
					// start of inner loop
					Object d=data[li++];
					if (d==null) continue;
					if (d instanceof TreeGrid<?>) {
						TreeGrid<T> tg=(TreeGrid<T>)d;
						tg.visitBlocksLocal(
								bf, 
								cx+lx, 
								cy+ly, 
								cz+lz, 
								x1-lx,
								y1-ly,
								z1-lz,
								x2-lx,
								y2-ly,
								z2-lz,
								shift-DIM_SPLIT_BITS);
					} else {
						int p1=cx+Math.max(lx,x1);
						int p2=cy+Math.max(ly,y1);
						int p3=cz+Math.max(lz,z1);
						int q1=cx+Math.min(lx+bsize-1,x2);
						int q2=cy+Math.min(ly+bsize-1,y2);
						int q3=cz+Math.min(lz+bsize-1,z2);
						bf.visit(
								p1-TOP_OFFSET,
								p2-TOP_OFFSET,
								p3-TOP_OFFSET,
								q1-TOP_OFFSET,
								q2-TOP_OFFSET,
								q3-TOP_OFFSET,
								(T)d);
					}
				}
			}
		}
	}
	
	@Override
	public TreeGrid<T> clear() {
		Arrays.fill(data, null);
		return this;
	}
	
	@Override
	public TreeGrid<T> clearContents() {
		clear();
		return this;
	}
	
	public TreeGrid() {
		
	}
	
	public TreeGrid(T defaultvalue) {
		for (int i=0; i<data.length; i++) {
			data[i]=defaultvalue;
		}
	}

	@Override
	public TreeGrid<T> set(int x, int y, int z, T value) {
		return setLocal(x+TOP_OFFSET,y+TOP_OFFSET,z+TOP_OFFSET,value);
	}
	
	@SuppressWarnings("unchecked")
	private TreeGrid<T> setLocal(int x, int y, int z, T value) {
		int shift=TOP_SHIFT;
		TreeGrid<T> head=this;
		while (shift>=0) {
			// int li = index(x,y,z,shift);
			int li=((x>>shift)&DIM_SPLIT_MASK) + (((y>>shift)&DIM_SPLIT_MASK)<<DIM_SPLIT_BITS) + (((z>>shift)&DIM_SPLIT_MASK)<<(DIM_SPLIT_BITS*2));
			Object d=head.data[li];
			if ((d==null)&&(shift>0)) {
				if (value==null) return this;
				d=new TreeGrid<T>();
				head.data[li]=d;
			}
			if (shift==0) {
				head.data[li]=value;
				if (head.isSolid(value)) solidify(x,y,z,TOP_SHIFT);
				return this;
			} else if (!(d instanceof TreeGrid<?>)) {
				if (d.equals(value)) return this;
				d=new TreeGrid<T>((T)d);
				head.data[li]=d;				
			}
			shift-=DIM_SPLIT_BITS;
			head=(TreeGrid<T>)d;
		}
		throw new Error("This shouldn't happen!!");
	}
	
	private boolean isSolid(T value) {
		for (int i=0; i<DATA_ARRAY_SIZE; i++) {
			Object d=data[i];
			if (!(Tools.equalsWithNulls(value,d))) {
				return false;
			}
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private boolean isSolid() {
		Object d=data[0];
		return (!(d instanceof TreeGrid<?>))&&isSolid((T)data[0]);
	}
	
	private static int index(int x, int y, int z, int shift) {
		int lx=(x>>shift)&DIM_SPLIT_MASK;
		int ly=(y>>shift)&DIM_SPLIT_MASK;
		int lz=(z>>shift)&DIM_SPLIT_MASK;
		int li=lx+(ly<<DIM_SPLIT_BITS)+(lz<<(2*DIM_SPLIT_BITS));
		return li;
	}
	
	@SuppressWarnings("unchecked")
	private Object solidify(int x, int y, int z, int shift) {
		int li=index(x,y,z,shift);
		Object d=data[li];

		if (d instanceof TreeGrid<?>) {
			TreeGrid<T> g=(TreeGrid<T>)d;
			Object r=g.solidify(x,y,z,shift-DIM_SPLIT_BITS);
			if (r==g) return this;
			data[li]=r;
			d=r;
		} 
		
		if (isSolid((T)d)) {
			return d;
		}
		return this;
	}
	
	@Override
	public TreeGrid<T> setBlock(int x1, int y1, int z1, int x2, int y2, int z2, T value) {
		setBlockLocal(x1+TOP_OFFSET, 
				y1+TOP_OFFSET, 
				z1+TOP_OFFSET, 
				x2+TOP_OFFSET, 
				y2+TOP_OFFSET, 
				z2+TOP_OFFSET, 
				value,
				TOP_SHIFT);
		return this;
	}

	@SuppressWarnings("unchecked")
	protected Object setBlockLocal(int x1, int y1, int z1, int x2, int y2, int z2, T value, int shift) {
		int bmask=3<<shift;
		int bstep=1<<shift;
		boolean setData=false;
		
		// get coordinates of sub block containing point 1
		// note masking to keep correct sign
		int bx1=((x1)&(bmask));
		int by1=((y1)&(bmask));
		int bz1=((z1)&(bmask));
	
		// loop over sub blocks (lx,ly,lz)-(ux,uy,uz)
		for (int lz=bz1; lz<=z2; lz+=bstep) {
			for (int ly=by1; ly<=y2; ly+=bstep) {
				for (int lx=bx1; lx<=x2; lx+=bstep) {
					int li=index(lx,ly,lz,shift);
					Object d=data[li];
					if (Tools.equalsWithNulls(d, value)) continue;
					
					int ux=lx+bstep-1;
					int uy=ly+bstep-1;
					int uz=lz+bstep-1;
					if ((shift<=0)||((z1<=lz)&&(z2>=uz)&&(y1<=ly)&&(y2>=uy)&&(x1<=lx)&&(x2>=ux))) {
						// set entire sub block
						data[li]=value;
						setData=true;
					} else {
						if (d==null) {
							d=new TreeGrid<T>();
							data[li]=d;
						} else if (!(d instanceof TreeGrid<?>)) {
							d=new TreeGrid<T>((T)d);
							data[li]=d;
						}
						TreeGrid<T> tg=(TreeGrid<T>)d;
						Object nd=tg.setBlockLocal(
								Maths.max(lx, x1)-lx,
								Maths.max(ly, y1)-ly,
								Maths.max(lz, z1)-lz,
								Maths.min(x2, ux)-lx,
								Maths.min(y2, uy)-ly,
								Maths.min(z2, uz)-lz,
								value,
								shift-DIM_SPLIT_BITS);
						if (nd!=d) {
							setData=true;
							data[li]=nd;
						}
					}
				}
			}
		}
		if (setData&&isSolid()) return data[0];
		return this;
	}

	public ArrayList<T> getObjectList(int x1, int y1, int z1, int x2, int y2, int z2) {
		final ArrayList<T> al=new ArrayList<T>();
		BlockVisitor<T> bv=new BlockVisitor<T>() {

			@Override
			public Object visit(int x1, int y1, int z1, int x2, int y2, int z2,
					T value) {
				al.add(value);
				return null;
			}
			
		};
		visitBlocks(bv,x1,y1,z1,x2,y2,z2);
		return al;
	}
	
	@Override
	public void validate() {
		super.validate();
		validateLocal(TOP_SHIFT);
	}
	
	@SuppressWarnings("unchecked")
	public void validateLocal(int shift) {	
		if (shift!=TOP_SHIFT&&isSolid()) throw new Error("Failed to solidify");
		for (int i=0; i<DATA_ARRAY_SIZE; i++) {
			Object o=data[i];
			if (o instanceof TreeGrid<?>) {
				TreeGrid<T> tg=(TreeGrid<T>)o;
				tg.validateLocal(shift-DIM_SPLIT_BITS);
			}
		}
	}
}
