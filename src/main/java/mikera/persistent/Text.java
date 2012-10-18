package mikera.persistent;

import java.io.ObjectStreamException;
import java.util.Iterator;

import mikera.annotations.Immutable;
import mikera.util.Bits;
import mikera.util.Maths;
import mikera.util.TextUtils;
import mikera.util.emptyobjects.NullArrays;

/**
 * Immutable char sequence implementation based on a tree 
 * 
 * Each node contains count and pre-computed hashcode
 * 
 * Initially creates packed blocks, i.e. all blocks full except from final block, although this is not
 * guaranteed to be maintained (especially with concatenation / substring operations)
 * 
 * Also tries to maintain balanced tree as far as possible
 * 
 * @author Mike
 *
 */
@Immutable
public final class Text extends PersistentObject implements CharSequence, Comparable<CharSequence>, Iterable<Character> {
	private static final long serialVersionUID = 5744895584967327995L;
	public static final int BLOCK_SIZE_BITS=6;
	public static final int BLOCK_SIZE=1<<BLOCK_SIZE_BITS;
	public static final Text EMPTY_TEXT=new Text(NullArrays.NULL_CHARS);
	
	private final char[] data;
	private final Text front;
	private final Text back;
	private final int count;
	private final int hashCode;
	
	public static Text create(String s) {
		return create(s,0,s.length());
	}
	
	/**
	 * Return a new Text instance
	 * 
	 * @param s String from which to source characters
	 * @param start Index of starting character (inclusive) within string
	 * @param end Index of ending character (exclusive) within string
	 * @return Newly created Text object
	 */
	public static Text create(String s, int start, int end) {
		int length=end-start;
		if (length==0) return Text.EMPTY_TEXT;
		if (length<=BLOCK_SIZE) {
			char[] chars=new char[length];
			s.getChars(start, end, chars, 0);
			return new Text(chars);
		}
		
		// need to create a Text with multiple blocks
		int mid=((start+end+(BLOCK_SIZE-1))>>(BLOCK_SIZE_BITS+1))<<(BLOCK_SIZE_BITS);
		return new Text(create(s,start, mid),create(s,mid, end));
	}

	private Text(Text f, Text b) {
		data=null;
		front=f;
		back=b;
		count=f.count+b.count;
		hashCode=calculateConcatenatedHash(f,b);
	}

	
	private Text(char[] charData) {
		data=charData;
		count=data.length;
		back=null;
		front=null;
		hashCode=calculateHash(0,charData);
	}
	
	public Text subText(int start, int end) {
		if ((start<0)||(end>count)) throw new IndexOutOfBoundsException();
		if (start==end) return Text.EMPTY_TEXT;
		if ((start==0)&&(end==count)) return this;
		if (data!=null) {
			int len=end-start;
			char[] ndata=new char[len];
			System.arraycopy(data, start, ndata, 0, len);
			return new Text(ndata);			
		}
		int frontCount=front.count;
		if (end<=frontCount) return front.subText(start,end);
		if (start>=frontCount) return back.subText(start-frontCount,end-frontCount);
		return concat(front.subText(start, frontCount),back.subText(0, end-frontCount));
	}
	
	public int countNodes() {
		if (data!=null) {
			// this is a leaf node
			return 1;
		}
		return 1+front.countNodes()+back.countNodes();
	}
	
	public int countBlocks() {
		if (data!=null) {
			// this is a leaf node
			return 1;
		}
		return front.countBlocks()+back.countBlocks();
	}
	
	/**
	 * Deletes a block of text
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public Text deleteRange(int start, int end) {
		if (start>=end) return this;
		if ((start<=0)&&(end>=count)) return Text.EMPTY_TEXT;
		if (start<=0) return subText(end,count);
		if (end>=count) return subText(0,start);
		return concat(subText(0,start),subText(end,count));
	}
	
	/**
	 * Concatenates two Text objects, balancing the tree as far as possible
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static Text concat(Text a, Text b) {
		int alen=a.length(); if (alen==0) return b;
		int blen=b.length(); if (blen==0) return a;
		
		if (alen+blen<BLOCK_SIZE) {
			char[] ndata=new char[alen+blen];
			a.getChars(0, alen, ndata, 0);
			b.getChars(0, blen, ndata, alen);
			return new Text(ndata);
		}
		
		if ((alen<(blen>>1))&&(b.data==null)) {
			return new Text(concat(a,b.front),b.back);
		} 
		
		if ((blen<(alen>>1))&&(b.data==null)) {
			return new Text(a.front,concat(a.back,b));	
		}
		
		return new Text(a,b);
	}
	
	public boolean isPacked() {
		return isFullyPacked(this,true);
	}
	
	public Text append(String s) {
		return concat(this,Text.create(s));
	}
	
	public Text concat(Text t) {
		return concat(this,t);
	}
	
	public Text insert(int index, Text t) {
		return concat(concat(subText(0,index),t),subText(index,count));
	}
	
	public Text insert(int index, String s) {
		return insert(index,Text.create(s));
	}
	
	private static boolean isFullyPacked(Text t, boolean end) {
		if (t.data!=null) {
			return (end)||(t.data.length==BLOCK_SIZE);
		}
		return isFullyPacked(t.front,false)&&(isFullyPacked(t.back,true));
	}
	
	public String substring(int start, int end) {
		if ((start<0)||(end>count)) throw new IndexOutOfBoundsException();
		if (data!=null) {
			return new String(data,start,end-start);
		}
		
		// construct string from large char array
		char[] chars=new char[end-start];
		getChars(start,end,chars,0);
		return new String(chars);
	}
	
	/**
	 * Gets characters into a given char[] buffer
	 * 
	 * @param srcBegin
	 * @param srcEnd
	 * @param dst
	 * @param dstBegin
	 */
	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
		if ((srcBegin<0)||(srcEnd>count)) throw new IndexOutOfBoundsException();
		if (srcEnd<=srcBegin) return;
		
