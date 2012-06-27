package mikera.math;

import mikera.annotations.Mutable;
import mikera.util.Maths;

/**
 * Multi-dimension vector class
 * 
 * @author Mike
 *
 */
@Mutable
public final class Vector extends BaseVector {
	private static final long serialVersionUID = -265007091119573847L;

	public final float[] data;
	
	public Vector(int size) {
		data=new float[size];
	}
	
	public Vector(float x, float y) {
		this(2);
		data[0]=x;
		data[1]=y;
	}
	
	public Vector(float x, float y, float z) {
		this(3);
		data[0]=x;
		data[1]=y;
		data[2]=z;
	}
	
	public Vector(float x, float y, float z, float t) {
		this(4);
		data[0]=x;
		data[1]=y;
		data[2]=z;
		data[3]=t;
	}
	
	public Vector(double x, double y, double z) {
		this(3);
		data[0]=(float)x;
		data[1]=(float)y;
		data[2]=(float)z;
	}
	
	public Vector(double x, double y, double z, double t) {
		this(4);
		data[0]=(float)x;
		data[1]=(float)y;
		data[2]=(float)z;
		data[3]=(float)t;
	}
	
	public Vector(int x, int y, int z) {
		this(3);
		data[0]=x;
		data[1]=y;
		data[2]=z;
	}
	
	public Vector(Vector a) {
		this(a.data.clone());
	}
	
	public float x() {
		return data[0];
	}
	
	public float y() {
		return data[1];
	}
	
	public float z() {
		return data[2];
	}
	
	public void set(int x, int y, int z) {
		data[0]=x;
		data[1]=y;
		data[2]=z;
	}
	
	public void set(float x, float y, float z) {
		data[0]=x;
		data[1]=y;
		data[2]=z;
	}
	
	public void set(float[] xs, int offset) {
		for (int i=0; i<data.length; i++) {
			data[i]=xs[offset+i];
		}
	}
	
	public int toRGBColour() {
		int col=0xFF000000;
		col|=   0x00FF0000 & (((int)(data[0]*255))<<16);
		col|=   0x0000FF00 & (((int)(data[1]*255))<<8);
		col|=   0x000000FF & (((int)(data[2]*255)));		
		return col;
	}
	
	public int toARGBColour() {
		// note: vector format is RGBA
		int col=0xFF000000 & (((int)(data[3]*255))<<24);
		col|=   0x00FF0000 & (((int)(data[0]*255))<<16);
		col|=   0x0000FF00 & (((int)(data[1]*255))<<8);
		col|=   0x000000FF & (((int)(data[2]*255)));		
		return col;
	}
	
	public void set(double x, double y, double z) {
		data[0]=(float)x;
		data[1]=(float)y;
		data[2]=(float)z;
	}
	
	public void set(Vector v) {
		data[0]=v.data[0];
		data[1]=v.data[1];
		data[2]=v.data[2];
	}
	
	public void add(float x, float y, float z) {
		data[0]+=x;
		data[1]+=y;
		data[2]+=z;
	}
	
	public void subtract(float[] xs, int offset) {
		for (int i=0; i<data.length; i++) {
			data[i]-=xs[offset+i];
		}		
	}
	
	private Vector(float[] adata) {
		data=adata;
	}
	
	public Vector(Vector3 vector) {
		this(3);
		data[0]=vector.x;
		data[1]=vector.y;
		data[2]=vector.z;		
	}
	
	public Vector(float[] adata, int offset, int size) {
		data=new float[size];
		System.arraycopy(adata, offset, data, 0, size);
	}
	
	public Vector(double x, double y) {
		this(2);
		data[0]=(float)x;
		data[1]=(float)y;
	}

	public static Vector construct(float[] dataToEmbed) {
		Vector v=new Vector(dataToEmbed);
		return v;
	}
	
	public static Vector create(float[] data) {
		Vector v=new Vector(data.clone());
		return v;
	}
	
	public Vector resize(int newSize) {
		Vector v=new Vector(newSize);
		int n=Maths.min(data.length, newSize);
		System.arraycopy(data, 0, v.data, 0, n);
		return v;
	}
		
	public int size() {
		return data.length;
	}
	
	public String toString() {
		return toString(data);
	}
	
	public static String toString(final float[] data) {
		StringBuilder result=new StringBuilder("{");
		int max=data.length;
		for (int i=0; i<max; i++) {
			if(i>0) result.append(", ");
			result.append(Float.toString(data[i]));
		}
		result.append("}");
		return result.toString();
	}
	
