package mikera.engine;

import mikera.util.Maths;

public abstract class BaseGrid<T> extends Grid<T> implements Cloneable {
	
	@Override
	public BaseGrid<T> set(Grid<T> src) {		
		return (BaseGrid<T>) clear().paste(src);
	}
	
	@Override
	public BaseGrid<T> paste(Grid<T> src) {
		return paste(src,0,0,0);
	}
	
	@Override
	public int countNodes() {
		return 1;
	}
	
	@Override
	public void visitPoints(final PointVisitor<T> pointVisitor) {
		BlockVisitor<T> bv=new BlockVisitor<T>() {
			@Override
			public Object visit(int x1, int y1, int z1, int x2, int y2, int z2,
					T value) {
				for (int z=z1; z<=z2; z++) {
					for (int y=y1; y<=y2; y++) {
						for (int x=x1; x<=x2; x++) {
							pointVisitor.visit(x, y, z, value);
						}
					}
				}
				return null;
			}		
		};
		visitBlocks(bv);
	}
	
	@Override
	public void visitBlocks(final BlockVisitor<T> bf, final int xmin, final int ymin, final int zmin, final int xmax, final int ymax, final int zmax) {
		BlockVisitor<T> bv=new BlockVisitor<T>() {
			@Override
			public Object visit(int x1, int y1, int z1, int x2, int y2, int z2,
					T value) {
				x1=Maths.max(x1,xmin);
				x2=Maths.min(x2,xmax);
				y1=Maths.max(y1,ymin);
				y2=Maths.min(y2,ymax);
				z1=Maths.max(z1,zmin);
				z2=Maths.min(z2,zmax);
				if ((x1<=x2)&&(y1<=y2)&&(z1<=z2)) {
					bf.visit(x1, y1, z1, x2,y2,z2, value);
				}
				return null;
			}		
		};
		visitBlocks(bv);
	}

	
	@Override
	public void visitPoints(final PointVisitor<T> bf, final int xmin, final int ymin, final int zmin, final int xmax, final int ymax, final int zmax) {
		// by default, use a blockvisitor
		BlockVisitor<T> bv=new BlockVisitor<T>() {
			@Override
			public Object visit(int x1, int y1, int z1, int x2, int y2, int z2,
					T value) {
				if (value==null) return null;
				x1=Maths.max(x1,xmin);
				x2=Maths.min(x2,xmax);
				y1=Maths.max(y1,xmin);
				y2=Maths.min(y2,xmax);
				z1=Maths.max(z1,xmin);
				z2=Maths.min(z2,xmax);
				
				if ((z2<z1)||(y2<y1)||(x2<x1)) return null;
				
				for (int z=z1; z<=z2; z++) {
					for (int y=y1; y<=y2; y++) {
						for (int x=x1; x<=x2; x++) {
							// visit the point
							bf.visit(x, y, z, value);
						}
					}
				}
				return null;
			}		
		};
		visitBlocks(bv);
	}


	
	public void changeAll(final T value) {
		BlockVisitor<T> changer=new BlockVisitor<T>() {
			@Override
			public Object visit(int x1, int y1, int z1, int x2, int y2, int z2,
					T v) {
				if (v!=null) {
					setBlock(x1,y1,z1,
							x2, y2, z2, value);
				}
				return null;
			}
		};
		visitBlocks(changer);
	}

	@Override
	@SuppressWarnings("unchecked")
	public BaseGrid<T> paste(Grid<T> src, final int dx, final int dy, final int dz) {
		final Object[] tmp=new Object[] {this};
		BlockVisitor<T> paster=new BlockVisitor<T>() {
			@Override
			public Object visit(int x1, int y1, int z1, int x2, int y2, int z2,
					T value) {
				tmp[0]=((BaseGrid<T>)tmp[0]).setBlock(x1+dx,y1+dy,z1+dz,
						x2+dx, y2+dy, z2+dz, value);
				return null;
			}
		};
		src.visitBlocks(paster);
		return (BaseGrid<T>)tmp[0];
	}
	
	@Override
	public BaseGrid<T> setBlock(int x1, int y1, int z1, int x2, int y2, int z2, T value) {
		BaseGrid<T> result=this;
		for (int z=z1; z<=z2; z++) {
			for (int y=y1; y<=y2; y++) {
				for (int x=x1; x<=x2; x++) {
					result=(BaseGrid<T>)set(x,y,z,value);
				}	
			}		
		}
		return result;
	}
}
