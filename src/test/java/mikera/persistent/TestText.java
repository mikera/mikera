package mikera.persistent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import mikera.persistent.Text;
import mikera.util.Rand;
import mikera.util.TextUtils;

import org.junit.Test;

public class TestText {
	@Test public void testWhiteSpace() {
		assertEquals("   ",TextUtils.whiteSpace(3));
		assertEquals("   ",TextUtils.whiteSpace2(3));
		
		int n=Rand.d(10,10);
		assertEquals(n,TextUtils.whiteSpace(n).length());
		
		int n2=Rand.d(10,10);
		assertEquals(n2,TextUtils.whiteSpace2(n2).length());
	}
	
	@Test public void testRoman() {
		assertEquals("XXXIV",TextUtils.roman(34));
		
		assertEquals("-DCLXVI",TextUtils.roman(-666));
		
		assertEquals("MMMCMXCIX",TextUtils.roman(3999));
		
		assertEquals("nullus",TextUtils.roman(0));
	}

	@Test public void testHex() {
		assertEquals("00000000",TextUtils.toHexString(0));
		assertEquals("00010000",TextUtils.toHexString(65536));
		assertEquals("FFFFFFFF",TextUtils.toHexString(-1));
		assertEquals("00000A00",TextUtils.toHexString(2560));
	}
	
	@Test public void testText() {
		String st="My text";
		
		Text t1=Text.create("My text");
		Text t2=Text.create("My text");
		Text t3=Text.create(TextUtils.whiteSpace(1000));
		Text t4=Text.create(TextUtils.whiteSpace(1001));
		
		testTextObject(t1);
		testTextObject(t2);
		testTextObject(t3);
		testTextObject(t4);

		assertEquals(16,t4.countBlocks()); // low level blocks
		assertEquals(31,t4.countNodes()); // total nodes including tree
		
		for (int i=0; i<=1000; i++) {
			assertNotNull(t4.getBlock(i));
			assertTrue(t4.getBlockStartPosition(i)<=i);
		}
		
		assertEquals("y tex",t1.substring(1, 6));
		assertEquals(st,t1.toString());
		
		assertNull(t3.getBlock(-1));
		assertNull(t3.getBlock(1000));
		
		assertTrue(t1!=t2);
		assertEquals(0,t1.compareTo(t2));
		assertEquals(-1,t3.compareTo(t4));
		
		assertEquals(t1.hashCode(),t2.hashCode());
		assertTrue(t3.hashCode()!=t4.hashCode());
		assertTrue(t3.firstBlock().hashCode()==t4.firstBlock().hashCode());
		
		assertEquals(Text.concat(t1,t3),Text.concat(t2,t3));
		
		assertEquals(true,t1.isPacked());
		assertEquals(true,t4.isPacked());
	}

	@Test public void testTextCompare() {
		Text t1=Text.create("ABC");
		assertTrue(t1.compareTo("ABC")==0);
		assertTrue(t1.compareTo("ABCD")<0);
		assertTrue(t1.compareTo("AB")>0);
		assertTrue(t1.compareTo("ABJ")<0);
		assertTrue(t1.compareTo("ABA")>0);
	}
	
	@Test public void testTextOps() {
		Text t1=Text.create("");
		StringBuilder sb=new StringBuilder();
		
		for (int i=0; i<200; i++) {
			String s=Integer.toString(Rand.d(100));
			sb.append(s);
			t1=t1.append(s);
			
			if (Rand.d(30)==1) {
				int a=Rand.r(sb.length());
				int b=Rand.range(a, sb.length()-1);
				sb=new StringBuilder(sb.substring(a, b));
				t1=t1.subText(a, b);
			}
		}
		
		testTextObject(t1);
		
		assertEquals(t1.toString(),sb.toString());
		assertEquals(t1.hashCode(),Text.create(sb.toString()).hashCode());
		
		StringBuilder sb2=new StringBuilder();
		for (Character ch: t1) {
			sb2.append(ch);
		}
		assertEquals(sb.toString(),sb2.toString());
		
		
	}
	
	@Test public void testConcat() {
		Text t1=Text.create("AB");
		Text t2=Text.create("CD"); 
		
		assertEquals("ABCD",Text.concat(t1, t2).toString());
		assertEquals(t1,Text.concat(t1, Text.EMPTY_TEXT));
		assertEquals(t1,Text.concat(Text.EMPTY_TEXT,t1));
		
	}
	
	@Test public void testInsetr() {
		Text t1=Text.create("AB");
		Text t2=Text.create("CD");
		
		assertEquals("ACDB",t1.insert(1, t2).toString());		
	}
	
	@Test public void testStringEqualsCharSequence() {
		Text t=Text.create("hi").concat(Text.create("there"));
		assertTrue(t.toString().contentEquals(t));
	}
	
	public void testTextObject(Text t) {
		int len=t.length();
		
		assertTrue(len>=0);
		assertNull(t.getBlock(-1));
		assertNull(t.getBlock(len));
		if (len>0) {
			assertNotNull(t.getBlock(0));
			assertNotNull(t.getBlock(len-1));
		}
		t.isPacked();
		assertTrue(t.countNodes()>=t.countBlocks());
		assertEquals(len*2,Text.concat(t, t).length());
	}
}
