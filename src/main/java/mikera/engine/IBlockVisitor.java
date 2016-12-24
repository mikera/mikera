package mikera.engine;

/**
 * Interface for callback objects that can be used to visit blocks in a grid.
 * Blocks are efficiently-stored grid regions containing an identical value
 * @author Mike
 *
 */
public interface IBlockVisitor<T> {
	/**
	 * Method to be called for all blocks visited. 
	 * 
	 * The area of the block is (x1,y1,z1) to (x2,y2,z2) inclusive.
	 * 
	 * Should be overriden by all IBlockVisitor instances
	 * @param value The value of the grid at the point visited
	 * @return
	 */
	Object visit(int x1, int y1, int z1, int x2, int y2, int z2, T value);

}
