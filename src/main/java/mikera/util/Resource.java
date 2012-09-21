package mikera.util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

/**
 * Utility functions for resource loading
 * 
 * Resources should be in the jar of the main class, and named as "resources/something.res"
 * 
 * @author Mike
 *
 */
public class Resource {
	public static URL getResource(String filename) {
		return Thread.currentThread().getContextClassLoader().getResource(filename);
	}
	
	public static InputStream getResourceAsStream(String filename) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
	}
	
	public static String getResourceAsString(String filename) throws FileNotFoundException {
		InputStream is=getResourceAsStream(filename);
		if (is==null) throw new FileNotFoundException(filename);
		return mikera.util.Tools.readStringFromStream(is);
	}
}
