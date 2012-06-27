package mikera.test;

import static org.junit.Assert.*;

import mikera.util.Resource;

import org.junit.Test;

public class TestResource {

	@Test
	public void testStringResource() {
		String s = Resource.getResourceAsString("test-resource/test.txt");
	    assertEquals("hello",s);
	}

}
