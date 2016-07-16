package mikera.persistent;
import mikera.annotations.Immutable;

/**
 * Base for all mikera.persistent classes
 * 
 * @author Mike Anderson
 *
 */
@Immutable
public abstract class PersistentObject implements IPersistentObject {
	private static final long serialVersionUID = -4077880416849448410L;

	/**
	 * Clone returns the same object since PersistentObject
	 * and all subclasses must be immutable
	 * 
	 */
	@Override
	public PersistentObject clone() {
		return this;
	}
	
	@Override
	public boolean hasFastHashCode() {
		return false;
	}
	
	@Override
	public void validate() {
		if (!this.clone().equals(this)) {
			throw new Error("Clone problem!");
		}
	}
}
