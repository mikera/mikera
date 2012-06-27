
package mikera.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

/** 
* Class containing utility functions for text manipulations
*/
public class TextUtils {
    public static String NEWLINE = System.getProperty("line.separator");
    public static String EMPTY_STRING = "";


	/**
	 * return Roman numerals
	 * 
	 * note that beyond MMMCMXCIX = 3999 we can't show roman numerals in ascii
	 * since would require putting a line on top of V for 5000....
	 * 
	 */ 
	public static String roman(int n) {
		if (n<0) return "-"+roman(-n);
		if (n==0) return "nullus";
		
		String r = "";
		switch (n / 1000) {
			case 0 :
				break;
			case 1 :
				r += "M";
				break;
			case 2 :
				r += "MM";
				break;
			case 3 :
				r += "MMM";
				break;
			default:
				throw new Error("Number "+r+" too big to convert to roman numerals");
		}
		n = n % 1000;

		switch (n / 100) {
			case 1 :
				r += "C";
				break;
			case 2 :
				r += "CC";
				break;
			case 3 :
				r += "CCC";
				break;
			case 4 :
				r += "CD";
				break;
			case 5 :
				r += "D";
				break;
			case 6 :
				r += "DC";
				break;
			case 7 :
				r += "DCC";
				break;
			case 8 :
				r += "DCCC";
				break;
			case 9 :
				r += "CM";
				break;
		}
		n = n % 100;

		switch (n / 10) {
			case 1 :
				r += "X";
				break;
			case 2 :
				r += "XX";
				break;
			case 3 :
				r += "XXX";
				break;
			case 4 :
				r += "XL";
				break;
			case 5 :
				r += "L";
				break;
			case 6 :
				r += "LX";
				break;
			case 7 :
				r += "LXX";
				break;
			case 8 :
				r += "LXXX";
				break;
			case 9 :
				r += "XC";
				break;
		}
		n = n % 10;

		switch (n) {
			case 1 :
				r += "I";
				break;
			case 2 :
				r += "II";
				break;
			case 3 :
				r += "III";
				break;
			case 4 :
				r += "IV";
				break;
			case 5 :
				r += "V";
				break;
			case 6 :
				r += "VI";
				break;
			case 7 :
				r += "VII";
				break;
			case 8 :
				r += "VIII";
				break;
			case 9 :
				r += "IX";
				break;
		}

		return r;
	}

	public static String ordinal(int n) {
        String st=Integer.toString(n);
        if (((n % 100) >= 11) && ((n % 100) <= 13)) {
            return st + "th";
        } else if (n % 10 == 1) {
            return st + "st";
        } else if (n % 10 == 2) {
            return st + "nd";
        } else if (n % 10 == 3) {
            return st + "rd";
        } else {
            return st + "th";
        }
	}
	
	// return index of string s in array ss
	public static int index(String s, String[] ss) {
		for (int i = 0; i < ss.length; i++) {
			if (s.equals(ss[i]))
				return i;
		}
		return -1;
	}
	
	public static String arrayToString(float[] as) {
		StringBuilder sb=new StringBuilder('{');
		for (int i = 0; i < as.length; i++) {
			if (i>0) sb.append(", ");
			sb.append(Float.toString(as[i]));
		}
		sb.append("}");
		return sb.toString();
	}
	
