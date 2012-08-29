package mikera.math;

import mikera.annotations.Mutable;
import mikera.util.Maths;

@Mutable
public final class Vector3 extends BaseVector {
	static final long serialVersionUID = 5710579313106988144L;

	public float x;
    public float y;
    public float z;

    public Vector3() {
    }

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3 v) {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    public float get(int i) {
        switch (i) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
            default:
            	throw new IndexOutOfBoundsException();
        }
    }

    public final float length() {
        return (float) Math.sqrt((x * x) + (y * y) + (z * z));
    }

    public final float lengthSquared() {
        return (x * x) + (y * y) + (z * z);
    }

    public final Vector3 negate() {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }

    public final Vector3 negate(Vector3 dest) {
        dest.x = -x;
        dest.y = -y;
        dest.z = -z;
        return dest;
    }

    public final Vector3 multiply(float s) {
        x *= s;
        y *= s;
        z *= s;
        return this;
    }

    public final Vector3 multiply(float s, Vector3 dest) {
        dest.x = x * s;
        dest.y = y * s;
        dest.z = z * s;
        return dest;
    }

    public final Vector3 divide(float d) {
        x /= d;
        y /= d;
        z /= d;
        return this;
    }

    public final Vector3 divide(float d, Vector3 dest) {
        dest.x = x / d;
        dest.y = y / d;
        dest.z = z / d;
        return dest;
    }

    public final float normalizeAndReturnLength() {
        float n = Maths.sqrt(x * x + y * y + z * z);
        float in = 1.0f / n;
        x *= in;
        y *= in;
        z *= in;
        return n;
    }

    public final Vector3 normalize() {
        float in = 1.0f / Maths.sqrt((x * x) + (y * y) + (z * z));
        x *= in;
        y *= in;
        z *= in;
        return this;
    }

    public final Vector3 normalize(Vector3 dest) {
        float in = 1.0f / (float) Math.sqrt((x * x) + (y * y) + (z * z));
        dest.x = x * in;
        dest.y = y * in;
        dest.z = z * in;
        return dest;
    }

    public final Vector3 set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public final Vector3 set(Vector3 v) {
        x = v.x;
        y = v.y;
        z = v.z;
        return this;
    }

    public final float dot(float vx, float vy, float vz) {
        return vx * x + vy * y + vz * z;
    }

    public static final float dot(Vector3 v1, Vector3 v2) {
        return (v1.x * v2.x) + (v1.y * v2.y) + (v1.z * v2.z);
    }

    public static final Vector3 cross(Vector3 v1, Vector3 v2, Vector3 dest) {
        dest.x = (v1.y * v2.z) - (v1.z * v2.y);
        dest.y = (v1.z * v2.x) - (v1.x * v2.z);
        dest.z = (v1.x * v2.y) - (v1.y * v2.x);
        return dest;
    }

    public static final Vector3 add(Vector3 v1, Vector3 v2, Vector3 dest) {
        dest.x = v1.x + v2.x;
        dest.y = v1.y + v2.y;
        dest.z = v1.z + v2.z;
        return dest;
    }

    public static final Vector3 sub(Vector3 v1, Vector3 v2, Vector3 dest) {
        dest.x = v1.x - v2.x;
        dest.y = v1.y - v2.y;
        dest.z = v1.z - v2.z;
        return dest;
    }
    
    public static final Vector3 mid(Vector3 p1, Vector3 p2, Vector3 dest) {
        dest.x = 0.5f * (p1.x + p2.x);
        dest.y = 0.5f * (p1.y + p2.y);
        dest.z = 0.5f * (p1.z + p2.z);
        return dest;
    }

	public int size() {
		return 3;
	}
	
	public Vector3 clone() {
		return new Vector3(this);
	}
}