package mikera.data;

import java.util.ArrayList;
import java.util.Arrays;

import mikera.util.Bits;
import mikera.util.emptyobjects.NullArrays;

// NOTE: Performance tests show that this is 
// slower than creating new byte arrays
// THEREFORE CURRENTLY NOT RECCOMMENDED

public final class ByteArrayCache {
	// cache of byte arrays
	// position 0=not used
	// position 1=length 1
	// position 2=length 2-3
	// ...
	@SuppressWarnings("unchecked")
	private static ArrayList<byte[]>[] cache=new ArrayList[32];
	
	private static final int CACHE_COUNT=10;
	
	public static byte[] getByteArray(int minLength) {
		if (minLength==0) return NullArrays.NULL_BYTES;
		int up=Bits.fillBitsRight(minLength-1);
		int i=Integer.bitCount(up)+1;
		
		ArrayList<byte[]> al=cache[i];
		synchronized (al) {
			int s=al.size();
			if (s>0) {
				byte[] ba=al.remove(s-1);
				return ba;
			}
		}
		return newByteArray(minLength);
	}
	
	public static void recycleByteArray(byte[] ba) {
		int up=Bits.fillBitsRight(ba.length);
		int i=Integer.bitCount(up);
		ArrayList<byte[]> al=cache[i];
		synchronized (al) {
			if (al.size()<CACHE_COUNT) {
				Arrays.fill(ba, (byte)0);
				al.add(ba);
			}
		}
	}
	
	public static byte[] newByteArray(int length) {
		return new byte[length];
	}
	
	public static int countCachedArrays() {
		int result=0;
		for (int i=0; i<32; i++) {
			result+=cache[i].size();
		}		
		return result;
	}
	
	static {
		for (int i=0; i<32; i++) {
			cache[i]=new ArrayList<byte[]>();
		}
	}
}
