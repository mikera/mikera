package mikera.engine;

import java.util.ArrayList;
import java.util.Arrays;

import mikera.util.Maths;
import mikera.util.Tools;

/**
 * Persistent data structure implementation of Grid implemented as a hierarchy of 4*4*4 grids
 * 
 * Top level is offset to centre at (0,0,0)
 * 
 * @author Mike Anderson
 *
 * @param <T>
 */
public class PersistentTreeGrid<T> extends BaseGrid<T> {

	private static final int DIM_SPLIT_BITS=2;
	private static final int DIM_SPLIT_MASK=(1<<DIM_SPLIT_BITS)-1;
	private static final int SIGNIFICANT_BITS=DIM_SPLIT_BITS*(20/DIM_SPLIT_BITS);
	private static final int TOP_SHIFT=SIGNIFICANT_BITS-DIM_SPLIT_BITS;
	private static final int DATA_ARRAY_SIZE=1<<(3*DIM_SPLIT_BITS);
	private static final int SIGNIFICANT_MASK=(1<<SIGNIFICANT_BITS)-1;
	private static final int TOP_OFFSET=(1<<(SIGNIFICANT_BITS-1));
	private static final int TOP_MAX=SIGNIFICANT_MASK;
	
	@SuppressWarnings("rawtypes")
	public static final PersistentTreeGrid EMPTY=new PersistentTreeGrid();
	
	// each cell contains either object of type T or a sub-grid
	private final Object[] data;
	
	public int countNonNull() {
		return countNonNull(TOP_SHIFT);
	}
	
