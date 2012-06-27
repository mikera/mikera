package mikera.persistent;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

public class ASCIIString implements CharSequence, java.io.Serializable,
		Comparable<ASCIIString> {

	private final int count;
	private final byte[] value;
	private int hash = 0;

	public ASCIIString(String s) {
		this(s.length());
		for (int i = 0; i < count; i++) {
			char c = s.charAt(i);
			if (c == (c & 0xFF)) {
				value[i] = (byte) c;
			} else {
				throw new Error("Non-ANSI character value found: " + (int) c);
			}
		}
	}

	public ASCIIString(CharSequence s) {
		this(s.length());
		for (int i = 0; i < count; i++) {
			char c = s.charAt(i);
			if (c == (c & 0xFF)) {
				value[i] = (byte) c;
			} else {
				throw new Error("Non-ANSI character value found: " + (int) c);
			}
		}
	}

	public ASCIIString(ASCIIString s) {
		this(s.count);
		System.arraycopy(s.value, 0, value, 0, count);
	}

	public ASCIIString(ASCIIString a, ASCIIString b) {
		this(a.count + b.count);
		System.arraycopy(a.value, 0, value, 0, a.count);
		System.arraycopy(b.value, 0, value, a.count, b.count);
	}

	private ASCIIString(int newLength) {
		count = newLength;
		value = new byte[newLength];
	}

	@Override
	public char charAt(int position) {
		return (char) value[position];
	}

	@Override
	public int length() {
		return count;
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return substring(start, end);
	}

	public ASCIIString concat(ASCIIString str) {
		return new ASCIIString(this, str);
	}

	public int hashCode() {
		int h = hash;
		if (h == 0 && count > 0) {
			int off = 0;
			byte val[] = value;
			int len = count;

			for (int i = 0; i < len; i++) {
				h = 31 * h + (0xFF & val[off++]);
			}
			hash = h;
		}
		return h;
	}

	public ASCIIString replace(char oldChar, char newChar) {
		return replace((byte) oldChar, (byte) newChar);
	}

	public ASCIIString replace(byte oldByte, byte newByte) {
		int pos = 0;

		while ((pos < count) && (value[pos] != oldByte))
			pos++;

		if (pos < count) {
			ASCIIString s = new ASCIIString(this);
			s.value[pos] = newByte;
			for (int i = pos + 1; i < count; i++) {
				if (s.value[pos] == oldByte)
					s.value[pos] = newByte;
			}
			return s;
		} else {
			return this;
		}
	}

	public boolean matches(String regex) {
		return Pattern.matches(regex, toString());
	}

	public ASCIIString substring(int beginIndex) {
		return substring(beginIndex, count);
	}

	public boolean contains(CharSequence s) {
		return indexOf(s) >= 0;
	}

	public int indexOf(CharSequence s) {
		final int sl = s.length();
		for (int i = 0; i <= count - sl; i++) {
			for (int j = 0; j < sl; j++) {
				if (s.charAt(j) != charAt(i + j))
					break;
				if ((j + 1) == sl)
					return i;
			}
		}
		return -1;
	}

	public char[] toCharArray() {
		char result[] = new char[count];
		getChars(0, count, result, 0);
		return result;
	}
	
	public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
		int len=srcEnd-srcBegin;
		if (len<0) throw new IllegalArgumentException("Negative length: "+len);
		for (int i=0; i<len; i++) {
  			dst[dstBegin+i]=(char)(value[srcBegin+i]&0xFF);	
		}
	}
	
	public void getBytes(int srcBegin, int srcEnd, byte dst[], int dstBegin) {
		int len=srcEnd-srcBegin;
		System.arraycopy(value,srcBegin,dst,dstBegin,len);	
	}

	private ASCIIString substring(int beginIndex, int endIndex) {
		int newLength = endIndex - beginIndex;
		ASCIIString s = new ASCIIString(newLength);
		System.arraycopy(value, beginIndex, s.value, 0, newLength);
		return s;
	}

	@Override
	public String toString() {
		try {
			return new String(value, "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}
	}

	@Override
	public int compareTo(ASCIIString t) {
		if (t == this)
			return 0;
		int pos = 0;
		int len1 = count;
		int len2 = t.count;

		while (true) {
			if (pos >= len1) {
				if (pos >= len2) {
					return 0;
				} else {
					return 1;
				}
			} else if (pos >= len2) {
				return -1;
			}
			int c = this.value[pos] - t.value[pos];
			if (c != 0)
				return c;
			pos++;
		}
	}
}
