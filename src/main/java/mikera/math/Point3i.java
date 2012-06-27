package mikera.math;

import java.io.Serializable;

public final class Point3i implements Comparable<Point3i>, Serializable {
	private static final long serialVersionUID = 6938913329599405951L;

	public int x, y, z;

    public Point3i() {
    }

    public Point3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3i(Point3i p) {
        x = p.x;
        y = p.y;
        z = p.z;
    }

    public int getComponent(int i) {
        switch (i) {
            case 0:
                return x;
            case 1:
                return y;
            default:
                return z;
        }
    }

    public final float distanceTo(Point3i p) {
        float dx = x - p.x;
        float dy = y - p.y;
        float dz = z - p.z;
        return (float) Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
    }

    public final float distanceTo(float px, float py, float pz) {
        float dx = x - px;
        float dy = y - py;
        float dz = z - pz;
        return (float) Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
    }

    public final float distanceToSquared(Point3i p) {
        float dx = x - p.x;
        float dy = y - p.y;
        float dz = z - p.z;
        return (dx * dx) + (dy * dy) + (dz * dz);
    }

    public final float distanceToSquared(float px, float py, float pz) {
        float dx = x - px;
        float dy = y - py;
        float dz = z - pz;
        return (dx * dx) + (dy * dy) + (dz * dz);
    }

    public final Point3i set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public final Point3i set(Point3i p) {
        x = p.x;
        y = p.y;
        z = p.z;
        return this;
    }

    public static final Point3i add(Point3i p1, Point3i p2, Point3i dest) {
        dest.x = p1.x + p2.x;
        dest.y = p1.y + p2.y;
        dest.z = p1.z + p2.z;
        return dest;
    }

    public static final Point3i sub(Point3i p1, Point3i p2, Point3i dest) {
        dest.x = p1.x - p2.x;
        dest.y = p1.y - p2.y;
        dest.z = p1.z - p2.z;
        return dest;
    }


    
    @Override
    public final String toString() {
        return "("+x+", "+y+", "+z+")";
    }
    
    public final Vector3 toVector3() {
        return new Vector3(x,y,z);
    }
    
	public boolean equals(Object o) {
		if (o instanceof Point3i) {
			return equals((Point3i)o);
		}
		return false;
	}
    
	public boolean equals(Point3i o) {
		return (o==this)||((x==o.x)&&(y==o.y)&&(z==o.z));
	}
	
	@Override
	public int hashCode() {
		return (x^(x<<20))+(y^(y<<10))+(z^(z<<15));
	}

	public int compareTo(Point3i o) {
		int c=x-o.x;
		if (c!=0) return c;
		c=y-o.y;
		if (c!=0) return c;
		c=z-o.z;
		return c;
	}
}