	@SuppressWarnings("unchecked")
	public int countNodes() {
		int res=0;
		for (int i=0; i<DATA_ARRAY_SIZE; i++) {
			Object d=data[i];
			if (d==null) continue;
			if (d instanceof PersistentTreeGrid<?>) {
				PersistentTreeGrid<T> tg=(PersistentTreeGrid<T>)d;
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
			if (d instanceof PersistentTreeGrid<?>) {
				if (shift<=0) throw new Error("TreeGrid element where shift="+shift);
				PersistentTreeGrid<T> tg=(PersistentTreeGrid<T>)d;
				res+=tg.countNonNull(shift-DIM_SPLIT_BITS);
			} else {
				res+=1<<(3*shift);
			}
		}
		return res;
	}
	
	public T get(int x, int y, int z) {
		return getLocal(x+TOP_OFFSET,y+TOP_OFFSET,z+TOP_OFFSET);
	}
	
	@SuppressWarnings("unchecked")
	private T getLocal(final int x, final int y, final int z) {
		int shift=TOP_SHIFT;
		PersistentTreeGrid<T> head=this;
		while (shift>=0) {
			int li;
			// for some reason the inline version is much faster!!
			// li=index(x,y,z,shift);
			li= ((x>>shift)&DIM_SPLIT_MASK) + (((y>>shift)&DIM_SPLIT_MASK)<<DIM_SPLIT_BITS) + (((z>>shift)&DIM_SPLIT_MASK)<<(DIM_SPLIT_BITS*2));
			
			//if (li!=li1) System.err.println(((x>>(shift))&3)+","+((y>>(shift))&3)+","+((z>>(shift))&3)+"@"+shift+"   "+li+"->"+li1);
			
			Object d=head.data[li];
			if (d==null) return null;
			if (!(d instanceof PersistentTreeGrid<?>)) {
				return (T)d;
			}
			shift-=DIM_SPLIT_BITS;
			head=(PersistentTreeGrid<T>)d;
		}
		throw new Error("This shouldn't happen!!");
	}
	
	public void visitBlocks(BlockVisitor<T> bf) {
		visitBlocksLocal(bf,
				0,0,0,
				0,0,0,
				TOP_MAX,TOP_MAX,TOP_MAX,
				TOP_SHIFT);
	}
	
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
					if (d instanceof PersistentTreeGrid<?>) {
						PersistentTreeGrid<T> tg=(PersistentTreeGrid<T>)d;
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
	public PersistentTreeGrid<T> clear() {
		return EMPTY;
	}
	
	@Override
	public PersistentTreeGrid<T> clearContents() {
		return clear();
	}
	
	public PersistentTreeGrid() {
		 data=new Object[DATA_ARRAY_SIZE];
	}
	
	public PersistentTreeGrid(T defaultvalue) {
		data=new Object[DATA_ARRAY_SIZE];
		for (int i=0; i<data.length; i++) {
			data[i]=defaultvalue;
		}
	}
	
	private PersistentTreeGrid(Object[] arrayToUse) {
		data=arrayToUse;
	}

	@SuppressWarnings("unchecked")
	public PersistentTreeGrid<T> set(int x, int y, int z, T value) {
		return (PersistentTreeGrid<T>) setLocal(x+TOP_OFFSET,y+TOP_OFFSET,z+TOP_OFFSET,value,TOP_SHIFT);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object setLocal(int x, int y, int z, T value, int shift) {
		
		// int li = index(x,y,z,shift);
		int li=((x>>shift)&DIM_SPLIT_MASK) + (((y>>shift)&DIM_SPLIT_MASK)<<DIM_SPLIT_BITS) + (((z>>shift)&DIM_SPLIT_MASK)<<(DIM_SPLIT_BITS*2));
		Object d=data[li];
		if (Tools.equalsWithNulls(value, d)) return this;
		
		if ((d==null)&&(shift>0)) {
			// create child array with single non-null value
			Object[] arr=setOrCreateArray(data.clone(), li,createLocal(x,y,z,value,null,shift-DIM_SPLIT_BITS));
			return new PersistentTreeGrid(arr);
		}
		
		if (shift==0) {
			if (isSolid(data,value,li)) return value;
			Object[] arr=setOrCreateArray(data.clone(), li,value);
			return new PersistentTreeGrid(arr);
		} else if (!(d instanceof PersistentTreeGrid<?>)) {
			if (d.equals(value)) return this;
			PersistentTreeGrid<T> sub=createLocal(x,y,z,value,(T)d,shift-DIM_SPLIT_BITS);
			Object[] arr=setOrCreateArray(data.clone(), li,sub);
			return new PersistentTreeGrid(arr);
		}
		PersistentTreeGrid<T> sg=(PersistentTreeGrid<T>)d;
		Object sub=sg.setLocal(x,y,z,value,shift-DIM_SPLIT_BITS);
		if (sub==sg) return this;
		
		if ((shift<TOP_SHIFT)&&isSolid(data,sub,li)) {
			return sub;
		}
		
		Object[] arr=setOrCreateArray(data.clone(), li,sub);
		return new PersistentTreeGrid(arr);		
	}
	
	private static <T> PersistentTreeGrid<T> createLocal(int x, int y, int z, T value, T fill, int shift) {
		Object[] newData=new Object[DATA_ARRAY_SIZE];
		if (fill!=null) Arrays.fill(newData, fill);
		
		int li=((x>>shift)&DIM_SPLIT_MASK) + (((y>>shift)&DIM_SPLIT_MASK)<<DIM_SPLIT_BITS) + (((z>>shift)&DIM_SPLIT_MASK)<<(DIM_SPLIT_BITS*2));
		if (shift==0) {
			newData[li]=value;
		} else {
			newData[li]=createLocal(x,y,z,value,fill,shift-DIM_SPLIT_BITS);
		}
		return new PersistentTreeGrid<T> (newData);		
	}	
	
	private static final Object[] setOrCreateArray(Object[] arr, int pos, Object value) {
		if (arr==null) {
			arr=new Object[DATA_ARRAY_SIZE];
		}
		arr[pos]=value;
		return arr;
	}
	
	private static <T> boolean isSolid(Object[] arr,T value, int pos) {
		for (int i=0; i<pos; i++) {
			Object d=arr[i];
			if (!(Tools.equalsWithNulls(value,d))) {
				return false;
			}
		}
		
		for (int i=pos+1; i<DATA_ARRAY_SIZE; i++) {
			Object d=arr[i];
			if (!(Tools.equalsWithNulls(value,d))) {
				return false;
			}
		}
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private boolean isSolid() {
		Object d=data[0];
		return (!(d instanceof PersistentTreeGrid<?>))&&isSolid(data,(T)d,0);
	}
	
	private static final int index(int x, int y, int z, int shift) {
		int lx=(x>>shift)&DIM_SPLIT_MASK;
		int ly=(y>>shift)&DIM_SPLIT_MASK;
		int lz=(z>>shift)&DIM_SPLIT_MASK;
		int li=lx+(ly<<DIM_SPLIT_BITS)+(lz<<(2*DIM_SPLIT_BITS));
		return li;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public PersistentTreeGrid<T> setBlock(int x1, int y1, int z1, int x2, int y2, int z2, T value) {
		return (PersistentTreeGrid<T>) setBlockLocal(
				x1+TOP_OFFSET, 
				y1+TOP_OFFSET, 
				z1+TOP_OFFSET, 
				x2+TOP_OFFSET, 
				y2+TOP_OFFSET, 
				z2+TOP_OFFSET, 
				value,
				TOP_SHIFT);
	}

	// TODO: make persistent update
	@SuppressWarnings("unchecked")
	protected Object setBlockLocal(int x1, int y1, int z1, int x2, int y2, int z2, T value, int shift) {
		int bmask=3<<shift;
		int bstep=1<<shift;
		
		// get coordinates of sub block containing point 1
		// note masking to keep correct sign
		int bx1=((x1)&(bmask));
		int by1=((y1)&(bmask));
		int bz1=((z1)&(bmask));
	
		Object[] newData=null;
		
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
						newData=setOrCreateArray((newData==null)?data.clone():newData,li,value);
					} else {
						if (d==null) {
							if (value==null) continue;
							PersistentTreeGrid<T> subGrid=(PersistentTreeGrid<T>)
								EMPTY.setBlockLocal(
										Maths.max(lx, x1)-lx,
										Maths.max(ly, y1)-ly,
										Maths.max(lz, z1)-lz,
										Maths.min(x2, ux)-lx,
										Maths.min(y2, uy)-ly,
										Maths.min(z2, uz)-lz,
										value, 
										shift-DIM_SPLIT_BITS);
							newData=setOrCreateArray((newData==null)?data.clone():newData,li,subGrid);
						} else if (!(d instanceof PersistentTreeGrid<?>)) {
							if (d.equals(value)) continue;
							PersistentTreeGrid<T> subGrid=(PersistentTreeGrid<T>)
								new PersistentTreeGrid<T>((T)d).setBlockLocal(
										Maths.max(lx, x1)-lx,
										Maths.max(ly, y1)-ly,
										Maths.max(lz, z1)-lz,
										Maths.min(x2, ux)-lx,
										Maths.min(y2, uy)-ly,
										Maths.min(z2, uz)-lz,
										value, 
										shift-DIM_SPLIT_BITS);
							newData=setOrCreateArray((newData==null)?data.clone():newData,li,subGrid);
						} else {
							PersistentTreeGrid<T> tg=(PersistentTreeGrid<T>)d;
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
								newData=setOrCreateArray((newData==null)?data.clone():newData,li,nd);
							}
						}
					}
				}
			}
		}
		if (newData==null) return this;
		
		if ((shift<TOP_SHIFT)&&isSolid(newData,newData[0],0)) {
			return newData[0];
		}
		
		return new PersistentTreeGrid<T>(newData);
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
			if (o instanceof PersistentTreeGrid<?>) {
				PersistentTreeGrid<T> tg=(PersistentTreeGrid<T>)o;
				tg.validateLocal(shift-DIM_SPLIT_BITS);
			}
		}
	}
}
