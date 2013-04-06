package mikera.util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import mikera.persistent.MapFactory;
import mikera.persistent.PersistentHashMap;

import org.w3c.dom.Document;

public final class Tools {
	
	public static class HashComparator<T> implements Comparator<T>, Serializable {
		private static final long serialVersionUID = -568440287836864164L;

		public int compare(T o1, T o2) {
			return o2.hashCode()-o1.hashCode();
		}
	}
	
	public static class DefaultComparator<T> implements Comparator<T>, Serializable {
		private static final long serialVersionUID = 1695713461396657889L;

		@SuppressWarnings("unchecked")
		public int compare(T o1, T o2) {
			return ((Comparable<T>)o1).compareTo(o2);
		}
	}
	
	public static void debugBreak(Object o) {
		o.toString();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int compareWithNulls(Object a, Object b) {
		if (a==b) {
			return 0;
		}
		if ((a==null)) return -1;
		if ((b==null)) return 1;
		return ((Comparable)a).compareTo(b);
	}
	
	public static final boolean equalsWithNulls(Object a, Object b) {
		if (a==b) return true;
		if ((a==null)||(b==null)) return false;
		return a.equals(b);
	}
	
	public static <T> ArrayList<T> buildArrayList(Iterator<T> iterator) {
		ArrayList<T> al=new ArrayList<T>();
		while (iterator.hasNext()) {
			al.add(iterator.next());
		}
		return al;
	}
	
	public static <T> HashSet<T> buildHashSet(Iterator<T> iterator) {
		HashSet<T> hs=new HashSet<T>();
		while (iterator.hasNext()) {
			hs.add(iterator.next());
		}
		return hs;
	}
	
	/**
	 * Hash code based on summed hash codes of individual integer values
	 * 
	 * Defined as XOR of hashcodes of all elements rotated right for each element, to be consistent with PersistentList<T>
	 * 
	 * @param data
	 * @return
	 */
	public static int hashCode(int[] data) {
		int result=0;
		for(int i=0; i<data.length; i++) {
			result^=hashCode(data[i]);
			result=Integer.rotateRight(result, 1);
		}
		return result;
	}
	
	public static <T> int hashCode(T[] data) {
		int result=0;
		for(int i=0; i<data.length; i++) {
			result^=data[i].hashCode();
			result=Integer.rotateRight(result, 1);
		}
		return result;
	}
	
	public static<T> int hashCode(Iterator<T> data) {
		int result=0;
		
		while(data.hasNext()) {
			result^=hashCodeWithNulls(data.next());
			result=Integer.rotateRight(result, 1);
		}
		return result;
	}
	
	/**
	 * Hashcode for an int, defined as the value of the int itself for consistency with java.lang.Integer
	 * 
	 * @param value
	 * @return
	 */
	public static int hashCode(int value) {
		return value;
	}
	
	public static int hashCodeWithNulls(Object value) {
		if (value==null) return 0;
		return value.hashCode();
	}
	
	/** 
	 * Hashcode for a double primitive
	 * 
	 * @param d
	 * @return
	 */
	public static int hashCode(double d) {
		return hashCode(Double.doubleToLongBits(d));
	}
	
	/**
	 * Hashcode for a long primitive
	 * @param l
	 * @return
	 */
	public static int hashCode(long l) {
		return (int) (l ^ (l >>> 32));
	}
	
	/**
	 * Compares two Comparable values, considering null as the lowest possible value 
	 * 
	 * @param <T>
	 * @param a
	 * @param b
	 * @return
	 */
	public static <T extends Comparable<? super T>> int compareWithNulls(T a, T b) {
		if (a==b) return 0;
		if (a==null) return -1;
		if (b==null) return 1;
		return a.compareTo(b);
	}
	
	public static void writeXMLToFile(Document doc, String fileName) {
		try {
			TransformerFactory factory=TransformerFactory.newInstance();
			factory.setAttribute("indent-number", 4);
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			Source source = new DOMSource(doc);
			Result result = new StreamResult(new OutputStreamWriter(new FileOutputStream(new File(fileName)),"UTF-8"));
			transformer.transform(source, result);
		} catch (Throwable t) {
			throw new Error(t);
		}
	}

	public static void writeStringToFile(File file, String s) {
		writeStringToFile(file, s, Charset.defaultCharset());
	}	
	
	public static void writeStringToFile(File file, String s, Charset cs) {
		try {
			OutputStream stream=null;
			try {
				stream = new FileOutputStream(file);
				writeStringToStream(stream, s, cs);
			} finally {
				stream.close();
			}
		} catch (Throwable t) {
			throw new Error(t);
		}
	}

	public static void writeStringToStream(OutputStream stream, String s) {
		writeStringToStream(stream,s,Charset.defaultCharset());
	}
	
	public static void writeStringToStream(OutputStream stream, String s, Charset cs) {
		try {
			Writer writer=null;
			try {
				writer=new BufferedWriter(new OutputStreamWriter(stream,cs));
				writer.write(s);
			} finally {
				if (writer!=null) writer.close();
			}
		} catch (Throwable t) {
			throw new Error(t);
		}
	}
	
	public static String readStringFromFile(File file) {
		return readStringFromFile(file,Charset.defaultCharset());
	}

	
	public static String readStringFromFile(File file, Charset cs) {
		try {
		    InputStream stream=null;		    
			try {
				stream=new FileInputStream(file);
		        return readStringFromStream(stream,cs);
			} finally {
				if (stream!=null) stream.close();
			}
		} catch (Throwable t) {
			throw new Error(t);
		}
	}

	public static String readStringFromStream(InputStream stream) {
		return readStringFromStream(stream,Charset.defaultCharset());
	}
	
	public static ArrayList<String> readStringLinesFromStream(InputStream stream) {
		ArrayList<String> al=new ArrayList<String>();
		BufferedReader reader=null;
		try {
			try {
				reader = new BufferedReader(new InputStreamReader(stream, Charset.defaultCharset()));
				String s;
				while ((s=reader.readLine())!=null) {
					al.add(s);
				}
			} finally {
		        if (reader!=null) reader.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    }  
		
		return al;
	}
	
	public static String readStringFromStream(InputStream stream, Charset cs) {
		try {
		    Reader reader=null;
			try {
		        reader = new BufferedReader(new InputStreamReader(stream, cs));
		        StringBuilder builder = new StringBuilder();
		        char[] buffer = new char[4096];
		        int readBytes;
		        while ((readBytes = reader.read(buffer, 0, buffer.length)) > 0) {
		            builder.append(buffer, 0, readBytes);
		        }
		        return builder.toString();
		    } finally {
		        if (reader!=null) reader.close();
		    }   
		} catch (Throwable t) {
			throw new Error(t);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <K,V> Map<K,V> mapDifference(Map<K,V> a, Map<K,V> b) {
		if (a==b) return (Map<K, V>) PersistentHashMap.EMPTY;
		
		PersistentHashMap<K,V> phm=(PersistentHashMap<K, V>) PersistentHashMap.EMPTY;
		for (Map.Entry<K, V> entry: a.entrySet()) {
			K key=entry.getKey();
			V av=entry.getValue();
			V bv=b.get(key);
			if (!equalsWithNulls(av,bv)) phm=phm.include(key, av);
		}
		
		for (Map.Entry<K, V> entry: b.entrySet()) {
			K key=entry.getKey();
			if ((!a.containsKey(key)) && (entry.getValue()!=null)) phm=phm.include(key, null);
		}

		return phm;
	}

	public static <K,V> boolean equalsMap(Map<K,V> a, Map<K,V> b) {
		return MapFactory.create(a).equals(MapFactory.create(b));
	}
	
	/**
	 * Factory method for Integer objects
	 */
	public static Integer integer(int i) {	
		// TODO: Consider larger cache
		return Integer.valueOf(i);	
	}

	public static <T> void reverse(ArrayList<T> al) {
		int size=al.size();
		int lastIndex=size-1;
		for (int i=0; i<(size>>1); i++) {
			Arrays.swap(al, i, lastIndex-i);
		}
	}

	public static boolean distinctObjects(Object... objects) {
		int n=objects.length;
		IdentityHashMap<Object, Integer> im=new IdentityHashMap<Object, Integer>();
		for (int i=0; i<n; i++) {
			im.put(objects[i], 1);
		}
		return im.size()==n;
	}
	
	public static boolean distinctObjects(List<?> objects) {
		int n=objects.size();
		IdentityHashMap<Object, Integer> im=new IdentityHashMap<Object, Integer>();
		for (int i=0; i<n; i++) {
			im.put(objects.get(i), 1);
		}
		return im.size()==n;
	}
}
