package mikera.web;

import java.net.URI;

public class WebUtils {
	public static void launchBrowser(String s) {
		try {
			java.awt.Desktop.getDesktop().browse(new URI(s));
		} catch (Throwable e) {
			throw new Error(e);
		}
	}
}
