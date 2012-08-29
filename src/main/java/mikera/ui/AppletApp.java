package mikera.ui;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class AppletApp extends JApplet {

	public void init() {
		// do any applet-specific initialisation here
		getContentPane().add(new AppPanel());
	}
	
	private static final class AppPanel extends JPanel {
		public AppPanel() {
			// do any common initialisation here
			add(new JButton("Hello World!"));
		}
	}
	
	public static void main(String[] args) {
		// do any frame-specific initialisation here
		JFrame f=new JFrame("Frame");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		AppPanel appPanel=new AppPanel();
		
		f.getContentPane().add(appPanel);
		f.pack();
		f.setVisible(true);
	}
}
