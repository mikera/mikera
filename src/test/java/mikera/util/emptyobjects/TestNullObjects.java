package mikera.util.emptyobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import mikera.data.DataType;
import mikera.util.emptyobjects.NullArrays;
import mikera.util.emptyobjects.NullMap;

import org.junit.Test;

public class TestNullObjects {
	@SuppressWarnings("unchecked")
	@Test public void test1() {
		Map<String,Object> nm=(Map<String,Object>)NullMap.INSTANCE;
		
		assertEquals(null,nm.get("Hello"));
		
	}
	
	@Test public void testNullArrays() {
		for (DataType dt: DataType.values()) {
			Object test=NullArrays.getNullArray(dt);
			assertNotNull(test);
		}
	}
}
