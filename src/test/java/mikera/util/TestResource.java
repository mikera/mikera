package mikera.util;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import mikera.util.Resource;

import org.junit.Test;

public class TestResource {

	@Test
	public void testStringResource() {
		String s;
		try {
			s = Resource.getResourceAsString("test-resource/test.txt");
		} catch (FileNotFoundException e) {
			throw new Error(e);
		}
	    assertEquals("hello",s);
	}
	
	@Test (expected = FileNotFoundException.class)
	public void testNotFoundResource() throws FileNotFoundException {
		String s;
		s = Resource.getResourceAsString("test-resource/non-existent-filename.txt");
	    assertEquals("hello",s);
	}

}