	public boolean equals(Vector v) {
		if (this==v) return true;
		int len=data.length;
		if (v.data.length!=len) return false;
		for (int i=0; i<len; i++) {
			if (data[i]!=v.data[i]) return false;
		}
		return true;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Vector) {
			return this.equals((Vector)o);
		}
		return false;
	}
	
	public int hashCode() {
		int result=178;
		for (int i=0; i<data.length; i++) {
			Integer.rotateLeft(result, 7);
			result^= Float.floatToIntBits(data[i]);
		}
		return result;
	}
	
	public void add(Vector v) {
		for (int i=0; i<data.length; i++) {
			data[i]+=v.data[i];
		}
	}

	public void addMultiple(Vector v, float factor) {
		for (int i=0; i<data.length; i++) {
			data[i]+=v.data[i]*factor;
		}
	}
	
	public void set(int index, float value) {
		data[index]=value;
	}
	
	public void scale(float f) {
		for (int i=0; i<data.length; i++) {
			data[i]*=f;
		}
	}
	
	public float dot(Vector v) {
		float result=0;
		for (int i=0; i<data.length; i++) {
			result+=data[i]*v.data[i];
		}
		return result;
	}
	
	public static float dot3(float[] v1, float[] v2) {
		return v1[0]*v2[0]+v1[1]*v2[1]+v1[2]*v2[2];
	}
	
	public float lengthSquared() {
		return lengthSquared(data);	
	}
	
	public static float lengthSquared(float[] data) {
		float result=0;
		for (int i=0; i<data.length; i++) {
			result+=data[i]*data[i];
		}
		return result;			
	}
	
	public float length() {
		return Maths.sqrt(lengthSquared());
	}
	
	public void cross(Vector v) {
		float x=data[1]*v.data[2]-data[2]*v.data[1];
		float y=data[2]*v.data[0]-data[0]*v.data[2];
		float z=data[0]*v.data[1]-data[1]*v.data[0];
		data[0]=x;
		data[1]=y;
		data[2]=z;
	}
	
	
	public static Vector cross(Vector a, Vector b) {
		Vector target=new Vector(3);
		Vector.cross(a.data,0,b.data,0,target.data,0);
		return target;
	}
	
	public static void cross(Vector a, Vector b, Vector target) {
		Vector.cross(a.data,0,b.data,0,target.data,0);
	}
	
	public static void cross(float[] adata, int ai, float[]  bdata, int bi, float[] tdata, int ti) {
		float x=adata[ai+1]*bdata[bi+2]-adata[ai+2]*bdata[bi+1];
		float y=adata[ai+2]*bdata[bi+0]-adata[ai+0]*bdata[bi+2];
		float z=adata[ai+0]*bdata[bi+1]-adata[ai+1]*bdata[bi+0];	
		tdata[ti]=x;
		tdata[ti+1]=y;
		tdata[ti+2]=z;
	}
	
	
	public static float lengthSquared(float[] data, int di, int n) {
		float result=0;
		for (int i=0; i<n; i++) {
			float f=data[di+i];
			result+=f*f;
		}
		return result;			
	}	
	
	public static float lengthSquared(float dx, float dy) {
		return dx*dx+dy*dy;
	}

	
	public float normalise() {
		return Vector.normalise(data,0,data.length);
	}
	
	public Vector clone() {
		return new Vector(this);
	}
	
	public static float normalise(float[] data, int di, int n) {
		float f=lengthSquared(data, di, n);
		float factor;
		if (f!=0.0) {
			f=(float)Math.sqrt(f);
			factor=1.0f/f;
		} else {
			return 0.0f;
		}
		for (int i=0; i<n; i++) {
			data[di+i]*=factor;
		}
		return f;
	}

	public void fill(float v) {
		int l=data.length;
		for (int i=0; i<l; i++) {
			data[i]=v;
		}
	}

	public float get(int i) {
		return data[i];
	}

	public static float distanceBetween(Vector targetPos, Vector cameraPos) {
		float res=0;
		for (int i=0; i<targetPos.data.length; i++) {
			float diff=targetPos.data[i]-cameraPos.data[i];
			res+=diff*diff;
		}
		return Maths.sqrt(res);
	}

	public static void set(final float[] dst, final float[] src) {
		System.arraycopy(src, 0, dst, 0, dst.length);
	}
	
	public static void multiply(final float[] dst, float f) {
		for (int i=0; i<dst.length; i++) {
			dst[i]*=f;
		}
	}
	
	public static void addMultiple(final float[] dst, final float[] src, float f) {
		for (int i=0; i<dst.length; i++) {
			dst[i]+=src[i]*f;
		}
	}


}
