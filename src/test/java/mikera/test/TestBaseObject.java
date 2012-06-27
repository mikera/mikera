package mikera.test;

import static org.junit.Assert.assertEquals;
import mikera.engine.BaseObject;

import org.junit.Test;

public class TestBaseObject {
	@Test public void testBaseObject() {
		BaseObject b=new BaseObject();
	
		assertEquals(null,b.get("A"));
		b.set("A", "AValue");
		b.set("B", "BValue");
		assertEquals("AValue",b.get("A"));
		assertEquals("BValue",b.get("B"));
		assertEquals(null,b.get("C"));
		assertEquals(null,b.getLocal("C"));
		
		BaseObject bb=new BaseObject(b);
		assertEquals("AValue",bb.get("A"));
		
		b.set("A", "AValue2");
		bb.set("B", "BValue2");
		assertEquals("AValue2",bb.get("A"));
		assertEquals("BValue2",bb.get("B"));
		assertEquals("AValue2",b.get("A"));
		assertEquals("BValue",b.get("B"));
		assertEquals(null,bb.getLocal("A"));
		
		bb.set("B", "BValue");
		
	}

	@Test public void testReset() {
		BaseObject a=new BaseObject();
		BaseObject b=new BaseObject(a);
		
		a.set("A", "AValue");
		assertEquals("AValue",a.get("A"));
		assertEquals("AValue",b.get("A"));
		
		b.set("A", null);
		assertEquals("AValue",a.get("A"));
		assertEquals(null,b.get("A"));
		assertEquals(true,b.containsLocalKey("A"));
		
		b.set("A","AValue");
		assertEquals("AValue",b.get("A"));
		assertEquals(false,b.containsLocalKey("A"));
		
	}
}
