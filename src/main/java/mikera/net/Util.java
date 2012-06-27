package mikera.net;

import java.nio.ByteBuffer;

import mikera.data.Data;

public class Util {

	
	public static int writeCompacted(ByteBuffer bb, long a) {
		byte b=(byte)(a&127); // get bottom 7 bits
		a>>=7;
		int i=0;
		while ((((b&64)==0)&&(a!=0))||(((b&64)!=0)&&(a!=-1))) {
			b|=128;
			bb.put(b); i++;
			b=(byte)(a&127);
			a>>=7;
		}
		bb.put(b); i++;
		return i;
	}
	
	public static int writeCompacted(Data data, long a) {
		byte b=(byte)(a&127); // get bottom 7 bits
		a>>=7;
		int i=0;
		while ((((b&64)==0)&&(a!=0))||(((b&64)!=0)&&(a!=-1))) {
			b|=128;
			data.appendByte(b); i++;
			b=(byte)(a&127);
			a>>=7;
		}
		data.appendByte(b); i++;
		return i;
	}
	
	public static int compactedLength(long a) {
		byte b=(byte)(a&127); // get bottom 7 bits
		a>>=7;
		int i=0;
		while ((((b&64)==0)&&(a!=0))||(((b&64)!=0)&&(a!=-1))) {
			b|=128;
			i++;
			b=(byte)(a&127);
			a>>=7;
		}
		i++;
		return i;		
	}
	
	
	public static long readCompacted(ByteBuffer bb) {
		long result=0;
		long b=0;
		int bits=0;
		do {
			b=bb.get();
			result|=(b&127)<<bits;
			bits+=7;
		} while ((b&128)!=0);
		// if ((bits<64)&&((result&(1L<<(bits-1)))!=0)) {
		if ((bits<64)) {
			// sign extend
			result=(result<<(64-bits))>>(64-bits);
		}
		return result;
	}
	
	public static long readCompacted(Data data, int offset) {
		long result=0;
		long b=0;
		int bits=0;
		do {
			b=data.get(offset++);
			result|=(b&127)<<bits;
			bits+=7;
		} while ((b&128)!=0);
		// if ((bits<64)&&((result&(1L<<(bits-1)))!=0)) {
		if ((bits<64)) {
			// sign extend
			result=(result<<(64-bits))>>(64-bits);
		}
		return result;
	}
	
	
	/**
	 * Writes an string of ASCII bytes, prefixed by the length of the string
	 * 
	 * @param bb
	 * @param src
	 * @return Number of bytes written
	 */
	public static int writeASCIIString(ByteBuffer bb, String src) {

		int startPos=bb.position();
		
		if (src!=null) {		
			int len=src.length();
			writeCompacted(bb,len);			
			for (int i=0; i<len; i++) {
				bb.put((byte)(src.charAt(i)));
			}
		} else {
			// send -1 for null string
			writeCompacted(bb,-1);	
		}
		return bb.position()-startPos;
	}
	
	public static int writeASCIIString(Data data, String src) {
		if (src!=null) {		
			int len=src.length();
			int hlen=writeCompacted(data,len);			
			for (int i=0; i<len; i++) {
				data.appendByte((byte)(src.charAt(i)));
			}
			return hlen+len;
		} else {
			// send -1 for null string
			writeCompacted(data,-1);	
			return 1;
		}
	}
	
	public static int readASCIIString(ByteBuffer bb, String[] dest) {
		int startPos=bb.position();
		int len=(int)readCompacted(bb);

		dest[0]=readASCIIStringChars(bb,len);
		
		return bb.position()-startPos;
	}
	
	public static int readASCIIString(Data data, int index, String[] dest) {
		long header=readCompacted(data,index);
		int len=(int)header;
		int hlen= compactedLength(header);
		
		dest[0]=readASCIIStringChars(data,index+hlen,len);
		
		return hlen+len;
	}
	
	public static String readASCIIString(ByteBuffer bb) {
		int len=(int)readCompacted(bb);
		return readASCIIStringChars(bb,len);
	}
	
	public static String readASCIIString(Data  data, int index) {
		long header=readCompacted(data,index);
		int len=(int)header;
		int hlen= compactedLength(header);
		return readASCIIStringChars(data,index+hlen,len);
	}
	
	public static String readASCIIStringChars(ByteBuffer bb, int numChars) {
		int len=numChars;
		if (len<0) return null;
		char[] readChars=new char[len];
		for (int i=0; i<len; i++) {
			readChars[i]=(char)(bb.get());
		}
		return new String(readChars);
	}
	
	public static String readASCIIStringChars(Data data, int index, int numChars) {
		int len=numChars;
		if (len<0) return null;
		char[] readChars=new char[len];
		for (int i=0; i<len; i++) {
			readChars[i]=(char)(data.getByte(index+i));
		}
		return new String(readChars);
	}
		
			
}
