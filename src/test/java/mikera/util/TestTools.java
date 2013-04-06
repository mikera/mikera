package mikera.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import mikera.util.Arrays;
import mikera.util.Rand;
import mikera.util.Tools;

import org.junit.Test;

public class TestTools {
	@Test public void testSort() {
		Integer[] a=new Integer[100];
		Integer[] b=new Integer[100];
		
		for (int i=0; i<100; i++) {
			a[i]=Rand.d(100);
		}
		
		Arrays.mergeSort(a, b, 0, 99);
		
		assertTrue(Arrays.isSorted(a, 0, 99));
		
		Arrays.mergeSort(a);
		assertTrue(Arrays.isSorted(a, 0, 99));
	}
	
	@Test public void testSortEmpty() {
		Integer[] a=new Integer[0];
		Integer[] b=new Integer[0];
				
		Arrays.mergeSort(a, b, 0, 0);
		
		assertTrue(Arrays.isSorted(a, 0, 0));
		
		Arrays.mergeSort(a);
	}
	
	@Test public void testStringReadingAndWriting() {
		Charset cs=Charset.defaultCharset();
		String s1="Hello\r\nWorld\nThere\r  \n\r ";  
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Tools.writeStringToStream(out, s1, cs);
		String s2=Tools.readStringFromStream(new ByteArrayInputStream(out.toByteArray()),cs);
		assertEquals(s1,s2);
	}
	
	@Test public void testMapDifference() {
		HashMap<String,String> a=new HashMap<String, String>();
		HashMap<String,String> b=new HashMap<String, String>();
		
		a.put("1","a");
		a.put("2",null);
		a.put("3","new");
		a.put("4","added");

		b.put("1","a");
		b.put("2","b");
		b.put("3","old");
		b.put("5","deleted");
				
		Map<String,String> d=Tools.mapDifference(a, b);
		
		assertEquals(null,d.get("1"));
		assertTrue(!d.containsKey("1"));
		
		assertEquals(null,d.get("2"));
		assertTrue(d.containsKey("2"));

		assertEquals("new",d.get("3"));
		
		assertEquals("added",d.get("4"));
		
		assertEquals(null,d.get("5"));
		assertTrue(d.containsKey("5"));
	}
	
	@Test public void testCompares() {
		assertEquals(-1, Tools.compareWithNulls(null, 1));
		assertEquals(-1, Tools.compareWithNulls(1, 2));
		assertEquals(1, Tools.compareWithNulls(2, null));
		assertEquals(0, Tools.compareWithNulls(null, null));
		assertEquals(0, Tools.compareWithNulls(1, 1));
		
		assertEquals(0, Tools.compareWithNulls(1, 1));

	}
	
	@Test public void testEquals() {
		assertEquals(false, Tools.equalsWithNulls(null, 1));
		assertEquals(false, Tools.equalsWithNulls(1, 2));
		assertEquals(false, Tools.equalsWithNulls(2, null));
		assertEquals(true, Tools.equalsWithNulls(null, null));
		assertEquals(true, Tools.equalsWithNulls(1, 1));
	}
	
	@Test public void testDistinct() {
		assertEquals(true, Tools.distinctObjects());
		assertEquals(true, Tools.distinctObjects(new Boolean(true),new Boolean(true)));
		assertEquals(false, Tools.distinctObjects(true,true));
		assertEquals(false, Tools.distinctObjects("Hello","Hello"));
	}
}
