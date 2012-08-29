package mikera.math;

import mikera.annotations.Mutable;

@Mutable
public final class Vector2 extends BaseVector {
	static final long serialVersionUID = 2170889059548078960L;

	public float x, y;

    public Vector2() {
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2 p) {
        x = p.x;
        y = p.y;
    }

    public final Vector2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public final Vector2 set(Vector2 p) {
        x = p.x;
        y = p.y;
        return this;
    }
    
    public float get(int i) {
        switch (i) {
            case 0:
                return x;
            case 1:
                return y;
            default:
            	throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public final String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }

	public int size() {
		return 2;
	}
	
	public Vector2 clone() {
		return new Vector2(this);
	}
}