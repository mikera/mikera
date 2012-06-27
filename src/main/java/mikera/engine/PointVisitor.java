/**
 * 
 */
package mikera.engine;

public abstract class PointVisitor<T> extends BlockVisitor<T> {
	public abstract Object visit(int x, int y, int z, T value);
	
	public Object visit(int x1, int y1, int z1,int x2, int y2, int z2, T value) {
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				for (int z = z1; z <= z2; z++) {
					visit(x,y,z,value);
				}
			}
		}
		return null;
	}
}