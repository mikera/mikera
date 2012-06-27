package mikera.util.emptyobjects;

import mikera.data.DataType;

public class NullArrays {
	public static final Object[] NULL_OBJECTS=new Object[0];
	public static final float[] NULL_FLOATS=new float[0];
	public static final double[] NULL_DOUBLES=new double[0];
	public static final int[] NULL_INTS=new int[0];
	public static final long[] NULL_LONGS=new long[0];
	public static final char[] NULL_CHARS=new char[0];
	public static final short[] NULL_SHORTS=new short[0];
	public static final byte[] NULL_BYTES=new byte[0];
	public static final boolean[] NULL_BOOLEANS=new boolean[0];
	public static final String[] NULL_STRINGS=new String[0];

	public static Object getNullArray(DataType dt) {
		switch (dt) {
			case OBJECT: return NULL_OBJECTS;
			case BYTE: return NULL_BYTES;
			case SHORT: return NULL_SHORTS;
			case INTEGER: return NULL_INTS;
			case LONG: return NULL_LONGS;
			case BOOLEAN: return NULL_BOOLEANS;
			case CHAR: return NULL_CHARS;
			case FLOAT: return NULL_FLOATS;
			case DOUBLE: return NULL_DOUBLES;
			case STRING: return NULL_STRINGS;
			default:
				throw new Error("Datatype not recognised");
		}
	}

}
