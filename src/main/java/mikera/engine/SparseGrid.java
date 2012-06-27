package mikera.engine;

import mikera.util.Bits;

public class SparseGrid<T> extends BaseGrid<T> {
	
	private SparseArray<T> data;
	
	public SparseGrid() {
		clear();
	}
	
	@Override
	public SparseGrid<T> clear() {
		data=new SparseArray<T>();
		return this;
	}

	@Override
	public SparseGrid<T> clearContents() {
		data.clear();
		return this;
	}

	@Override
	public int countNonNull() {
		return data.countNonNull();
	}

	@Override
	public T get(int x, int y, int z) {
		long zz=calculateIndex(x, y, z);
		if (inRange(zz)) {
			return data.get(zz);
		} else {
			return null;
		}
	}

	@Override
	public SparseGrid<T> set(int x, int y, int z, T value) {
		long zz=calculateIndex(x, y, z);
		while (!inRange(zz)) {
			data=data.grow();
		}
		data.set(zz, value);
		return this;
	}
	
	private boolean inRange(long zz) {
		return (zz>=0)&&(zz<data.longSize());
	}
	
	public static long calculateIndex(int x, int y, int z) {
		return Octreap.calculateZ(Bits.zigzagEncodeInt(x), Bits.zigzagEncodeInt(y), Bits.zigzagEncodeInt(z));
	}

	@Override
	public void visitBlocks(BlockVisitor<T> bf) {
		visitBlocksLocal(bf,data,0);
	}

	@SuppressWarnings("unchecked")
	private void visitBlocksLocal(BlockVisitor<T> bf, SparseArray<T> sa, long index) {
		int n=sa.shiftSize();
		int shift=sa.getShift();
		for (int i=0; i<n; i++) {
			Object d=sa.getSubObject(i);
			if (d==null) continue;
			
			if (d instanceof SparseArray<?>) {
				visitBlocksLocal(bf,(SparseArray<T>)d,index+(((long)i)<<shift));
			} else {
				long startindex=index+(((long)i)<<shift);
				int nn=(1<<shift);
				for (int ii=0; ii<nn; ii++) {
					visitCell(bf,(T)d,startindex+ii);						
				}
			}
		}
	}

	private void visitCell(BlockVisitor<T> bf, T d, long index) {
		int x=Bits.zigzagDecodeInt(Octreap.extractX(index));
		int y=Bits.zigzagDecodeInt(Octreap.extractY(index));
		int z=Bits.zigzagDecodeInt(Octreap.extractZ(index));
		bf.visit(x, y, z, x, y, z, d);
	}

}
