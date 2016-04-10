package mikera.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.BitSet;


/**
 * Immutable bit string
 * 
 * @author Mike
 *
 */
public final class BitString implements Cloneable, Serializable{
	private static final long serialVersionUID = -4384823563702555173L;

	/**
	 * Zero-padded byte array containing bit data
	 */
	private final byte[] data;
	
	/**
	 * Length of bit string in bits - may not fill entire data array
	 */
	private final int length;
	
	public BitString() {
		this(0);
	}
	
	/**
	 * Construct a bitstring from a byte array.
	 * 
	 * Creates an internal defensive copt of the bytes.
	 * 
	 * @param bytes
	 */
	public BitString(byte[] bytes) {
		length=bytes.length<<3;
		data=bytes.clone();
	}
	
	public BitString(Data data) {
		length=data.size()<<3;
		this.data=data.toNewByteArray();
	}
	
	private BitString(int bitLength) {
		length=bitLength;
		data=new byte[bytesNeeded(bitLength)];
	}
	
	public BitString substring(int start, int end) {
		if ((start<0)||(end>length)) throw new IndexOutOfBoundsException();
		int newLength=end-start;
		BitString ss=new BitString(newLength);
		for (int i=0; i<newLength; i++) {
			ss.setWithOr(i,this.get(i+start));
		}
		return ss;
	}
	
	private static int bytesNeeded(int l) {
		return (l+7)>>3;
	}
	
	private void setWithOr(int index, boolean value) {
		data[index>>3]|=value?(1<<(index&7)):0;
	}
	
	public boolean get(int index) {
		return ((data[index>>3]>>(index&7))&1)!=0;
	}
	
	public byte getByte(int byteIndex) {
		return data[byteIndex];
	}

	/**
	 * Construct a BitString from a standard java.util.BitSet
	 * 
	 * Note that the length of the BitString is determined by the highest set bit in the BitSet.
	 * 
	 * @param bitset
	 */
	public BitString(BitSet bitset) {
		this(bitset.length());
		for (int i=0; i<length; i++) {
			data[i>>3]|=bitset.get(i)?(1<<(i&7)):0;
		}
	}
	
	/**
	 * Construct a cloned BitString using structural sharing
	 * @param bitString
	 */
	public BitString(BitString bitString) {
		length=bitString.length;
		data=bitString.data;
	}

	public int length() {
		return length;
	}
	
	public int byteLength() {
		return data.length;
	}
	
	public byte[] toByteArray() {
		return data.clone();
	}
	
	public Data toData() {
		return Data.wrap(toByteArray());
	}
	
	@Override
	public BitString clone() {
		return new BitString(this);
	}
	
	public BitSet toBitSet() {
		BitSet bitset=new BitSet(length);
		
		for (int i=0; i<length; i++) {
			bitset.set( i, ((data[i>>3]>>(i&7))&1)!=0);
		}		
		
		return bitset;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this==o) return true;
		
		if (!(o instanceof BitString)) return false;
		
		BitString bs=(BitString)o;
		
		if (length!=bs.length) return false;
		return Arrays.equals(data, bs.data);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}
	
	@Override
	public String toString() {
		return Arrays.toString(data);
	}
	
	public void validate() {
		if (data.length!=bytesNeeded(length)) throw new Error("Unexpected data length");
		int padBits=(8 * data.length)-length;
		if (((0xFF&data[data.length-1])>>>(8-padBits))!=0) throw new Error("Non-zero padding!");
	}
}
