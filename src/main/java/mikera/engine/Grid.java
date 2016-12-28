package mikera.engine;

import mikera.annotations.Mutable;

@Mutable
public abstract class Grid<T> {
	// core get/set functions
	
	public abstract Grid<T> set(int x, int y, int z, T value);
	
	public abstract T get(int x, int y, int z);
	
	// query functions
	
	public abstract int countNodes();
	
	public abstract int countNonNull();
	
	// visitor functions
	
	/**
	 * Visits all non-null Points in this grid
	 * @param pointVisitor
	 */
	public abstract void visitPoints(IPointVisitor<T> pointVisitor);

	/**
	 * Visits all non-null Blocks in this grid
	 * @param pointVisitor
	 */
	public abstract void visitBlocks(IBlockVisitor<T> blockVisitor);

	/**
	 * Visits all non-null points in the grid.
	 * 
	 * Visiting is intended to be partially ordered in increasing x,y,z
	 * But this depends on the grid implementation
	 */
	public abstract void visitPoints(IPointVisitor<T> pointVisitor, int xmin, int ymin, int zmin, int xmax, int ymax, int zmax);

	/**
	 * Visits all non-null blocks in this grid.
	 * 
	 * Visiting is guaranteed to be partially ordered in increasing x,y,z
	 */
	public abstract void visitBlocks(IBlockVisitor<T> blockVisitor, int xmin, int xmax, int ymin, int ymax, int zmin, int zmax);

	/**
	 * Traces all points from a given start point in a given direction,
	 * up to the maximum range (inclusive)
	 * Calls the point visitor for all non-null points.
	 * @param value
	 * @return
	 */
	public abstract void trace(IPointVisitor<T> pointVisitor, double x, double y, double z, double dx, double dy, double dz, double range);
	
	// bulk change operations
	
	public abstract Grid<T> setBlock(int x1, int y1, int z1, int x2, int y2, int z2, T value);

	public abstract Grid<T> clear();

	public abstract Grid<T> clearContents();

	public abstract Grid<T> paste(Grid<T> src);
	
	public abstract Grid<T> paste(Grid<T> src, final int dx, final int dy, final int dz);
	
	public abstract Grid<T> set(Grid<T> src);
	
	
	
	public static boolean pointWithin(int x, int y, int z, int x1, int y1, int z1, int x2, int y2, int z2) {
		return ((x>=x1)&&(x<=x2)&&(y>=y1)&&(y<=y2)&&(z>=z1)&&(z<=z2));
	}
	
	public static boolean areaIntersect(int x1, int y1, int z1, int x2, int y2, int z2, int x3, int y3, int z3, int x4, int y4, int z4) {
		return ((x4>=x1)&&(x3<=x2)&&(y4>=y1)&&(y3<=y2)&&(z4>=z1)&&(z3<=z2));
	}
	
	public void validate() {
		// TODO: some validation
	}
}