		int pos=srcBegin;
		Text t=getBlock(pos);
		int tpos=getBlockStartPosition(pos);
		int tlen=t.length();
		
		int offset=dstBegin-srcBegin;
		while (pos<srcEnd) {
			dst[pos+offset]=t.data[pos-tpos];
			pos++;
			if (pos>=tpos+tlen) {
				t=getBlock(pos);
				tpos=getBlockStartPosition(pos);
				if(t!=null) tlen=t.length();	
			}
		}
	}
	
	/**
	 * Calculated hashcode based on rolled character values plus the length of the character array
	 * 
	 * @param initialHash
	 * @param data
	 * @return
	 */
	public static int calculateHash(int initialHash,char[] data) {
		int result=0;
		for (int i=0; i<data.length; i++) {
			result=Bits.rollLeft(result, 7) ^ (data[i]);
		}
		return result+data.length;
	}
	
	public static int calculateConcatenatedHash(Text front,Text back) {
		int frontCount=front.count;
		int backCount=back.count;
		int hc=front.hashCode()-frontCount;
		hc=Bits.rollLeft(hc, 7*back.length());
		hc=hc^(back.hashCode()-backCount);
		return hc+frontCount+backCount;
	}

	
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public boolean hasFastHashCode() {
		return true;
	}

	public char charAt(int index) {
		if ((index<0)||(index>count)) throw new IndexOutOfBoundsException();
		return charAtLocal(index);
	}
	
	private char charAtLocal(int index) {
		if (data!=null) {
			return data[index];
		}
		
		int fc=front.count;
		if (fc>index) {
			return front.charAtLocal(index);
		}
		return back.charAtLocal(index);
	}

	public int length() {
		return count;
	}
	
	public Text firstBlock() {
		Text t=this;
		while (t.data==null) {
			t=t.front;
		}
		return t;
	}
	
	public Text getBlock(int pos) {
		if ((pos<0)||(pos>=count)) return null;
		return getBlockLocal(this,pos);
	}
	
	public int getBlockStartPosition(int pos) {
		if ((pos<0)||(pos>count)) throw new IndexOutOfBoundsException();
		return getBlockStartPositionLocal(this,pos);
	}
	
	private static Text getBlockLocal(Text head, int pos) {
		while (head.data==null) {
			int frontCount=head.front.count;
			if (pos<frontCount) {
				head=head.front;
			} else {
				pos-=frontCount;
				head=head.back;
			}
		}
		return head;
	}
	
	private static int getBlockStartPositionLocal(Text head, int pos) {
		int result=0;
		while (head.data==null) {
			int frontCount=head.front.count;
			if (pos<frontCount) {
				head=head.front;
			} else {
				pos-=frontCount;
				result+=frontCount;
				head=head.back;
			}
		}
		return result;
	}

	public CharSequence subSequence(int start, int end) {
		return new TextUtils.SourceSubSequence(this, start, end);
	}
	
	public String toString() {
		return substring(0,count);
	}
	
	public Text clone() {
		return (Text)super.clone();
	}
	
	public boolean equals(Object o) {
		if (o instanceof Text) {
			Text t=(Text)o;
			if (hashCode!=t.hashCode) return false;
			return compareTo(t)==0;
		}
		return false;
	}

	public int compareTo(CharSequence cs) {
		if (cs instanceof Text) {
			return compareTo((Text)cs);
		}
		int size=Maths.min(length(), cs.length());
		for (int i=0; i<size; i++) {
			int c=(charAt(i)-cs.charAt(i));
			if (c!=0) return c;
		}
		return Maths.sign(length()-cs.length());
	}
	
	public int compareTo(Text t) {
		if (t==this) return 0;
		int pos=0;
		int s1=0;
		int s2=0;
		Text text1=this.getBlock(0);
		Text text2=t.getBlock(0);
		int len1=text1.length();
		int len2=text2.length();

		while (true) {
			if (text1==null) {
				return (text2==null)?0:-1;
			}
			if (text2==null) {
				return 1;
			}
					
			int c=text1.data[pos-s1]-text2.data[pos-s2];
			if (c!=0) return c;
			
			pos++; 
			if (pos-s1>=len1) {
				text1=this.getBlock(pos);
				if (text1!=null) len1=text1.length();
				s1=pos;
			}
			if (pos-s2>=len2) {
				text2=t.getBlock(pos);
				if (text2!=null) len2=text2.length();
				s2=pos;
			}
		}
	}

	private class TextIterator implements Iterator<Character> {
		private int pos=0;
		private Text block=getBlock(0);
		private int blockStart=0;
		
		public boolean hasNext() {
			return pos<count;
		}

		public Character next() {
			char c=block.data[pos-blockStart];
			pos++;
			if (pos>=blockStart+block.count) {
				block=getBlock(pos);
				blockStart=pos;
			}
			
			return Character.valueOf(c);
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	public Iterator<Character> iterator() {
		return new TextIterator();
	}
	
	private Object readResolve() throws ObjectStreamException {
		// needed for deserialisation to the correct static instance
		if (data.length==0) return EMPTY_TEXT;
		return this;
	}
}
