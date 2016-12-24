package mikera.engine;

/**
 * Interface for callback objects that can be used to visit points in a grid.
 * @author Mike
 *
 */
public interface IPointVisitor<T> {
	/**
	 * Methods to be called for all points visited. Should be overriden by all IPointVisitor instances
	 * @param x The x-coordinate of the point visited
	 * @param y The y-coordinate of the point visited
	 * @param z The z-coordinate of the point visited
	 * @param value The value of the grid at the point visited
	 * @return
	 */
	Object visit(int x, int y, int z, T value);

}