	protected static char[] HEXCHARS={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	public static char toHexChar(int i) {
		i&=15;
		return (i<10)? (char)(i+48) : (char)(i+55);
	}
	
	public static String toHexString(int n) {
		char[] chars=new char[8];
		for (int i=0; i<8; i++) {
			chars[7-i]=toHexChar(n);
			n>>=4;
		};
		return new String(chars);
	}
	
 
	/**
	 * Soft HashMap containing whitespace strings of various lengths
	 */
	private static SoftHashMap<Integer,String> whiteSpaceStore=new SoftHashMap<Integer,String>();
	private static String whiteSpaceString="                                "; // initial length of 32
	
	public static String whiteSpace(int l) {
		if (l<0) throw new Error("Negative whitespace not possible");
		if (l==0) return "";
		
		String s=whiteSpaceStore.get(l);
		if (s!=null) return s;

		while (whiteSpaceString.length()<l) {
			whiteSpaceString=whiteSpaceString+whiteSpaceString;
		}

		s=whiteSpaceString.substring(0, l);
		whiteSpaceStore.put(l,s);
        return s;
	}
	
	public static String whiteSpace2(int l) {
		if (l==0) return "";
		String half=whiteSpace2(l/2);
		if ((l&1)!=0) {
			return half+" "+half;
		} else {
			return half+half;
		}
	}

	public static String leftPad(String s, int l) {
		if (s==null) s="";
		int spaces=l-s.length();
		if (spaces<0) throw new Error("String ["+s+"] too large to pad to length "+l);
		return whiteSpace(spaces)+s;
	}
	
	public static String leftPad(int v, int l) {
		return leftPad(Integer.toString(v),l);
	}        
	
	public static String rightPad(String s, int l) {
		if (s==null) s="";
		int spaces=l-s.length();
		if (spaces<0) throw new Error("String ["+s+"] too large to pad to length "+l);
		return s+whiteSpace(spaces);
	}
	
	// returns a+whitespace+b with total length len
	public static String centrePad(String a, String b, int len) {
		len = len - a.length();
		len = len - b.length();
		return a + whiteSpace(len) + b;
	}

	public static String capitalise(String s) {
		if (s==null) return null;
		if (s.length()==0) return "";
		char c = s.charAt(0);
		if (Character.isUpperCase(c))
			return s;
		StringBuilder sb = new StringBuilder(s);
		sb.setCharAt(0, Character.toUpperCase(c));
		return sb.toString();
	}

	public static String titleCase(String s) {
		StringBuilder sb = new StringBuilder(s);
		for (int i=0; i<s.length(); i++) {
			if ((i==0)||(!Character.isLetterOrDigit(s.charAt(i-1)))) {
				sb.setCharAt(i, Character.toUpperCase(s.charAt(i)));
			}
		}
		return sb.toString();
	}
	
	public static int countChar(String s, char c) {
		int count = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == c)
				count++;
		}
		return count;
	}

	public static int wrapLength(String s, int start, int len) {
		if ((s.length() - start) <= len)
			return (s.length() - start);

		for (int i = len; i >= 0; i--) {
			if (Character.isWhitespace(s.charAt(i + start)))
				return i;
		}

		return len;
	}

	public static String loadFromFile(String name) {
		StringBuilder sb=new StringBuilder();
	    try {
	    	InputStream in=String.class.getResourceAsStream(name);
	    	BufferedReader br=new BufferedReader(new InputStreamReader(in));
	    	
	    	for ( String s=br.readLine(); s!=null; s=br.readLine()) {
		        sb.append(s);
		        sb.append(NEWLINE);
		    }
	    	br.close();
	    } catch (Throwable t) {
        	t.printStackTrace();
        	return null;
	    }	    
		return sb.toString();
	}
	
	public static String[] wrapString(String s, int len) {
		String[] working = new String[1 + ((2 * s.length()) / len)];

		int end = s.length();
		int pos = 0;
		int i = 0;
		while (pos < end) {
			int inc = wrapLength(s, pos, len);
			working[i] = s.substring(pos, pos + inc);
			i++;
			pos = pos + inc;
			while ((pos < end) && Character.isWhitespace(s.charAt(pos)))
				pos++;
		}

		// return empty string if size zero
		if (i == 0) {
			i = 1;
			working[0] = "";
		}

		String[] result = new String[i];
		System.arraycopy(working, 0, result, 0, i);
		return result;
	}

	public static int encryptionHash(String s) {
		// mildly tricky encryption hash
		// can't be bothered with anything tougher yet
		int len = s.length();

		int a = 0;
		int f = 1;
		for (int i = len - 1; i >= 0; i--) {
			a += s.charAt(i) * f;
			f *= 31;
		}

		return a;
	}

	public static String[] separateString(String s, char c) {
		int num = countChar(s, c);
		int start = 0;
		int finish = 0;
		String[] result = new String[num + 1];
		for (int i = 0; i < (num + 1); i++) {
			finish = s.indexOf(c, start);
			if (finish < start)
				finish = s.length();
			if (start < finish) {
				result[i] = s.substring(start, finish);
			} else {
				result[i] = "";
			}
			start = finish + 1;
		}
		return result;
	}

	public static boolean isVowel(char c) {
		c = Character.toLowerCase(c);
		return ((c == 'a') || (c == 'e') || (c == 'i') || (c == 'o') || (c == 'u'));
    }

    /**
     * Convert a string with embedded spaces into proper camel notation.
     @param input a string to camelize.
     @return the newly camelized string.
    */
    public static String camelizeString(String input) {
        StringTokenizer tokenizer = new StringTokenizer(input," ");
        StringBuilder output = new StringBuilder();
        String token = null;
        while (tokenizer.hasMoreElements()) {
            token = tokenizer.nextToken();
            char first = token.charAt(0);
            if (Character.isLetter(first)) {
                output.append(Character.toUpperCase(first));
                output.append(token.substring(1));
            } else {
                output.append(token);
            }
        }
        return output.toString();
    }

    /**
     * Convert a string with embedded spaces into proper case notation.
     @param input a string to properCase.
     @return the newly properCased string.
    */    
    
    public static String properCase(String input) {
      StringTokenizer tokenizer = new StringTokenizer(input," ");
      StringBuilder output = new StringBuilder();
      String token = null;
      while (tokenizer.hasMoreElements()) {
          token = tokenizer.nextToken();
          char first = token.charAt(0);
          if (Character.isLetter(first)) {
              output.append(Character.toUpperCase(first));
              output.append(token.substring(1));
          } else {
              output.append(token);
          }
          if( tokenizer.hasMoreElements() ){
            output.append(" ");
          }
      }
      return output.toString();
  }
    
    
    
	/**
	 * Converts a string into the appropriate Object for Tyrant properties
	 * 
	 * @param s
	 * @return
	 */
    public static Object parseObject(String s) {
    	s=s.trim();
    	if (Character.isDigit(s.charAt(0))) {
    		try { 	
	    		if (s.indexOf(".")>=1) {
	    			return new Double(Double.parseDouble(s));
	    		} 
	    			
	    		return Integer.valueOf(Integer.parseInt(s));
	    		
	    	} catch (Throwable t) {
	    		// safe catch
	    	}
    	}
    	return s;
    }
    
    /**
     * Immutable class for wrapping a CharSequence when creating a subsequence
     * @author Mike
     *
     */
    public static final class SourceSubSequence implements CharSequence,Cloneable {
    	private CharSequence source;
    	private final int s_start;
    	private final int s_end;
    	
    	public SourceSubSequence(CharSequence cs, int start, int end) {
			if (start<0) throw new IndexOutOfBoundsException();
			if (end>cs.length()) throw new IndexOutOfBoundsException();
    		source=cs;
    		this.s_start=start;
    		this.s_end=end;
    	}
    	
		public char charAt(int index) {
			if (index<0) throw new IndexOutOfBoundsException();
			if (index>=length()) throw new IndexOutOfBoundsException();
			return source.charAt(index-s_start);
		}

		public int length() {
			return s_end-s_start;
		}

		public CharSequence subSequence(int start, int end) {
			int length=length();
			if ((start==0)&&(end==length)) return this;
			return new SourceSubSequence(source,start+s_start, end+s_start);
		}
		
		public SourceSubSequence clone() {
			return this;
		}
    }

    private static final DecimalFormat df=new DecimalFormat("#,#00.00");
    
	public static String decimalFormat(double number) {
		return df.format(number);
	}
	
    private static final DecimalFormat ff=new DecimalFormat(" 0000.00000;-0000.00000");
    
	public static String fixedFormat(double number) {
		return ff.format(number);
	}
	
    private static final DecimalFormat percentformat=new DecimalFormat("##0.00%");

	public static String percentage(double fc, double vnc) {
		double pc=fc/vnc;
		
		return percentformat.format(pc);
	}


}