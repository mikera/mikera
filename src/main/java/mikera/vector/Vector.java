package mikera.vector;

public final class Vector {
	public final double[] data;
	
	public final int offset;
	public final int length;
	
	public Vector(double[] values) {
		offset=0;
		length=values.length;
		data=new double[length];
		System.arraycopy(values, 0, data, 0, length);
	}
	
	public Vector (int length) {
		this.length=length;
		offset=0;
		data=new double[length];
	}
	
	public Vector(Vector vector, int offset, int length) {
		if (offset<0) {
			throw new IndexOutOfBoundsException("Negative offset for Vector: "+offset);
		}
		if (offset+length>vector.length) {
			throw new IndexOutOfBoundsException("Beyond bounds of parent vector with offset: "+offset+" and length: "+length);
		}
		this.data=vector.data;
		this.length=length;
		this.offset=vector.offset+offset;
	}
	
	public int length() {
		return length;
	}
	
	public Vector subVector(int offset, int length) {
		return new Vector(this,offset,length);
	}
	
	public double get(int i) {
		if ((i<0)||(i>length)) throw new IndexOutOfBoundsException("Index = "+i);
		return data [offset+i];
	}
	
	
	public void set(int i, double value) {
		if ((i<0)||(i>length))  throw new IndexOutOfBoundsException("Index = "+i);
		data[offset+i]=value;
	}
}
