package mikera.math;

import java.io.Serializable;

import mikera.annotations.Mutable;

@Mutable
public final class Point2i implements Comparable<Point2i>, Serializable {
	private static final long serialVersionUID = -7212240628840421412L;

	public int x, y;

    public Point2i() {
    }

    public Point2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point2i(Point2i p) {
        x = p.x;
        y = p.y;
    }

    public int getComponent(int i) {
        switch (i) {
            case 0:
                return x;
            case 1:
                return y;
            default:
                throw new IllegalArgumentException();
        }
    }

    public final float distanceTo(Point2i p) {
        float dx = x - p.x;
        float dy = y - p.y;
        return (float) Math.sqrt((dx * dx) + (dy * dy));
    }

    public final float distanceTo(float px, float py, float pz) {
        float dx = x - px;
        float dy = y - py;
        return (float) Math.sqrt((dx * dx) + (dy * dy) );
    }

    public final float distanceToSquared(Point2i p) {
        float dx = x - p.x;
        float dy = y - p.y;
        return (dx * dx) + (dy * dy);
    }

    public final float distanceToSquared(float px, float py) {
        float dx = x - px;
        float dy = y - py;
        return (dx * dx) + (dy * dy);
    }

    public final Point2i set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        return this;
    }

    public final Point2i set(Point2i p) {
        x = p.x;
        y = p.y;
        return this;
    }

    public static final Point2i add(Point2i p1, Point2i p2, Point2i dest) {
        dest.x = p1.x + p2.x;
        dest.y = p1.y + p2.y;
        return dest;
    }

    public static final Point2i sub(Point2i p1, Point2i p2, Point2i dest) {
        dest.x = p1.x - p2.x;
        dest.y = p1.y - p2.y;
        return dest;
    }


    
    @Override
    public final String toString() {
        return "("+x+", "+y+")";
    }
    
    public final Vector2 toVector2() {
        return new Vector2(x,y);
    }
    
    public final Vector3 toVector3() {
        return new Vector3(x,y,0);
    }
    
	public boolean equals(Object o) {
		if (o instanceof Point2i) {
			return equals((Point2i)o);
		}
		return false;
	}
    
	public boolean equals(Point2i o) {
		return (o==this)||((x==o.x)&&(y==o.y));
	}
	
	@Override
	public int hashCode() {
		return (x^(x<<20))+(y^(y<<10));
	}

	public int compareTo(Point2i o) {
		int c=x-o.x;
		if (c!=0) return c;
		c=y-o.y;
		return c;
	}
}